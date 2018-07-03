package com.umbocv.cachedatautil.data.remote;

import com.umbocv.cachedatautil.data.model.CameraByLocation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

// api for getting data from network
public interface UmboApi {
    @GET("/api/locations")
    Call<CameraByLocation[]> getCameraResponse(@Header("Authorization") String authToken);
}
