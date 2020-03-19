package com.example.motive_app.network.DTO;

public class PutProfileImageRequest {
    private String id;
    private String imageUrl;
    private String type;

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setType(String type) {
        this.type = type;
    }
}
