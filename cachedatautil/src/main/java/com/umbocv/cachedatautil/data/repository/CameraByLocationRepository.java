package com.umbocv.cachedatautil.data.repository;

import com.umbocv.cachedatautil.data.local.UmboDao;
import com.umbocv.cachedatautil.data.model.UmboObject;

import java.util.List;

// for database operations for camera_group
public class CameraByLocationRepository implements UmboRepository{

    private UmboDao cameraByLocationDao;

    @Override
    public List<UmboObject> loadData(String authToken) {
        return null;
    }

    @Override
    public UmboObject loadSingleData(String authToken) {
        return null;
    }

    @Override
    public void saveData(UmboObject... objects) {

    }

    @Override
    public void deleteData(UmboObject object) {

    }
}
