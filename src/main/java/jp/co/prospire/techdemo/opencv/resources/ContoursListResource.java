package jp.co.prospire.techdemo.opencv.resources;

import jp.co.prospire.techdemo.opencv.core.Contour;
import jp.co.prospire.techdemo.opencv.core.Contours;
import jp.co.prospire.techdemo.opencv.service.RedirectToAuthUrlView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.google.common.base.Charsets;
import com.googlecode.javacpp.Loader;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

@Path("/contoursList")
@Produces(MediaType.TEXT_HTML)
public class ContoursListResource
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicLong counter;

    private final int canvasWidth;
    private final int canvasHeight;
    private final int smoothness;
    
    private FileSystem defaultFileSystem;
    
    private final String haarcascadeFileName = "haarcascade_frontalface_default.xml";
    private String haarcascadeFileAbsolutePath;

    private java.nio.file.Path tempDirectoryPath;
    
    private final ArrayList<Contours> emptyList = new ArrayList<Contours>();

    public ContoursListResource(int canvasWidth, int canvasHeight, int smoothness)
    {
        this.counter = new AtomicLong();
        
        this.canvasWidth = canvasWidth; 
        this.canvasHeight = canvasHeight; 
        this.smoothness = smoothness; 
        logger.info(
                String.format(
                        "canvas size: %d x %d",
                        this.canvasWidth,
                        this.canvasHeight
                    )
            );

        this.defaultFileSystem = FileSystems.getDefault();
        
        InputStream haarcascadeInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(this.haarcascadeFileName);

        this.tempDirectoryPath = this.defaultFileSystem.getPath(
                ".",
                "Charavatar"
            );
        
        java.nio.file.Path haarcascadeFilePath = this.defaultFileSystem.getPath(
                this.tempDirectoryPath.toAbsolutePath().toString(),
                this.haarcascadeFileName
            );

        try
        {
            Files.createDirectory(tempDirectoryPath);

            logger.info(
                    "temporary directory: "
                            + this.tempDirectoryPath.toAbsolutePath().toString()
                );
            
            Files.copy(haarcascadeInputStream, haarcascadeFilePath);
        }
        catch (IOException e)
        {
            logger.warn("can't make directory: " + e.getMessage());
        }

        this.haarcascadeFileAbsolutePath = haarcascadeFilePath.toAbsolutePath().toString();
        logger.info("haar cascade file copied: " + this.haarcascadeFileAbsolutePath);
    }
    
    public enum LogLevel
    {
        ERROR,
        WARN,
        INFO,
        DEBUG
    }
    
    public void log(LogLevel loglevel, String formatString, Object... args)
    {
        long _counter = this.counter.get();

        Object[] _args = new Object[args.length + 1];
        _args[0] = _counter;
        for (int index = 0; index < args.length; index++)
        {
            _args[index + 1] = args[index];
        }

        String _formatString = String.format(
                "%d: " + formatString,
                _args
                );
        switch (loglevel)
        {
            case ERROR:
                logger.error(_formatString);
                break;
            case WARN:
                logger.warn(_formatString);
                break;
            case INFO:
                logger.info(_formatString);
                break;
            case DEBUG:
            default:
                logger.debug(_formatString);
        }
    }
    
    public IplImage getImage(InputStream inputStream) throws Exception
    {
        java.nio.file.Path temporaryFilePath = this.defaultFileSystem.getPath(
                this.tempDirectoryPath.toAbsolutePath().toString(),
                inputStream.hashCode() + Long.toString(System.currentTimeMillis())
            );
        String temporaryFilePathString = temporaryFilePath.toAbsolutePath().toString();

        Files.copy(inputStream, temporaryFilePath);

        log(LogLevel.INFO, "uploaded image to %s",
                temporaryFilePathString);
        
        IplImage uploadImage = cvLoadImage(
                temporaryFilePathString,
                CV_LOAD_IMAGE_ANYCOLOR
            );

        // read EXIF for considering rotation.
        File jpegFile = new File(temporaryFilePathString);
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
        int orientation = 1;
        orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        log(LogLevel.DEBUG, "camera orientation value: %d", orientation);

        int rawWidth = uploadImage.width();
        int rawHeight = uploadImage.height();

        IplImage returnImage = IplImage.create(
                rawWidth,
                rawHeight,
                IPL_DEPTH_8U,
                CV_LOAD_IMAGE_ANYCOLOR
            );;

        double rotateDegree = 0.0;
        CvPoint2D32f center = new CvPoint2D32f(uploadImage.width() / 2, uploadImage.height() / 2);
        CvMat rotationMat = cvCreateMat(2, 3, CV_32F);
        switch (orientation)
        {
            case 3:
                rotateDegree = 180.0;
                break;
            case 6:
                rotateDegree = 90.0;
                returnImage = IplImage.create(
                        rawHeight,
                        rawWidth,
                        uploadImage.depth(),
                        uploadImage.nChannels()
                    );
                break;
            case 8:
                rotateDegree = 270.0;
                returnImage = IplImage.create(
                        rawHeight,
                        rawWidth,
                        uploadImage.depth(),
                        uploadImage.nChannels()
                    );
                break;
        }
        CvMat affineMatrix = cv2DRotationMatrix(center, -rotateDegree, 1.0, rotationMat);
        cvWarpAffine(uploadImage, returnImage, affineMatrix);
        log(LogLevel.DEBUG, "camera orientation degree: %f", rotateDegree);

        return returnImage;
    }
    
    public IplImage getPrimaryFaceImage(IplImage rawImage) throws Exception
    {
        // get original size.
        double rawWidth = (double) rawImage.width();
        double rawHeight = (double) rawImage.height();
        log(LogLevel.INFO, "image size: %d x %d",
                        (int) rawWidth,
                        (int) rawHeight
                    );

        // get grayscale.
        IplImage grayImage = IplImage.create(
                (int) rawWidth,
                (int) rawHeight,
                IPL_DEPTH_8U,
                CV_LOAD_IMAGE_GRAYSCALE
            );
        cvCvtColor(rawImage, grayImage, CV_BGR2GRAY);

        // get smaller image for speedy analysis.
        double scaleRateForFaceDetection = rawWidth / 320;
        log(LogLevel.INFO, "scale rate for face detection: %f",
                        scaleRateForFaceDetection
                    );
        IplImage smallImage = IplImage.create(
                (int) (rawWidth / scaleRateForFaceDetection),
                (int) (rawHeight / scaleRateForFaceDetection),
                IPL_DEPTH_8U,
                CV_LOAD_IMAGE_GRAYSCALE
            );
        cvResize(grayImage, smallImage, CV_INTER_LINEAR);
        int resizedWidth = smallImage.width();
        int resizedHeight = smallImage.height();
        log(LogLevel.INFO, "resized image size: %d x %d",
                        resizedWidth,
                        resizedHeight
                    );

        // get equalized image for right analysis.
        IplImage equalizedImage = IplImage.create(
                resizedWidth,
                resizedHeight,
                IPL_DEPTH_8U,
                CV_LOAD_IMAGE_GRAYSCALE
            );
        cvEqualizeHist(smallImage, equalizedImage);

        // face detection.
        CvMemStorage faceStorage = CvMemStorage.create();
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
                cvLoad(
                        this.haarcascadeFileAbsolutePath
                        )
                );
        CvSeq faces = cvHaarDetectObjects(
                equalizedImage,
                cascade,
                faceStorage,
                1.1,
                3,
                CV_HAAR_DO_CANNY_PRUNING
            );
        cvClearMemStorage(faceStorage);
        
        // get the biggest face area.
        int totalFaceCount = faces.total();
        if (totalFaceCount <= 0)
        {
            throw new Exception("Found no face");
        }
        log(LogLevel.INFO, "Found %d face(s)",
                        totalFaceCount
                    );
        int selectedFaceIndex = 0;
        int maxFaceRectWidth = 0;
        int maxFaceRectHeight = 0;
        for (int faceIndex = 0; faceIndex < totalFaceCount; faceIndex++)
        {
            CvRect faceRect = new CvRect(cvGetSeqElem(faces, faceIndex));
            int faceRectWidth = faceRect.width();
            int faceRectHeight = faceRect.width();
            if (faceRectWidth * faceRectHeight > maxFaceRectWidth * maxFaceRectHeight)
            {
                selectedFaceIndex = faceIndex;
                maxFaceRectWidth = faceRectWidth;
                maxFaceRectHeight = faceRectHeight;
            }
        }
        log(LogLevel.INFO, "selected face:%d",
                        selectedFaceIndex
                    );
        
        // set the biggest face area to original image.
        CvRect primaryFaceRect = new CvRect(cvGetSeqElem(faces, selectedFaceIndex));
        int rectX = (int) (primaryFaceRect.x() * scaleRateForFaceDetection);
        int rectY = (int) (primaryFaceRect.y() * scaleRateForFaceDetection);
        int rectWidth = (int) (primaryFaceRect.width() * scaleRateForFaceDetection);
        int rectHeight = (int) (primaryFaceRect.height() * scaleRateForFaceDetection);
        CvRect scaledFaceRect = new CvRect(
                rectX,
                rectY,
                rectWidth,
                rectHeight
            );
        log(LogLevel.INFO, "x:%d, y:%d, width:%d, height:%d",
                        rectX,
                        rectY,
                        rectWidth,
                        rectHeight
                    );
        cvSetImageROI(grayImage, scaledFaceRect);

        // get face image.
        IplImage faceImage = cvCreateImage(
                new CvSize(rectWidth, rectHeight),
                grayImage.depth(),
                grayImage.nChannels()
            );
        cvCopy(grayImage, faceImage);
        
        return faceImage;
    }
    
    public CvSeq getContours(IplImage image, int thresholdLevel) throws Exception
    {
        CvSeq contours = new CvSeq();
        
        // get threshold image.
        IplImage thresholdImage = cvCreateImage(
                cvGetSize(image),
                image.depth(),
                image.nChannels()
            );
        cvThreshold(
                image,
                thresholdImage,
                thresholdLevel,
                255,
                CV_THRESH_BINARY
            );

        // find contours.
        CvMemStorage contourStorage = CvMemStorage.create();
        int totalContoursCount = cvFindContours(
                thresholdImage,
                contourStorage,
                contours,
                Loader.sizeof(CvContour.class),
                CV_RETR_LIST,
                //CV_CHAIN_APPROX_NONE
                //CV_CHAIN_APPROX_SIMPLE
                CV_CHAIN_APPROX_TC89_L1 
            );
        cvClearMemStorage(contourStorage);
        if (totalContoursCount <= 0)
        {
            throw new Exception("contours is null");
        }
        
        log(LogLevel.DEBUG, "threshold level:%d, contours size:%d",
                        thresholdLevel,
                        totalContoursCount
                    );
        
        return contours;
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RedirectToAuthUrlView getContoursList(
        @FormDataParam("file") final InputStream inputStream,
        @FormDataParam("file") final FormDataContentDisposition disposition
        )
    {
        this.counter.incrementAndGet();
        
        IplImage uploadedImage = null;
        try
        {
            uploadedImage = this.getImage(inputStream);
        }
        catch (Exception e)
        {
            log(LogLevel.WARN, "can't copy image: %s",
                    e.getMessage());
            return new RedirectToAuthUrlView(this.emptyList, Charsets.UTF_8);
        }
        
        IplImage primaryFaceImage = null;
        try
        {
            primaryFaceImage = this.getPrimaryFaceImage(uploadedImage);
        }
        catch (Exception e)
        {
            log(LogLevel.WARN, "can't get face image: %s",
                    e.getMessage());
            e.printStackTrace();
            return new RedirectToAuthUrlView(this.emptyList, Charsets.UTF_8);
        }

        // get scaling rate for canvas in UI.
        double scaleRateForCanvas = (double) primaryFaceImage.width() / (double) this.canvasWidth;
        log(LogLevel.INFO, "scale rate for canvas: %f",
                        scaleRateForCanvas
                    );

        // get contours of face image.
        ArrayList<Contours> contoursList = new ArrayList<Contours>(this.smoothness);
        for (int thresholdLevel = 0; thresholdLevel <= 255; thresholdLevel += 255 / this.smoothness)
        {
            CvSeq contours = null;
            try
            {
                contours = this.getContours(primaryFaceImage, thresholdLevel);
            }
            catch (Exception e)
            {
                log(LogLevel.DEBUG, "threshold level:%d, contours is null",
                        thresholdLevel
                    );
                continue;
            }

            // get contours from list.
            Contours contoursElement = new Contours(thresholdLevel);
            for (int contoursIndex = 0; contours != null;
                    contours = contours.h_next(), contoursIndex++)
            {
                // get contour from opencv contours.
                int totalPointsCount = contours.total();
                Contour contourElement = new Contour(contoursIndex);
                for (int pointsIndex = 0; pointsIndex < totalPointsCount; pointsIndex++)
                {
                    // get point from contour.
                    CvPoint point = new CvPoint(cvGetSeqElem(contours, pointsIndex));
                    int pointX = (int) (point.x() / scaleRateForCanvas);
                    int pointY = (int) (point.y() / scaleRateForCanvas);
                    contourElement.addPoint(pointX, pointY);
                    log(LogLevel.DEBUG, "%d: %d: %d: x:%d, y:%d",
                                    thresholdLevel,
                                    contoursIndex,
                                    pointsIndex,
                                    pointX,
                                    pointY
                                );
                }
                // set points to object.
                contoursElement.addContour(contourElement);
            }
            // set contours to object.
            contoursList.add(contoursElement);
        }

        return new RedirectToAuthUrlView(contoursList, Charsets.UTF_8);
    }
}