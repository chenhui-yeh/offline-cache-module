package com.umbocv.cachedatautil.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.umbocv.cachedatautil.data.model.Camera;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CameraDaoTest {
    CameraDao cameraDao;

    AppDatabase mAppDatabase;

    @Before
    public void setup() {
        mAppDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        cameraDao = mAppDatabase.cameraDao();
    }

    @Test
    public void loadCameras_shouldGetEmptyList_ifNothingInserted() throws InterruptedException {
        assertEquals(0, getValue(cameraDao.loadData()).size());
    }

    @Test
    public void loadCameras_loadCameras_success() throws InterruptedException {
        Camera newCamera = generalTestCamera("camera id 1", "name 1", "jumbo id 1", "serial 1", "group id");

        cameraDao.saveData(newCamera);
        List<Camera> cameraRetrieved = getValue(cameraDao.loadData());
        assertEquals(1, cameraRetrieved.size());

    }

    @Test
    public void addCamera_SuccessfullyAddCamera() throws InterruptedException {
        Camera newCamera = generalTestCamera("camera id 1", "name 1", "jumbo id 1", "serial 1", "group id");

        cameraDao.saveData(newCamera);

        List<Camera> cameraRetrieved = getValue(cameraDao.loadData());
        assertEquals("camera id 1", cameraRetrieved.get(0).getId());
        assertEquals("name 1", cameraRetrieved.get(0).getName());

    }

    @Test
    public void deleteCamera_SuccessfullyDeleteCamera() throws InterruptedException {
        Camera newCamera = generalTestCamera("camera id 1", "name 1", "jumbo id 1", "serial 1", "group id");
        cameraDao.saveData(newCamera);

        List<Camera> cameraRetrieved = getValue(cameraDao.loadData());
        assertEquals(newCamera.getId(), cameraRetrieved.get(0).getId());
        assertEquals(newCamera.getName(), cameraRetrieved.get(0).getName());
        cameraDao.deleteData(cameraRetrieved.get(0));
        List<Camera> cameraRetreivedAfterUpdate = getValue(cameraDao.loadData());
        assertEquals(0,cameraRetreivedAfterUpdate.size());

    }

    Camera generalTestCamera (String id, String name, String jumboId, String serial, String groupId) {
        return new Camera(id, name, jumboId, serial, groupId);
    }

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