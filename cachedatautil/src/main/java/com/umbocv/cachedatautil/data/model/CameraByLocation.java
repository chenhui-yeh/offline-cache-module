package com.umbocv.cachedatautil.data.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import static com.umbocv.cachedatautil.data.model.CameraByLocation.TABLE_NAME;

/** CameraByLocationDao class for database and network*/
@Entity(tableName = TABLE_NAME)
public class CameraByLocation {
    public static final String TABLE_NAME = "camera_by_location";

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("customerId")
    @Expose
    @Ignore
//    @Embedded(prefix = "customerId")
    private CustomerId customerId;

    @SerializedName("cameras")
    @Expose
    @Ignore
    private Camera[] cameras = null;

    @SerializedName("timezone")
    @Expose
    private String timezone;

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

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public Camera[] getCameras() {
        return cameras;
    }

    public void setCameras(Camera[] cameras) {
        this.cameras = cameras;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public CameraByLocation(@NonNull String id, String name, String timezone) {
        this.id = id;
        this.name = name;
        this.timezone = timezone;
    }

}