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
    private static Retrofit retrofit;

    // UPDATE TOKEN //
    private final String TOKEN = "bearer " +
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1MzA2OTU1MjUsImV4cCI6MTUzMDcxMzUyNX0" +
            ".WNm6cqbEhyiMQygXKdEC1qfRfU9jCPewZN1wKyjSK7U";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UmboApi umboApi = getRetrofitInstance().create(UmboApi.class);
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        AppExecutor executor = AppExecutor.getInstance();

        UmboDao cameraByLocationDao = appDatabase.cameraByLocationDao();
        UmboDao cameraDao = appDatabase.cameraDao();

        UmboRepository<CameraByLocation> cameraByLocationUmboRepository =
                CameraByLocationRepository.getInstance(cameraByLocationDao, umboApi, executor, this);
        cameraByLocationUmboRepository.initializeData(TOKEN);

        UmboRepository<Camera> cameraUmboRepository =
                CameraRepository.getInstance(cameraDao, umboApi, executor, this);
        cameraUmboRepository.initializeData(TOKEN);

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