package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;

import com.umbocv.cachedatautil.data.model.CameraGroup;

import java.util.List;

// for database operations for camera_group
public interface CameraGroupRepository {

    LiveData<List<CameraGroup>> loadCameraGroups(String authToken);

    void deleteCameraGroup(CameraGroup cameraGroup);

    void saveCameraGroup(CameraGroup cameraGroup);
}
