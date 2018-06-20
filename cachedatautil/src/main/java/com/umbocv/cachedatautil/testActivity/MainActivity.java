package com.umbocv.cachedatautil.testActivity;

import android.arch.lifecycle.LiveData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.chenhuiyeh.unittestingumbo.R;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.repository.Repository;
import com.umbocv.cachedatautil.injection.Injection;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String TOKEN = "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1Mjk0NjgxOTEsImV4cCI6MTUyOTQ4NjE5MX0.vQKPBoiHVL10rxIEQmTdbpyzMcImCVTl_g_gzufPY60";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Repository repository = Injection.provideRepository(this);
        repository.initializeData(TOKEN);
    }
}
