//package com.umbocv.cachedatautil.data.repository;
//
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.util.Log;
//
//import com.umbocv.cachedatautil.AppExecutor;
//import com.umbocv.cachedatautil.data.local.AppDatabase;
//import com.umbocv.cachedatautil.data.model.Camera;
//import com.umbocv.cachedatautil.data.model.CameraByLocation;
//import com.umbocv.cachedatautil.data.remote.RemoteWebService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//// retrieves data from network if available
//// otherwise load from local db
//public class ToRefactorRepo {
//
//    private static final String TAG = "ToRefactorRepo";
//
//    private static final Object LOCK = new Object();
//    private static ToRefactorRepo sInstance;
//
//    private final RemoteWebService mRemoteWebService;
//    private final AppDatabase appDatabase;
//    private final AppExecutor executor; // provide background thread for database operations
//    private final Context context; // needed to check network status
//
//    private static boolean initialized = false;
//    private MutableLiveData<List<CameraByLocation>> downloadedCameraByLocation; // to be observed
//    private MutableLiveData<List<Camera>> downloadedCameras; // to be observed
//
//
//    public static ToRefactorRepo getInstance(RemoteWebService remoteWebService,
//                                             AppDatabase appDatabase,
//                                             AppExecutor executor,
//                                             Context context) {
//        if (sInstance == null) {
//            synchronized (LOCK) {
//                sInstance = new ToRefactorRepo(remoteWebService, appDatabase, executor, context);
//            }
//        }
//        return sInstance;
//    }
//    private ToRefactorRepo(RemoteWebService remoteWebService,
//                           AppDatabase appDatabase,
//                           AppExecutor executor,
//                           Context context) {
//        this.mRemoteWebService = remoteWebService;
//        this.appDatabase = appDatabase;
//        this.executor = executor;
//        this.context = context;
//
//        downloadedCameraByLocation = new MutableLiveData<>();
//        downloadedCameras = new MutableLiveData<>();
//    }
//
//
//    // call this method when first instantiates repository object
//    // to initialize content in database
//    public synchronized void initializeData(String authToken) {
//        if (initialized) {
//            return;
//        }
//        initialized = true;
//
//        LiveData<List<CameraByLocation>> networkCameraByLocation = downloadedCameraByLocation;
//        LiveData<List<Camera>> networkCameras = downloadedCameras;
//
//        // observing data:
//        // networkCameraByLocation and networkCameras will be updated by postValue() each time fetchData() method is called
//        // will be observed until app is uninstalled
//        networkCameraByLocation.observeForever((List<CameraByLocation> newCameraByLocations) -> {
//            executor.diskIO().execute(()->{
//                if (newCameraByLocations != null) {
//                    for (int i = 0; i < newCameraByLocations.size(); i++) {
//                        // saving camera groups to database
//                        appDatabase.cameraByLocationDao().saveCameraByLocation(newCameraByLocations.get(i));
//                    }
//                }
//            });
//        });
//
//        networkCameras.observeForever((List<Camera> newCameras) -> {
//            executor.diskIO().execute(() -> {
//                if (newCameras != null) {
//                    for (int i = 0; i < newCameras.size(); i++) {
//                        // saving cameras to database
//                        appDatabase.cameraDao().saveCamera(newCameras.get(i));
//                    }
//                }
//            });
//        });
//
//        // fetch data from web the first time
//        if (isNetworkAvailable()){
//            fetchData(authToken);
//        }
//
//    }
//
//    //---------------------database operations -----------------------//
//
//    // cameras
//    @Override
//    public LiveData<List<Camera>> loadCameras(String authToken) {
//        // checking network status
//        if (isNetworkAvailable()) {
//            // if connected then fetch from web
//            fetchData(authToken);
//        }
//
//        // retrieve from persisted data in db regardless of network status
//        return appDatabase.cameraDao().loadCameras();
//    }
//
//    @Override
//    public void saveCamera(Camera camera) {
//        executor.diskIO().execute(()->{
//            appDatabase.cameraDao().saveCamera(camera);
//        });
//    }
//
//    @Override
//    public void deleteCamera(Camera camera) {
//        executor.diskIO().execute(() -> {
//            appDatabase.cameraDao().deleteCamera(camera);
//        });
//    }
//
//    // camera_by_location
//    @Override
//    public LiveData<List<CameraByLocation>> loadCameraByLocation(String authToken) {
//        // checking network status
//        if (isNetworkAvailable()){
//            // if connected then fetch from web
//            fetchData(authToken);
//        }
//
//        // retrieve from persisted data in db regardless of network status
//        return appDatabase.cameraByLocationDao().loadCameraByLocation();
//    }
//
//    @Override
//    public void deleteCameraByLocation(CameraByLocation cameraByLocation) {
//        executor.diskIO().execute(() -> {
//            appDatabase.cameraByLocationDao().deleteCameraByLocation(cameraByLocation);
//        });
//
//    }
//
//    @Override
//    public void saveCameraByLocation(CameraByLocation cameraByLocation) {
//        executor.diskIO().execute(()->{
//            appDatabase.cameraByLocationDao().saveCameraByLocation(cameraByLocation);
//        });
//    }
//
//    // -------------------networking operations ----------------------//
//
//    // fetch data through network
//    // needs to check network connection first before calling this method
//    public void fetchData(String authToken) {
//        Call<CameraByLocation[]> call = mRemoteWebService.getCameraResponse(authToken);
//        List<CameraByLocation> groupList = new ArrayList<CameraByLocation>();
//        List<Camera> cameraList = new ArrayList<>();
//        call.enqueue(new Callback<CameraByLocation[]>() {
//            @Override
//            public void onResponse(Call<CameraByLocation[]> call, Response<CameraByLocation[]> response) {
//                CameraByLocation[] newGroups = response.body();
//                if (response != null && response.isSuccessful())
//                for (int i = 0; i < newGroups.length; i++) {
//                    CameraByLocation newGroup = new CameraByLocation(newGroups[i].getId(),newGroups[i].getName(), newGroups[i].getTimezone());
//                    groupList.add(newGroup);
//                    String groupId = newGroup.getId();
//                    for (int j = 0; j < newGroups[i].getCameras().length; j++) {
//                        Camera newCamera = newGroups[i].getCameras()[j];
//                        newCamera.setLocationId(groupId);
//                        cameraList.add(newCamera);
//                    }
//                }
//                downloadedCameraByLocation.postValue(groupList); // updating data from network
//                downloadedCameras.postValue(cameraList);
//            }
//
//            @Override
//            public void onFailure(Call<CameraByLocation[]> call, Throwable t) {
//                Log.e(TAG, "onFailure: failed to perform network call");
//            }
//        });
//    }
//
//    // checks network connection
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
//    }
//
//}
