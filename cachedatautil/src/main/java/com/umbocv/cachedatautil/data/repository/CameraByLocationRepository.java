package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;

import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraByLocation;

import java.util.List;

// for database operations for camera_group
public interface CameraByLocationRepository {

    LiveData<List<CameraByLocation>> loadCameraByLocation(String authToken);

    void deleteCameras (CameraByLocation cameraByLocation);

    void saveCameras(CameraByLocation cameraByLocation);

    LiveData<List<Camera>> loadCameras(String authToken);

    void saveCamera(Camera camera);

    void deleteCamera(Camera camera);
}
