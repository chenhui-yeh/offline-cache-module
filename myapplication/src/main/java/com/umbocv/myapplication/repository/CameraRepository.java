package com.umbocv.myapplication.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.dao.CameraDao;
import com.umbocv.cachedatautil.data.local.dao.UmboDao;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.UmboApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.umbocv.cachedatautil.data.remote.Utils_NetworkStatus.isNetworkAvailable;

public class CameraRepository extends UmboRepository<Camera> {

    private static final String TAG = "CameraRepository";

    private static final Object LOCK = new Object();
    private static CameraRepository sInstance;

    private CameraDao umboDao;
    private UmboApi umboApi;
    private AppExecutor executor;
    private Context context;

    private MutableLiveData<List<Camera>> downloadedCameras;
    private static boolean initialized = false;

    private CameraRepository(UmboDao umboDao,
                             Context context) {

        this.umboApi = super.umboAPI;
        this.executor = super.executor;
        this.context = context;
        this.umboDao = (CameraDao) umboDao;

        downloadedCameras = new MutableLiveData<>();
    }


    public static CameraRepository getInstance(UmboDao umboDao,
                                               Context context) {
        if (sInstance == null) {
            synchronized (LOCK){
                sInstance = new CameraRepository(umboDao, context);
            }
        }
        return sInstance;
    }


    @Override
    public void initializeData(String authToken) {
        if (initialized) {
            return;
        }

        initialized = true;

        LiveData<List<Camera>> networkCameras = downloadedCameras;
        networkCameras.observeForever((List<Camera> newCameras) -> {
            this.executor.diskIO().execute(()->{
                if (newCameras != null && newCameras.size() > 0) {
                    for (int i = 0; i < newCameras.size(); i++) {
                        umboDao.saveData(newCameras.get(i));
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
        return umboDao.loadData();
    }

    @Override
    public void saveData(Camera... cameras) {
        executor.diskIO().execute(() -> {
            umboDao.saveData(cameras);
        });
    }

    @Override
    public void deleteData(Camera camera) {
        executor.diskIO().execute(() -> {
            umboDao.deleteData(camera);
        });
    }

    @Override
    public void fetchData(String authToken) {
        Call<CameraByLocation[]> call = umboApi.getCameraResponse(authToken);
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
}
