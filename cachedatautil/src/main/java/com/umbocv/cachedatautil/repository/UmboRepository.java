package com.umbocv.myapplication.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.Constants;
import com.umbocv.cachedatautil.data.local.dao.UmboDao;
import com.umbocv.cachedatautil.data.remote.UmboApi;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base repository
 * @param <T> The data type to be manipulated by the repository
 */
public abstract class UmboRepository<T> {

    private static Retrofit retrofit;

    // instances to be created once for subclasses
    protected UmboApi umboAPI = getRetrofitInstance().create(UmboApi.class);
    protected AppExecutor executor = AppExecutor.getInstance();

    UmboRepository(){
        // default empty constructor
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

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            retrofit = retrofitBuilder.build();
        }
        return retrofit;
    }
}
