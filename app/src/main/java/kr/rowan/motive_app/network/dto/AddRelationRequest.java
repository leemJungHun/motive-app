package kr.rowan.motive_app.network.dto;

import java.util.ArrayList;

public class AddRelationRequest {
    private String id;
    private ArrayList<RelationItem> relations;

    public void setId(String id) {
        this.id = id;
    }

    public void setRelations(ArrayList<RelationItem> relations) {
        this.relations = relations;
    }
}
