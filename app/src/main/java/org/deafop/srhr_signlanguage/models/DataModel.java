package org.deafop.srhr_signlanguage.models;


public class DataModel {
    int id_;
    int image;
    String name;

    public DataModel(String str, int i, int i2) {
        this.name = str;
        this.id_ = i;
        this.image = i2;
    }

    public String getName() {
        return this.name;
    }

    public int getImage() {
        return this.image;
    }

    public int getId() {
        return this.id_;
    }
}
