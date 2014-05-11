package jp.co.prospire.techdemo.opencv.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.co.prospire.techdemo.opencv.dao.EncodedImageDAO;
import jp.co.prospire.techdemo.opencv.service.SVGImageView;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Charsets;
import com.sun.jersey.core.util.Base64;

@Path("/share")
@Produces(MediaType.TEXT_HTML)
public class EncodedImageResource
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicLong counter;
    
    private final EncodedImageDAO encodedImageDAO;
    
    private FileSystem defaultFileSystem;
    
    private final String tempDirectoryPathString;
    private java.nio.file.Path tempDirectoryPath;
    
    private final String emptyImage = "";
    
    public EncodedImageResource(EncodedImageDAO encodedImageDAO, String tempDirectoryPathString)
    {
        this.counter = new AtomicLong();
        this.encodedImageDAO = encodedImageDAO;
        this.tempDirectoryPathString = tempDirectoryPathString;

        this.defaultFileSystem = FileSystems.getDefault();
        
        this.tempDirectoryPath = this.defaultFileSystem.getPath(
                this.tempDirectoryPathString,
                "Charavatar"
            );

        try
        {
            Files.createDirectory(tempDirectoryPath);

            logger.info(
                    "temporary directory: "
                            + this.tempDirectoryPath.toAbsolutePath().toString()
                );
        }
        catch (IOException e)
        {
            logger.warn("can't make directory. maybe dupulicated: " + e.getMessage());
        }
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

    @POST
    @Timed
    public SVGImageView registerImage(
            @FormParam("svg") String svgString
        )
    {
        this.counter.incrementAndGet();

        if (svgString.length() <= 0)
        {
            log(LogLevel.ERROR, "no svg.");
            return new SVGImageView(emptyImage, Charsets.UTF_8);
        }
        
        log(LogLevel.DEBUG, "svg:%s", svgString);

        // convert SVG to PNG.
        SVGConverter converter = new SVGConverter();
        converter.setDestinationType(DestinationType.PNG);

        java.nio.file.Path sourceFilePath = this.defaultFileSystem.getPath(
                this.tempDirectoryPath.toAbsolutePath().toString(),
                svgString.hashCode() + Long.toString(System.currentTimeMillis()) + "src.svg"
            );
        String sourceFileUrlString = sourceFilePath.toUri().toString();
        try
        {
            Files.write(sourceFilePath, svgString.getBytes());
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "temporary SVG write error:%s", e.getMessage());
            return new SVGImageView(emptyImage, Charsets.UTF_8);
        }
        converter.setSources(new String[]{ sourceFileUrlString });

        java.nio.file.Path destinationFilePath = this.defaultFileSystem.getPath(
                this.tempDirectoryPath.toAbsolutePath().toString(),
                svgString.hashCode() + Long.toString(System.currentTimeMillis()) + "dst.png"
            );
        String destinationFilePathString = destinationFilePath.toAbsolutePath().toString();
        converter.setDst(new File(destinationFilePathString));
        
        try
        {
            converter.execute();
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "SVG -> PNG convert error:%s", e.getMessage());
            return new SVGImageView(emptyImage, Charsets.UTF_8);
        }
        
        byte[] pngImage = null;
        try
        {
            pngImage = Files.readAllBytes(destinationFilePath);
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "PNG read error:%s", e.getMessage());
            return new SVGImageView(emptyImage, Charsets.UTF_8);
        }
        String encodedImage = new String(Base64.encode(pngImage), Charsets.UTF_8);
        log(LogLevel.DEBUG, "BASE64 encoded:%s", encodedImage);
        
        // insert new record to DB.
        long newId = 1;
        String dupulicateEncodedImage = this.encodedImageDAO.getEncodedImageById(newId);
        while(
                dupulicateEncodedImage != null
                && dupulicateEncodedImage.length() > 0
            )
        {
            log(LogLevel.DEBUG, "dupulicate ID:%d", newId);
            newId = Math.abs(new Random().nextLong());
            dupulicateEncodedImage = this.encodedImageDAO.getEncodedImageById(newId);
        }
        log(LogLevel.DEBUG, "new ID:%d", newId);

        String newShortUrl = Long.toString(newId, Character.MAX_RADIX);
        log(LogLevel.DEBUG, "new short URL:%s", newShortUrl);
        
        Timestamp createDate = new java.sql.Timestamp(System.currentTimeMillis()); 
        
        this.encodedImageDAO.insert(newId, newShortUrl, encodedImage, createDate);
        
        log(LogLevel.INFO, "new data. ID:%d, URL:%s", newId, newShortUrl);
        
        return this.getImage(newShortUrl);
    }
    
    @GET
    @Timed
    @Path("/{shortUrl}")
    public SVGImageView getImage(
            @PathParam("shortUrl") String shortUrl
        )
    {
        this.counter.incrementAndGet();

        if (shortUrl.length() <= 0)
        {
            log(LogLevel.ERROR, "no short URL.");
            return new SVGImageView(emptyImage, Charsets.UTF_8);
        }

        log(LogLevel.DEBUG, "short URL access:%s", shortUrl);
        
        long id = this.encodedImageDAO.getIdByShortUrl(shortUrl);
        String encodedImage = this.encodedImageDAO.getEncodedImageById(id);
        
        return new SVGImageView(encodedImage, Charsets.UTF_8);
    }
}
