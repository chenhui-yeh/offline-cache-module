package com.chenhuiyeh.myapplication.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.umbocv.cachedatautil.AppExecutor;
import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.model.CameraGroup;
import com.umbocv.cachedatautil.data.remote.RemoteWebService;
import com.umbocv.cachedatautil.data.repository.Repository;
import com.umbocv.cachedatautil.injection.Injection;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class RepositoryTest {

    AppDatabase appDatabase;
    RemoteWebService remoteDataSource;
    AppExecutor appExecutor;

    private Repository mRepository;


    @Before
    public void setup() {
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        remoteDataSource = Injection.provideRemoteDataSource();
        appExecutor = AppExecutor.getInstance();

        mRepository = Repository.getInstance(remoteDataSource, appDatabase, appExecutor, InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void saveFromWeb_invokeCorrectApiCalls() {
        String authToken = anyString();
        when(remoteDataSource
                .getCameraResponse(authToken))
                .thenReturn(remoteDataSource.getCameraResponse(authToken));
    }

    private List<CameraGroup> cameraGroupList () {
        List<CameraGroup> cameraGroups = new ArrayList<>();
        CameraGroup one = new CameraGroup("id 1", "name 1", "timezone 1");
        CameraGroup two = new CameraGroup("id 2", "name 2", "timezone 2");

        cameraGroups.add(one);
        cameraGroups.add(two);

        return cameraGroups;
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