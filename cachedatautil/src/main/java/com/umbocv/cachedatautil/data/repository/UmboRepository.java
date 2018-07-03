package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;

import com.umbocv.cachedatautil.data.model.UmboObject;

import java.util.List;

/**
 * Base repository
 * @param <T> The data type to be manipulated by the repository
 */
public interface UmboRepository<T extends UmboObject> {

    /**
     * initializes the data in database
     * @param authToken token needed to perform network call from UmboApi
     */
    void initializeData(String authToken);

    /**
     * loads data from network if network connection is available
     * else loads data from database
     * @param authToken token needed to perform network call
     * @return list of items loaded from database
     */
     LiveData<List<T>> loadData(String authToken);

    /**
     * saves data to database
     * @param objects UmboObjects to be saved in database
     */
     void saveData(T ... objects);

    /**
     * deletes data from database
     * @param object object to be deleted from database
     */
     void deleteData(T object);

    /**
     * fetches data from network
     * @param authToken token needed to perform network call from UmboApi
     */
     void fetchData(String authToken);

}
