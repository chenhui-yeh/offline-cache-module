package com.umbocv.cachedatautil.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.model.CameraByLocation;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;

//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.when;

public class RepositoryTest {

    AppDatabase appDatabase;

  //  @Mock
    RemoteWebService remoteDataSource;
    AppExecutor appExecutor;

    private Repository mRepository;


    @Before
    public void setup() {
       // MockitoAnnotations.initMocks(this);
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        appExecutor = AppExecutor.getInstance();
        // remoteDataSource = Injection.provideRemoteWebService();
        mRepository = Repository.getInstance(remoteDataSource, appDatabase, appExecutor, InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void saveFromWeb_invokeCorrectApiCalls() {
     //   String authToken = anyString();
     //   when(remoteDataSource
       //         .getCameraResponse(authToken))
     //           .thenReturn(remoteDataSource.getCameraResponse(authToken));
    }

    private List<CameraByLocation> cameraGroupList () {
        List<CameraByLocation> cameraByLocations = new ArrayList<>();
        CameraByLocation one = new CameraByLocation("id 1", "name 1", "timezone 1");
        CameraByLocation two = new CameraByLocation("id 2", "name 2", "timezone 2");

        cameraByLocations.add(one);
        cameraByLocations.add(two);

        return cameraByLocations;
    }

//    private List<Camera> cameraList() {
//
//    }

    /**
     * This is used to make sure the method waits till data is available from the observer.
     */
    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) data[0];
    }

}