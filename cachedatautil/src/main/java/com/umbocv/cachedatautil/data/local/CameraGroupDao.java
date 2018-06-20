package com.umbocv.cachedatautil.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.umbocv.cachedatautil.data.model.CameraGroup;

import java.util.List;

@Dao
public interface CameraGroupDao {

    @Query("SELECT * FROM camera_groups")
    LiveData<List<CameraGroup>> loadCameraGroups();

    @Query("SELECT * FROM camera_groups WHERE id = :id")
    LiveData<CameraGroup> loadCameraGroupById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCameraGroup(CameraGroup... cameraGroup);

    @Delete
    void deleteCameraGroup(CameraGroup cameraGroup);
}
