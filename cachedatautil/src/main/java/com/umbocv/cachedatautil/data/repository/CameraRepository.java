package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.CameraDao;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraRepository implements UmboRepository<Camera> {

    private static final String TAG = "CameraRepository";

    private final CameraDao cameraDao;
    private final RemoteWebService remoteWebService;
    private final AppExecutor executor;
    private final Context context;

    private static final Object LOCK = new Object();
    private static CameraRepository sInstance;

    private MutableLiveData<List<Camera>> downloadedCameras;
    private static boolean initialized = false;

    public static CameraRepository getInstance(CameraDao cameraDao,
                                        RemoteWebService remoteWebService,
                                        AppExecutor executor,
                                        Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new CameraRepository(cameraDao, remoteWebService, executor, context);
            }
        }
        return sInstance;
    }
    private CameraRepository(CameraDao cameraDao,
                            RemoteWebService remoteWebService,
                            AppExecutor executor,
                            Context context) {
        this.cameraDao = cameraDao;
        this.remoteWebService = remoteWebService;
        this.executor = executor;
        this.context = context;

        downloadedCameras = new MutableLiveData<>();
    }

    @Override
    public void initializeData(String authToken) {
        if (initialized) {
            return;
        }
        initialized = true;

        LiveData<List<Camera>> networkCameras = downloadedCameras;
        networkCameras.observeForever((List<Camera> newCameras) -> {
            executor.diskIO().execute(()->{
                if (newCameras != null && newCameras.size() > 0) {
                    for (int i = 0; i < newCameras.size(); i++) {
                        cameraDao.saveData(newCameras.get(i));
                    }
                }
            });
        });

        if (isNetworkAvailable(context)) {
            fetchData(authToken);
        }

        Log.d(TAG, "initializeData: initialized data");
    }

    @Override
    public LiveData<List<Camera>> loadData(String authToken) {
        if (isNetworkAvailable(context)) {
            fetchData(authToken);
        }
        return cameraDao.loadData();
    }

    @Override
    public void saveData(Camera... objects) {
        executor.diskIO().execute(() -> {
            cameraDao.saveData(objects);
        });
    }

    @Override
    public void deleteData(Camera object) {
        executor.diskIO().execute(() -> {
            cameraDao.deleteData(object);
        });
    }

    @Override
    public void fetchData(String authToken) {
        Call<CameraByLocation[]> call = remoteWebService.getCameraResponse(authToken);
        List<Camera> fetchedCameraList = new ArrayList<>();
        call.enqueue(new Callback<CameraByLocation[]>() {
            @Override
            public void onResponse(Call<CameraByLocation[]> call, Response<CameraByLocation[]> response) {
                if (response != null && response.isSuccessful()) {
                    CameraByLocation[] newCamerasByLocationArray = response.body();
                    if (newCamerasByLocationArray != null && newCamerasByLocationArray.length > 0) {
                        for (int i = 0; i < newCamerasByLocationArray.length; i++) {
                            String groupId =  newCamerasByLocationArray[i].getId();
                            if (newCamerasByLocationArray[i].getCameras() != null && newCamerasByLocationArray[i].getCameras().length > 0) {
                                for (int j = 0; j < newCamerasByLocationArray[i].getCameras().length; j++) {
                                    Camera fetchedCamera = newCamerasByLocationArray[i].getCameras()[j];
                                    fetchedCamera.setLocationId(groupId);
                                    fetchedCameraList.add(fetchedCamera);
                                }
                            }
                        }
                    }
                }
                downloadedCameras.postValue(fetchedCameraList);
                Log.d(TAG, "onResponse: updated data");
            }

            @Override
            public void onFailure(Call<CameraByLocation[]> call, Throwable t) {

            }
        });

        Log.d(TAG, "fetchData: fetched from web");
    }

    @Override
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
    }
}
