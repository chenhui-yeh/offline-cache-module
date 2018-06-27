package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.CameraByLocationDao;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// for database operations for camera_group
public class CameraByLocationRepository implements UmboRepository <CameraByLocation> {

    private static final String TAG = "CameraByLocationReposit";
    
    private final CameraByLocationDao cameraByLocationDao;
    private final RemoteWebService remoteWebService;
    private final AppExecutor executor;
    private final Context context;

    private static final Object LOCK = new Object();
    private static CameraByLocationRepository sInstance;

    private MutableLiveData<List<CameraByLocation>> downloadedCamerasByLocation;
    private static boolean initialized = false;

    public static CameraByLocationRepository getInstance(CameraByLocationDao cameraByLocationDao,
                                                         RemoteWebService remoteWebService,
                                                         AppExecutor executor,
                                                         Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new CameraByLocationRepository(cameraByLocationDao,
                        remoteWebService,
                        executor,
                        context);
            }
        }
        return sInstance;
    }

    private CameraByLocationRepository(CameraByLocationDao cameraByLocationDao,
                                       RemoteWebService remoteWebService,
                                       AppExecutor executor,
                                       Context context) {
        this.cameraByLocationDao = cameraByLocationDao;
        this.remoteWebService = remoteWebService;
        this.executor = executor;
        this.context = context;

        downloadedCamerasByLocation = new MutableLiveData<>();
    }

    @Override
    public void initializeData(String authToken) {
        if(initialized) {
            return;
        }

        initialized = true;
        LiveData<List<CameraByLocation>> networkCamerasByLocation = downloadedCamerasByLocation;

        networkCamerasByLocation.observeForever((List<CameraByLocation> newCamerasByLocation) -> {
            if (newCamerasByLocation != null && newCamerasByLocation.size() > 0) {
                executor.diskIO().execute(() -> {
                    for (int i = 0; i < newCamerasByLocation.size(); i++) {
                        cameraByLocationDao.saveData(newCamerasByLocation.get(i));
                    }
                    Log.d(TAG, "initializeData: saved to database");
                });
            }
        });

        if (isNetworkAvailable(context)) {
            fetchData(authToken);
        }

    }

    @Override
    public LiveData<List<CameraByLocation>> loadData(String authToken) {
        if (isNetworkAvailable(context)) {
            fetchData(authToken);
        }
        return cameraByLocationDao.loadData();
    }

    @Override
    public void saveData(CameraByLocation... objects) {
        executor.diskIO().execute(()->{
            cameraByLocationDao.saveData(objects);
        });
    }

    @Override
    public void deleteData(CameraByLocation object) {
        executor.diskIO().execute(()->{
            cameraByLocationDao.deleteData(object);
        });
    }

    @Override
    public void fetchData(String authToken) {
        Call<CameraByLocation[]> call = remoteWebService.getCameraResponse(authToken);
        List<CameraByLocation> cameraByLocationList = new ArrayList<>();

        call.enqueue(new Callback<CameraByLocation[]>() {
            @Override
            public void onResponse(Call<CameraByLocation[]> call, Response<CameraByLocation[]> response) {
                if (response != null && response.isSuccessful()) {
                    CameraByLocation[] fetchedCameraByLocationArray = response.body();

                    if (fetchedCameraByLocationArray != null) {
                        for (int i = 0; i < fetchedCameraByLocationArray.length; i++) {
                            CameraByLocation cameraByLocation = new CameraByLocation (fetchedCameraByLocationArray[i].getId(),
                                    fetchedCameraByLocationArray[i].getName(),
                                    fetchedCameraByLocationArray[i].getTimezone());
                            cameraByLocationList.add(cameraByLocation);
                            Log.d(TAG, "onResponse: " + "\n id: " + cameraByLocation.getId() + "\n name: " + cameraByLocation.getName());
                        }
                    }
                }

                downloadedCamerasByLocation.postValue(cameraByLocationList);
                Log.d(TAG, "onResponse: updated data");
            }

            @Override
            public void onFailure(Call<CameraByLocation[]> call, Throwable t) {

            }
        });

        Log.d(TAG, "fetchData: fetched data from web");

    }

    @Override
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();

    }

}
