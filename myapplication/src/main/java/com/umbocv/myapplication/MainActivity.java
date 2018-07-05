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
    private final String TOKEN = "bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1MzA3NzIxODQsImV4cCI6MTUzMDc5MDE4NH0" +
            ".5jjN6jKzIlChY826DjtZegI1i-LfhLkHwa5QZTbf-Ks";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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