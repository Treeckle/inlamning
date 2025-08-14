package se.su.inlupp;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    double xPos;
    double yPos;
    public Location(String name, double x, double y) {
        this.name = name;
        xPos = x;
        yPos = y;
    }
    public double getXPos() {return xPos;}
    public double getYPos() {return yPos;}
    public String toString() {return name;}
}
