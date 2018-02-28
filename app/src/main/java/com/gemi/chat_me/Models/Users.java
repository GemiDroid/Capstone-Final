package com.gemi.chat_me.Models;

public class Users {

    String image;
    String name;
    String status;
    Long online;

    public Users() {

    }

    public Users(String image, String name, String status, Long online) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.online = online;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOnline() {
        return online;
    }

    public void setOnline(Long online) {
        this.online = online;
    }
}