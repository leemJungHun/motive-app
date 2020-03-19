package com.example.motive_app.network.DTO;

import java.util.List;

public class FamilyRegistrationRequest {
    private String id;
    private String name;
    private String pswd;
    private String phone;
    private String email;
    private List<RelationItem> relations;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRelations(List<RelationItem> relations) {
        this.relations = relations;
    }
}
