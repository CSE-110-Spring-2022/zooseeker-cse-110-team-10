package com.example.zooseeker10;

public class DirectionsItem {
    public String from;
    public String to;
    public String street;
    public double dist;

    public DirectionsItem(String from, String to, String street, double dist){
        this.from=from;
        this.to=to;
        this.street=street;
        this.dist=dist;
    }
}
