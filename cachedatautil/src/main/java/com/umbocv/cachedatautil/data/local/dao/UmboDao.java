package com.umbocv.cachedatautil.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;

import com.umbocv.cachedatautil.data.model.UmboObject;

import java.util.List;

@Dao
public interface UmboDao <T extends UmboObject> {

    LiveData<List<T>> loadData();

    LiveData<T> loadDataById(String id);

    void saveData(T... t);

    void deleteData(T t);

}
