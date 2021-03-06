package jp.co.prospire.techdemo.opencv.service;

import java.nio.charset.Charset;

import io.dropwizard.views.View;

public class SVGImageView extends View
{
    private final String base64EncodedImage;
    private final String shortUrl;

    public SVGImageView(String base64EncodedImage, String shortUrl, Charset charset)
    {
        super("SVGImage.ftl", charset);
        this.base64EncodedImage = base64EncodedImage;
        this.shortUrl = shortUrl;
    }

    public String getBase64EncodedImage()
    {
        return this.base64EncodedImage;
    }

    public String getShortUrl()
    {
        return this.shortUrl;
    }
}