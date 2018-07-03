package com.umbocv.cachedatautil.testActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.Constants;
import com.umbocv.cachedatautil.R;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.local.dao.UmboDao;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.UmboApi;
import com.umbocv.cachedatautil.data.repository.CameraByLocationRepository;
import com.umbocv.cachedatautil.data.repository.CameraRepository;
import com.umbocv.cachedatautil.data.repository.UmboRepository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static Retrofit retrofit;

    // UPDATE TOKEN //
    private final String TOKEN = "Bearer " +
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1MzAxNTQzOTMsImV4cCI6MTUzMDE3MjM5M30" +
            ".KYWKd8sGocPcYJstQ0MT0hzzRXHwnVHuCUeD-HDB5Z4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UmboApi umboApi = getRetrofitInstance().create(UmboApi.class);
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        AppExecutor executor = AppExecutor.getInstance();

        // daos
        UmboDao cameraDao = appDatabase.cameraDao();
        UmboDao cameraByLocationDao = appDatabase.cameraByLocationDao();

        // CameraByLocation repository
        UmboRepository<CameraByLocation> cameraByLocationRepo =
                CameraByLocationRepository.getInstance(cameraByLocationDao, umboApi, executor, this);
        cameraByLocationRepo.initializeData(TOKEN);

        // Camera repository
        UmboRepository<Camera> cameraRepo =
                CameraRepository.getInstance(cameraDao, umboApi, executor, this);
        cameraRepo.initializeData(TOKEN);
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
