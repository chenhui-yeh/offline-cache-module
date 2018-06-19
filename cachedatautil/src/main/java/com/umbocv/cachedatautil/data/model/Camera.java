package com.umbocv.cachedatautil.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.umbocv.cachedatautil.data.model.Camera.TABLE_NAME;

/** Camera class for networking and database*/
@Entity(tableName = TABLE_NAME )
public class Camera {
    public static final String TABLE_NAME = "cameras";

    @SerializedName("_id")
    @Expose
    @PrimaryKey
    @NonNull
    private String id;

    @SerializedName("jumboId")
    @Expose
    private String name;
    @SerializedName("name")
    @Expose
    private String jumboId;
    @SerializedName("serial")
    @Expose
    private String serial;

    public Camera(@NonNull String id, String name, String jumboId, String serial) {
        this.id = id;
        this.name = name;
        this.jumboId = jumboId;
        this.serial = serial;
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

    public String getJumboId() {
        return jumboId;
    }

    public void setJumboId(String jumboId) {
        this.jumboId = jumboId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}

