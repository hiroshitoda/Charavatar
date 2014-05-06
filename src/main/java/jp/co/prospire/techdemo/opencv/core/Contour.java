package jp.co.prospire.techdemo.opencv.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contour
{
    private final int contoursIndex;
    private final ArrayList<HashMap<String, Integer>> points;
    
    public Contour(int contoursIndex)
    {
        this.contoursIndex = contoursIndex;
        this.points = new ArrayList<HashMap<String, Integer>>();
    }

    @JsonProperty
    public int getContoursIndex()
    {
        return contoursIndex;
    }

    @JsonProperty
    public ArrayList<HashMap<String, Integer>> getPoints()
    {
        return points;
    }

    @JsonProperty
    public void addPoint(int x, int y)
    {
        HashMap<String, Integer> newPoint = new HashMap<String, Integer>(2);
        newPoint.put("x", new Integer(x));
        newPoint.put("y", new Integer(y));
        points.add(newPoint);
    }
}