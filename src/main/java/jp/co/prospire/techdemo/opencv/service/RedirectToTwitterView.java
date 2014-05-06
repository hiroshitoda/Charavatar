package jp.co.prospire.techdemo.opencv.service;

import java.nio.charset.Charset;

import io.dropwizard.views.View;

public class RedirectToTwitterView extends View
{
    public final String authUrl;
    
    public RedirectToTwitterView(String authUrl, Charset charset)
    {
        super("RedirectToTwitter.ftl", charset);
        this.authUrl = authUrl;
    }
    
    public String getAuthUrl()
    {
        return this.authUrl;
    }
}