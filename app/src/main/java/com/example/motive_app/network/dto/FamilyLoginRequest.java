package com.example.motive_app.network.dto;

public class FamilyLoginRequest {

    private String familyId;
    private String pswd;

    public FamilyLoginRequest(String familyId, String pswd) {
        this.familyId = familyId;
        this.pswd = pswd;
    }
}