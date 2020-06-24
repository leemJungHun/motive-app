package com.example.motive_app.network.dto;

public class RemoveUserFamilyRelationRequest {
    private String userId;
    private String familyId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
}
