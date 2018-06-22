package com.umbocv.cachedatautil.data.remote;

import com.umbocv.cachedatautil.data.model.CameraGroup;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

// api for getting data from network
public interface RemoteWebService {

    @GET("/api/locations")
    Call<CameraGroup[]> getCameraResponse(@Header("Authorization") String authToken);
}
