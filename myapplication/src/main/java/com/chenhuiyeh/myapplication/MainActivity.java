package com.chenhuiyeh.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.umbocv.cachedatautil.data.repository.Repository;
import com.umbocv.cachedatautil.injection.Injection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Repository repository = Injection.provideRepository(this.getApplicationContext());
        repository.initializeData("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YTBiY2RlMDZjNmJkZDAwMDE3Njg5ZTIiLCJpYXQiOjE1MjkzODIyMjMsImV4cCI6MTUyOTQwMDIyM30.jPf1gEDDupQG7VZbM_rU2ARgI4ebAnzxG9crkEnMVRU");
    }
}
