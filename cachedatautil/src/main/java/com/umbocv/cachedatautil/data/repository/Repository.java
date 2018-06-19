package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraGroup;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository implements CameraRepository, CameraGroupRepository {

    private static final String TAG = "Repository";

    private static final Object LOCK = new Object();
    private static Repository sInstance;
    
    private final RemoteWebService mRemoteWebService;
    private final AppDatabase appDatabase;
    private final AppExecutor executor;

    private static boolean initialized = false;
    private MutableLiveData<List<CameraGroup>> downloadedCameraGroups;
    private MutableLiveData<List<Camera>> downloadedCameras;
    
    public static Repository getInstance(RemoteWebService remoteWebService,
                                                      AppDatabase appDatabase,
                                                      AppExecutor executor) {
        Log.d(TAG, "getInstance: Getting repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(remoteWebService, appDatabase, executor);
                Log.d(TAG, "getInstance: made new repository");
            }
        }
        return sInstance;
    }
    private Repository (RemoteWebService remoteWebService, AppDatabase appDatabase, AppExecutor executor) {
        this.mRemoteWebService = remoteWebService;
        this.appDatabase = appDatabase;
        this.executor = executor;

        downloadedCameraGroups = new MutableLiveData<>();
        downloadedCameras = new MutableLiveData<>();
    }

    public synchronized void initializeData(String authToken) {
        if (initialized) return;
        initialized = true;
        Log.d(TAG, "initializeData: initializing data");

        LiveData<List<CameraGroup>> networkCameraGroups = downloadedCameraGroups;
        LiveData<List<Camera>> networkCameras = downloadedCameras;
        networkCameraGroups.observeForever((List<CameraGroup> newCameraGroups) -> {
            executor.diskIO().execute(()->{
                CameraGroup[] groupArray = newCameraGroups.toArray(new CameraGroup[0]);
                appDatabase.cameraGroupDao().saveCameraGroup(groupArray);
                Log.d(TAG, "initializeData: saved camera group");
            });
        });
        networkCameras.observeForever((List<Camera> newCameras) -> {
            executor.diskIO().execute(() -> {
                Camera[] cameraArray = newCameras.toArray(new Camera[0]);
                appDatabase.cameraDao().saveCamera(cameraArray);
                Log.d(TAG, "initializeData: saved camera");
            });
        });

        fetchData(authToken);

    }

    @Override
    public LiveData<List<Camera>> loadCameras() {
        return appDatabase.cameraDao().loadCameras();
    }

    @Override
    public void saveCamera(Camera camera) {
        executor.diskIO().execute(()->{
            appDatabase.cameraDao().saveCamera(camera);
        });
    }

    @Override
    public void deleteCamera(Camera camera) {
        executor.diskIO().execute(() -> {
            appDatabase.cameraDao().deleteCamera(camera);
        });
    }

    @Override
    public LiveData<List<CameraGroup>> loadCameraGroups() {
        return appDatabase.cameraGroupDao().loadCameraGroups();
    }

    @Override
    public void deleteCameraGroup(CameraGroup cameraGroup) {
        executor.diskIO().execute(() -> {
            appDatabase.cameraGroupDao().deleteCameraGroup(cameraGroup);
        });

    }

    @Override
    public void saveCameraGroup(CameraGroup cameraGroup) {
        executor.diskIO().execute(()->{
            appDatabase.cameraGroupDao().saveCameraGroup(cameraGroup);
        });
    }

    /**networking operations */
    public void fetchData(String authToken) {
        Call<CameraGroup[]> call = mRemoteWebService.getCameraResponse(authToken);
        List<CameraGroup> groupList = new ArrayList<CameraGroup>();
        List<Camera> cameraList = new ArrayList<>();
        call.enqueue(new Callback<CameraGroup[]>() {
            @Override
            public void onResponse(Call<CameraGroup[]> call, Response<CameraGroup[]> response) {
                Log.d(TAG, "onResponse: sent");
                CameraGroup[] newGroups = response.body();
                if (response != null && response.isSuccessful())
                    Log.d(TAG, "onResponse: success");
                for (int i = 0; i < newGroups.length; i++) {
                    Log.d(TAG, "onResponse: entered loop");
                    CameraGroup newGroup = new CameraGroup(newGroups[i].getId(),newGroups[i].getName(), newGroups[i].getTimezone());
                    groupList.add(newGroup);
                    Log.d(TAG, "onResponse: " + newGroups[i].getTimezone());
                    for (int j = 0; j < newGroups[i].getCameras().length; j++) {
                        Log.d(TAG, "onResponse: entered second loop");
                        Camera newCamera = newGroups[i].getCameras()[j];
                        cameraList.add(newCamera);
                        Log.d(TAG, "onResponse: " + newCamera.getName());
                    }
                }
                downloadedCameraGroups.postValue(groupList); // update data
                downloadedCameras.postValue(cameraList);
            }

            @Override
            public void onFailure(Call<CameraGroup[]> call, Throwable t) {

            }
        });
    }
}
