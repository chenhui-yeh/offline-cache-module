package com.umbocv.cachedatautil.testActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chenhuiyeh.unittestingumbo.R;
import com.umbocv.cachedatautil.data.repository.Repository;
import com.umbocv.cachedatautil.injection.Injection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // UPDATE TOKEN
    private final String TOKEN = "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1Mjk2MzUzNDcsImV4cCI6MTUyOTY1MzM0N30.HsbjxDByRJcpLydBjzhoUqWsJ_ZpwSTeKef2ak4MF_U";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Repository repository = Injection.provideRepository(this);
        repository.initializeData(TOKEN);
        repository.loadCameras(TOKEN);
    }
}
