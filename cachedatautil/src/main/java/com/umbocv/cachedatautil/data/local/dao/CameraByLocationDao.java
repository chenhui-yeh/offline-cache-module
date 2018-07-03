package com.umbocv.cachedatautil.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.umbocv.cachedatautil.data.model.CameraByLocation;

import java.util.List;

@Dao
public interface CameraByLocationDao extends UmboDao<CameraByLocation> {

    @Override
    @Query("SELECT * FROM camera_by_location")
    LiveData<List<CameraByLocation>> loadData();

    @Override
    @Query("SELECT * FROM camera_by_location WHERE id = :id")
    LiveData<CameraByLocation> loadDataById(String id);

    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveData(CameraByLocation... cameraByLocation);

    @Override
    @Delete
    void deleteData(CameraByLocation cameraByLocation);
}
