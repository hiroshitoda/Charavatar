package jp.co.prospire.techdemo.opencv.service;

import java.nio.charset.Charset;
import java.util.HashMap;

import com.google.gson.Gson;

import io.dropwizard.views.View;

public class SVGCanvasView extends View
{
    public final HashMap<String, Integer> tweetWords;
    public final String json;
    
    public SVGCanvasView(HashMap<String, Integer> tweetWords, Charset charset)
    {
        super("SVGCanvas.ftl", charset);
        this.tweetWords = tweetWords;

        Gson gson = new Gson();
        this.json = gson.toJson(tweetWords);
    }
    
    public String getJson()
    {
        return this.json;
    }
}
