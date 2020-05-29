package com.example.motive_app.network.dto;

import java.util.ArrayList;

public class UploadVideoRequest {
    private ArrayList<UserIdRequest> userIds;
    private String fileName;
    private String fileUrl;
    private String fileSize;
    private String registerId;
    private String registerName;
    private String registerRelationship;
    private String thumbnailUrl;


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public void setRegisterRelationship(String registerRelationship) {
        this.registerRelationship = registerRelationship;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setUserIds(ArrayList<UserIdRequest> userIds) {
        this.userIds = userIds;
    }
}
