package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;


import com.umbocv.cachedatautil.data.model.UmboObject;

import java.util.List;

/**
 * Base repository
 * @param <T>
 */
public interface UmboRepository<T extends UmboObject<T>> {

    void initializeData(String authToken);

    /**
     * loads data from network if network connection is available
     * else loads data from database
     * @param authToken
     * @return list of items loaded from database
     */
     LiveData<List<T>> loadData(String authToken);

    /**
     * saves data to database
     * @param objects
     */
     void saveData(T ... objects);

    /**
     * deletes data from database
     * @param object
     */
     void deleteData(T object);

    /**
     * fetches data from network
     * @param authToken
     */
     void fetchData(String authToken);

    /**
     * checks network connection and availability
     * @param context
     * @return
     */
     boolean isNetworkAvailable(Context context);

}
