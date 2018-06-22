package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private final Context context;

    private boolean initialized = false;
    private MutableLiveData<List<CameraGroup>> downloadedCameraGroups;
    private MutableLiveData<List<Camera>> downloadedCameras;
    
    
    public static Repository getInstance(RemoteWebService remoteWebService,
                                         AppDatabase appDatabase,
                                         AppExecutor executor,
                                         Context context) {
//        Log.d(TAG, "getInstance: Getting repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(remoteWebService, appDatabase, executor, context);
//                Log.d(TAG, "getInstance: made new repository");
            }
        }
        return sInstance;
    }
    private Repository (RemoteWebService remoteWebService,
                        AppDatabase appDatabase,
                        AppExecutor executor,
                        Context context) {
        this.mRemoteWebService = remoteWebService;
        this.appDatabase = appDatabase;
        this.executor = executor;
        this.context = context;

        downloadedCameraGroups = new MutableLiveData<>();
        downloadedCameras = new MutableLiveData<>();
    }


    public synchronized void initializeData(String authToken) {
        if (initialized) {
//            Log.d(TAG, "initializeData: true");
            return;
        }
        initialized = true;

//        Log.d(TAG, "initializeData: initializing data");

        LiveData<List<CameraGroup>> networkCameraGroups = downloadedCameraGroups;
        LiveData<List<Camera>> networkCameras = downloadedCameras;

        /** observing data: camera groups and camera will be updated by postValue() each time
         * fetchData() method is called*/
        networkCameraGroups.observeForever((List<CameraGroup> newCameraGroups) -> {
            executor.diskIO().execute(()->{

                if (newCameraGroups != null) {
                    for (int i = 0; i < newCameraGroups.size(); i++) {
                        // saving camera groups to database
                        appDatabase.cameraGroupDao().saveCameraGroup(newCameraGroups.get(i));
                        Log.d(TAG, "initializeData: saved camera group " + newCameraGroups.get(i).getName());
                    }
                } else {
//                    Log.d(TAG, "initializeData: no camera groups in account");
                }

            });
        });
        networkCameras.observeForever((List<Camera> newCameras) -> {

            executor.diskIO().execute(() -> {

                if (newCameras != null) {
                    for (int i = 0; i < newCameras.size(); i++) {
                        // saving cameras to database
                        appDatabase.cameraDao().saveCamera(newCameras.get(i));
                        Log.d(TAG, "initializeData: saved camera " + newCameras.get(i).getName() + "\n jumbo id: " + newCameras.get(i).getJumboId());
                    }
                } else {
//                    Log.d(TAG, "initializeData: no cameras in account");
                }
            });
        });

        // fetch data from web the first time
        if (isNetworkAvailable()){
//            Log.d(TAG, "initializeData: network connected");
            fetchData(authToken);
        }

    }

    //-------------------database operations --------------------//

    // Cameras
    @Override
    public LiveData<List<Camera>> loadCameras(String authToken) {
        // checking network status
        if (isNetworkAvailable()) {
            Log.d(TAG, "loadCameras: network connected");
            // if connected then fetch from web
            fetchData(authToken);
        }

        // retrieve from persisted data in db
//        Log.d(TAG, "loadCameras: loaded from db");
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

    //Camera Groups
    @Override
    public LiveData<List<CameraGroup>> loadCameraGroups(String authToken) {
        // checking network status
        if (isNetworkAvailable()){
//            Log.d(TAG, "loadCameraGroups: network connected");
            // if connected then fetch from web
            fetchData(authToken);
        }

        // retrieve from persisted data in db
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

    // -------------------networking operations ----------------------//
    public void fetchData(String authToken) {
        Call<CameraGroup[]> call = mRemoteWebService.getCameraResponse(authToken);
        List<CameraGroup> groupList = new ArrayList<CameraGroup>();
        List<Camera> cameraList = new ArrayList<>();
        call.enqueue(new Callback<CameraGroup[]>() {
            @Override
            public void onResponse(Call<CameraGroup[]> call, Response<CameraGroup[]> response) {
//                Log.d(TAG, "onResponse: sent");
                CameraGroup[] newGroups = response.body();
                if (response != null && response.isSuccessful())
                    Log.d(TAG, "onResponse: success");
                for (int i = 0; i < newGroups.length; i++) {
//                    Log.d(TAG, "onResponse: entered loop");
                    CameraGroup newGroup = new CameraGroup(newGroups[i].getId(),newGroups[i].getName(), newGroups[i].getTimezone());
                    groupList.add(newGroup);
                    String groupId = newGroup.getId();
                    Log.d(TAG, "onResponse: GROUP " + newGroups[i].getName());
                    for (int j = 0; j < newGroups[i].getCameras().length; j++) {
                        Log.d(TAG, "onResponse: entered second loop");
                        Camera newCamera = newGroups[i].getCameras()[j];
                        newCamera.setGroupId(groupId);
                        cameraList.add(newCamera);
                        Log.d(TAG, "onResponse: name: " + newCamera.getName() + "\n group: " + newCamera.getGroupId());
                    }
                }
                downloadedCameraGroups.postValue(groupList); // updating data from network
                downloadedCameras.postValue(cameraList);
            }

            @Override
            public void onFailure(Call<CameraGroup[]> call, Throwable t) {

            }
        });

    }

    /** checks network connection*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
    }
}
