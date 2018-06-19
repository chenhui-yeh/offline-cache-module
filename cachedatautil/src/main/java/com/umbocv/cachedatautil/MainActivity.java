package com.umbocv.cachedatautil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chenhuiyeh.unittestingumbo.R;
import com.umbocv.cachedatautil.data.repository.Repository;
import com.umbocv.cachedatautil.injection.Injection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Repository repository = Injection.provideRepository(this);
        repository.initializeData("Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1MjkzOTQ0NDIsImV4cCI6MTUyOTQxMjQ0Mn0.Ys-bOjmXQpG95YkO7afpzdFJEuWCUCKj3F6gVoHDVCU");
    }
}
