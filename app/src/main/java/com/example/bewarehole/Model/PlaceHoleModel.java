package com.example.bewarehole.Model;

public class PlaceHoleModel {
    String id;
    String userid;
    double latitudelocation;
    double longitudelocation;
    String titlekind;
    String snippetdesciption;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getLatitudelocation() {
        return latitudelocation;
    }

    public void setLatitudelocation(double latitudelocation) {
        this.latitudelocation = latitudelocation;
    }

    public double getLongitudelocation() {
        return longitudelocation;
    }

    public void setLongitudelocation(double longitudelocation) {
        this.longitudelocation = longitudelocation;
    }

    public String getTitlekind() {
        return titlekind;
    }

    public void setTitlekind(String titlekind) {
        this.titlekind = titlekind;
    }

    public String getSnippetdesciption() {
        return snippetdesciption;
    }

    public void setSnippetdesciption(String snippetdesciption) {
        this.snippetdesciption = snippetdesciption;
    }
}
