package com.umbocv.cachedatautil.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.umbocv.cachedatautil.data.model.Camera;

import java.util.List;

@Dao
public interface CameraDao extends UmboDao{
//    @Query("SELECT * from cameras")
//    LiveData<List<Camera>> loadCameras();
//
//    @Query("SELECT * from cameras WHERE groupId = :groupId")
//    LiveData<List<Camera>> loadCamerasByGroup (String groupId);
//
//    @Query("SELECT * FROM cameras WHERE id = :id")
//    LiveData<Camera> loadCameraById(String id);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void saveCamera(Camera...  camera);
//
//    @Delete
//    void deleteCamera(Camera camera);
}
