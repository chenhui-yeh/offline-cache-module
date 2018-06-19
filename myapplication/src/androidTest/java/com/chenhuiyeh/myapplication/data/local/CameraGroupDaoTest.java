package com.chenhuiyeh.myapplication.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.umbocv.cachedatautil.data.local.AppDatabase;
import com.umbocv.cachedatautil.data.local.CameraGroupDao;
import com.umbocv.cachedatautil.data.model.CameraGroup;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CameraGroupDaoTest {
    CameraGroupDao cameraGroupDao;
    AppDatabase appDatabase;

    @Before
    public void setup() {
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        cameraGroupDao = appDatabase.cameraGroupDao();
    }

    @Test
    public void loadCameraGroups_shouldGetEmptyList () throws InterruptedException {
        assertEquals(0, getValue(cameraGroupDao.loadCameraGroups()).size());
    }

    @Test
    public void loadCameraGroups_successfullyRetrieveList() throws InterruptedException {
        CameraGroup newCameraGroup = generalTestCameraGroup("camera id 1", "name 1", "timezone 1");
        cameraGroupDao.saveCameraGroup(newCameraGroup);

        assertEquals(newCameraGroup.getId(), getValue(cameraGroupDao.loadCameraGroups()).get(0).getId());
    }

    @Test
    public void saveCameraGroups_successfullyInserted() throws InterruptedException {
        CameraGroup newCameraGroup = generalTestCameraGroup("camera id 1", "name 1", "timezone 1");
        cameraGroupDao.saveCameraGroup(newCameraGroup);

        assertEquals(newCameraGroup.getId(), getValue(cameraGroupDao.loadCameraGroups()).get(0).getId());
    }

    @Test
    public void deleteCameraGroups_successfullyDeleted() throws InterruptedException {
        CameraGroup newGroup = generalTestCameraGroup("id 1", "name 1", "timezone 1");
        cameraGroupDao.saveCameraGroup(newGroup);
        assertEquals(newGroup.getId(), getValue(cameraGroupDao.loadCameraGroups()).get(0).getId());
        cameraGroupDao.deleteCameraGroup(getValue(cameraGroupDao.loadCameraGroups()).get(0));

        List<CameraGroup> groupsAfterDeletion = getValue(cameraGroupDao.loadCameraGroups());
        assertEquals(0, groupsAfterDeletion.size());
    }

    CameraGroup generalTestCameraGroup (String id, String name, String timezone) {
        return new CameraGroup(id, name, timezone);
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