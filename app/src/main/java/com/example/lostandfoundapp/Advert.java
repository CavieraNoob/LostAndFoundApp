package com.example.lostandfoundapp;

public class Advert {

    private int id;
    private String type;
    private String name;
    private String phone;
    private String description;
    private String date;
    private String location;
    private String category;
    private String imageUri;
    private String timestamp;

    public Advert() {
    }

    public Advert(int id, String type, String name, String phone,
                  String description, String date, String location,
                  String category, String imageUri, String timestamp) {

        this.id = id;
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.category = category;
        this.imageUri = imageUri;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}