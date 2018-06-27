package com.umbocv.cachedatautil.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.umbocv.cachedatautil.data.model.CameraByLocation;

import java.util.List;

@Dao
public interface CameraByLocationDao extends UmboDao {

    @Query("SELECT * FROM camera_by_location")
    LiveData<List<CameraByLocation>> loadCameraByLocation ();

    @Query("SELECT * FROM camera_by_location WHERE id = :id")
    LiveData<CameraByLocation> loadCameraByLocationById (String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCameraByLocation (CameraByLocation... cameraByLocation);

    @Delete
    void deleteCameraByLocation (CameraByLocation cameraByLocation);
}
