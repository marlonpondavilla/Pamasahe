package com.example.pamasahe.classes;

public class Vehicle {
    private String from;
    private String to;
    private String duration;
    private String price;
    private int imageResId;

    public Vehicle(String from, String to, String duration, String price, int imageResId) {
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDuration() {
        return duration;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
