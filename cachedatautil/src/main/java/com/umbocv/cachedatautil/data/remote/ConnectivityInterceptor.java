package com.umbocv.cachedatautil.data.remote;

import android.util.Log;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.Response;

// can be used to check network availability
public class ConnectivityInterceptor implements Interceptor {

    private static final String TAG = "ConnectivityInterceptor";
    private boolean isNetworkActive;

    public ConnectivityInterceptor(Observable<Boolean> isNetworkActive) {
        isNetworkActive.subscribe(
                _isNetworkActive -> this.isNetworkActive = _isNetworkActive,
                _error -> Log.e(TAG, "ConnectivityInterceptor: " + _error.getMessage())
        );
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (! isNetworkActive) {
            throw new NoConnectivityException();
        } else {
            Response response = chain.proceed(chain.request());
            return response;
        }
    }

    public class NoConnectivityException extends IOException {
        @Override
        public String getMessage() {
            return "No network available, please check your wifi or Data connection";
        }
    }
}
