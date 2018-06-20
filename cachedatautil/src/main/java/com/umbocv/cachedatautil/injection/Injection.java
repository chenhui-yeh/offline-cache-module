package com.umbocv.cachedatautil.injection;

import android.content.Context;
import android.util.Log;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;
import com.umbocv.cachedatautil.data.repository.Repository;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Injection {
    private static final String TAG = "Injection";

    private static RemoteWebService sRemoteWebService;
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api-rc.umbocv.com/api/locations/";

    private static AppDatabase appDatabase;
    private static AppExecutor executor;

    public static Repository provideRepository(Context context) {
        sRemoteWebService = provideRemoteDataSource();
        appDatabase = AppDatabase.getInstance(context.getApplicationContext());
        executor = AppExecutor.getInstance();
        Log.d(TAG, "provideRepository:  made repo");
        return Repository.getInstance(sRemoteWebService, appDatabase, executor, context.getApplicationContext());
    }


    public static RemoteWebService provideRemoteDataSource() {
        if (sRemoteWebService == null){
            sRemoteWebService = getRetrofitInstance().create(RemoteWebService.class);
            Log.d(TAG, "provideRemoteDataSource: made new data source");
        }
        return sRemoteWebService;
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            retrofit = retrofitBuilder.build();
        }
        return retrofit;
    }
}
