package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;

import com.umbocv.cachedatautil.data.model.Camera;

import java.util.List;

public interface CameraRepository {
    LiveData<List<Camera>> loadCameras();

    void saveCamera(Camera camera);

    void deleteCamera(Camera camera);
}
