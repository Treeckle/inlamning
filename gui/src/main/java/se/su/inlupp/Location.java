package se.su.inlupp;

public class Location {
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
