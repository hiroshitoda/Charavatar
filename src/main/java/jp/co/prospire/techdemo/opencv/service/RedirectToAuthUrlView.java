package jp.co.prospire.techdemo.opencv.service;

import java.nio.charset.Charset;
import java.util.ArrayList;

import com.google.gson.Gson;

import jp.co.prospire.techdemo.opencv.core.Contours;

import io.dropwizard.views.View;

public class RedirectToAuthUrlView extends View
{
    public final ArrayList<Contours> contoursList;
    public final String json;
    
    public RedirectToAuthUrlView(ArrayList<Contours> contoursList, Charset charset)
    {
        super("RedirectToAuthUrl.ftl", charset);
        this.contoursList = contoursList;

        Gson gson = new Gson();
        this.json = gson.toJson(contoursList);
    }
    
    public String getJson()
    {
        return this.json;
    }
}