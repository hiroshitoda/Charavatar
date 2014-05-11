package jp.co.prospire.techdemo.opencv.service;

import java.nio.charset.Charset;

import io.dropwizard.views.View;

public class SVGImageView extends View
{
    private final String base64EncodedImage;

    public SVGImageView(String base64EncodedImage, Charset charset)
    {
        super("SVGImage.ftl", charset);
        this.base64EncodedImage = base64EncodedImage;
    }

    public String getBase64EncodedImage()
    {
        return this.base64EncodedImage;
    }
}
