package com.example.pamasahe.classes;

public class RideConfirmation {
    private String id;
    private String from;
    private String to;
    private String duration;
    private String originalPrice;
    private String finalPrice;
    private String discountType;
    private String dateTime; // New field for storing date and time

    public RideConfirmation() {
        // Default constructor for Firebase
    }

    // Updated constructor with all parameters (7 parameters)
    public RideConfirmation(String from, String to, String duration, String originalPrice,
                            String finalPrice, String discountType, String dateTime) {
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.discountType = discountType;
        this.dateTime = dateTime;
    }

    // Getter and setter for the new dateTime field
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // Other getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
}
