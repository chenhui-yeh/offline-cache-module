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
public interface CameraDao extends UmboDao<Camera> {

    @Override
    @Query("SELECT * FROM cameras WHERE id = :id")
    LiveData<Camera> loadDataById(String id);

    @Override
    @Query("SELECT * FROM cameras")
    LiveData<List<Camera>> loadData();

    @Override
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void saveData(Camera... camera);

    @Override
    @Delete
    void deleteData(Camera camera);

    @Query("SELECT * from cameras WHERE locationId = :groupId")
    LiveData<List<Camera>> loadCamerasByGroup (String groupId);
}
