package com.umbocv.cachedatautil.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// not used yet
public class CustomerId {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
