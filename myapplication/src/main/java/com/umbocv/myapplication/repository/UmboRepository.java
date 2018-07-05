package com.umbocv.myapplication.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * Base repository
 * @param <T> The data type to be manipulated by the repository
 */
public abstract class UmboRepository<T> {

    private static UmboRepository sInstance;
    private static final Object LOCK = new Object();

    UmboRepository(){

    }

    /**
     * initializes the data in database
     * @param authToken token needed to perform network call from UmboApi
     */
    abstract public void initializeData(String authToken);

    /**
     * loads data from network if network connection is available
     * else loads data from database
     * @param authToken token needed to perform network call
     * @return list of items loaded from database
     */
     abstract public LiveData<List<T>> loadData(String authToken);

    /**
     * saves data to database
     * @param objects UmboObjects to be saved in database
     */
     abstract public void saveData(T ... objects);

    /**
     * deletes data from database
     * @param object object to be deleted from database
     */
    abstract public void deleteData(T object);

    /**
     * fetches data from network
     * @param authToken token needed to perform network call from UmboApi
     */
    abstract public void fetchData(String authToken);

}
