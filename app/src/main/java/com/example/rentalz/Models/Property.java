package com.example.rentalz.Models;

public class Property {

    private int id;
    private String propertyType;
    private String bedrooms;
    private String date;
    private String time;
    private String monthlyRentPrice;
    private String furnitureType;
    private String notes;
    private String reporterName;
    private byte[] image;

    public Property(int id, String propertyType, String bedrooms, String date, String time, String monthlyRentPrice, String furnitureType, String notes, String reporterName, byte[] image) {
        this.id = id;
        this.propertyType = propertyType;
        this.bedrooms = bedrooms;
        this.date = date;
        this.time = time;
        this.monthlyRentPrice = monthlyRentPrice;
        this.furnitureType = furnitureType;
        this.notes = notes;
        this.reporterName = reporterName;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMonthlyRentPrice() {
        return monthlyRentPrice;
    }

    public void setMonthlyRentPrice(String monthlyRentPrice) {
        this.monthlyRentPrice = monthlyRentPrice;
    }


    public String getFurnitureType() {
        return furnitureType;
    }

    public void setFurnitureType(String furnitureType) {
        this.furnitureType = furnitureType;
    }
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
