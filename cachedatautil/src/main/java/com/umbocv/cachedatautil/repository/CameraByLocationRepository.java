package com.umbocv.myapplication.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.dao.CameraByLocationDao;
import com.umbocv.cachedatautil.data.local.dao.UmboDao;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.UmboApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.umbocv.cachedatautil.data.remote.Utils_NetworkStatus.isNetworkAvailable;

// for database operations for camera_group
public class CameraByLocationRepository extends UmboRepository<CameraByLocation> {

    private static final Object LOCK = new Object();
    private static CameraByLocationRepository sInstance;

    private CameraByLocationDao umboDao;
    private UmboApi umboApi;
    private AppExecutor executor;
    private Context context;

    private MutableLiveData<List<CameraByLocation>> downloadedCamerasByLocation;
    private static boolean initialized = false;

    private CameraByLocationRepository(UmboDao umboDao,
                                       Context context) {
        this.umboApi = super.umboAPI;
        this.executor = super.executor;
        this.context = context;
        this.umboDao = (CameraByLocationDao) umboDao;

        downloadedCamerasByLocation = new MutableLiveData<>();
    }

    public static CameraByLocationRepository getInstance(UmboDao umboDao,
                                                         Context context) {
        if (sInstance == null) {
            synchronized (LOCK){
                sInstance = new CameraByLocationRepository(umboDao, context);
            }
        }
        return sInstance;

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
                        umboDao.saveData(newCamerasByLocation.get(i));
                    }
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
        return umboDao.loadData();
    }

    @Override
    public void saveData(CameraByLocation... cameraByLocations) {
        executor.diskIO().execute(()->{
            umboDao.saveData(cameraByLocations);
        });
    }

    @Override
    public void deleteData(CameraByLocation cameraByLocation) {
        executor.diskIO().execute(()->{
            umboDao.deleteData(cameraByLocation);
        });
    }

    @Override
    public void fetchData(String authToken) {
        Call<CameraByLocation[]> call = umboApi.getCameraResponse(authToken);
        List<CameraByLocation> cameraByLocationList = new ArrayList<>();

        call.enqueue(new Callback<CameraByLocation[]>() {
            @Override
            public void onResponse(Call<CameraByLocation[]> call,
                                   Response<CameraByLocation[]> response) {
                if (response.isSuccessful()) {
                    CameraByLocation[] fetchedCameraByLocationArray = response.body();

                    if (fetchedCameraByLocationArray != null) {
                        for (int i = 0; i < fetchedCameraByLocationArray.length; i++) {
                            CameraByLocation cameraByLocation =
                                    new CameraByLocation (fetchedCameraByLocationArray[i].getId(),
                                    fetchedCameraByLocationArray[i].getName(),
                                    fetchedCameraByLocationArray[i].getTimezone());
                            cameraByLocationList.add(cameraByLocation);
                        }
                    }
                }

                downloadedCamerasByLocation.postValue(cameraByLocationList);
            }

            @Override
            public void onFailure(Call<CameraByLocation[]> call, Throwable t) {

            }
        });

    }

}
