package com.umbocv.cachedatautil.data.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// not used yet
public class CustomerId extends UmboObject{

    @SerializedName("_id")
    @Expose
    @NonNull
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public CustomerId(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
