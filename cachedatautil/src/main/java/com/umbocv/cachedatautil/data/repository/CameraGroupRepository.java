package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;

import com.umbocv.cachedatautil.data.model.CameraGroup;

import java.util.List;

public interface CameraGroupRepository {

    LiveData<List<CameraGroup>> loadCameraGroups(String authToken);

    void deleteCameraGroup(CameraGroup cameraGroup);

    void saveCameraGroup(CameraGroup cameraGroup);
}
