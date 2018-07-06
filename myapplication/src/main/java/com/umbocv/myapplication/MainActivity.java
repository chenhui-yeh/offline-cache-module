package com.umbocv.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.Constants;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.local.dao.UmboDao;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.UmboApi;
import com.umbocv.myapplication.repository.CameraByLocationRepository;
import com.umbocv.myapplication.repository.CameraRepository;
import com.umbocv.myapplication.repository.UmboRepository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // UPDATE TOKEN //
    private final String TOKEN = "bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1MzA4NTE4NTgsImV4cCI6MTUzMDg2OTg1OH0.skDlIUy3jUXM_uUv5Tsd03rmM6S8CZpzFLpKa-KpHN0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database
        AppDatabase appDatabase = AppDatabase.getInstance(this);

        // DAOs
        UmboDao cameraByLocationDao = appDatabase.cameraByLocationDao();
        UmboDao cameraDao = appDatabase.cameraDao();

        // CameraByLocationRepository
        UmboRepository<CameraByLocation> cameraByLocationUmboRepository =
                CameraByLocationRepository.getInstance(cameraByLocationDao, this);
        cameraByLocationUmboRepository.initializeData(TOKEN);

        // CameraRepository
        UmboRepository<Camera> cameraUmboRepository =
                CameraRepository.getInstance(cameraDao, this);
        cameraUmboRepository.initializeData(TOKEN);

    }


}