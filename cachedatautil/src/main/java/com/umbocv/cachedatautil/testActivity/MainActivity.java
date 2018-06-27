package com.umbocv.cachedatautil.testActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chenhuiyeh.unittestingumbo.R;
import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.Constants;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;
import com.umbocv.cachedatautil.data.repository.ToRefactorRepo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static Retrofit retrofit;
    private static RemoteWebService remoteWebService;
    private static AppDatabase appDatabase;
    private static AppExecutor executor;

    // UPDATE TOKEN //
    private final String TOKEN = "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1Mjk2MzUzNDcsImV4cCI6MTUyOTY1MzM0N30.HsbjxDByRJcpLydBjzhoUqWsJ_ZpwSTeKef2ak4MF_U";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remoteWebService = getRetrofitInstance().create(RemoteWebService.class);
        appDatabase = AppDatabase.getInstance(this);
        executor = AppExecutor.getInstance();
        ToRefactorRepo toRefactorRepo = ToRefactorRepo.getInstance(remoteWebService, appDatabase, executor, this.getApplicationContext());
        toRefactorRepo.initializeData(TOKEN);
        toRefactorRepo.loadCameras(TOKEN);
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            retrofit = retrofitBuilder.build();
        }
        return retrofit;
    }
}
