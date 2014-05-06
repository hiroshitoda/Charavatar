package jp.co.prospire.techdemo.opencv.core;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contours
{
    private final int thresholdLevel;
    private final ArrayList<Contour> contours;
    
    public Contours(int thresholdLevel)
    {
        this.thresholdLevel = thresholdLevel;
        this.contours = new ArrayList<Contour>();
    }

    @JsonProperty
    public int getThresholdLevel()
    {
        return thresholdLevel;
    }

    @JsonProperty
    public ArrayList<Contour> getContours()
    {
        return contours;
    }

    @JsonProperty
    public void addContour(Contour contour)
    {
        contours.add(contour);
    }
}