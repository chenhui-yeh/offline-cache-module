package com.umbocv.cachedatautil.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.umbocv.cachedatautil.data.local.dao.CameraByLocationDao;
import com.umbocv.cachedatautil.data.model.CameraByLocation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CameraByLocationDaoTest {
    CameraByLocationDao mCameraByLocationDao;
    AppDatabase appDatabase;

    @Before
    public void setup() {
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mCameraByLocationDao = appDatabase.cameraByLocationDao();
    }

    @Test
    public void loadCameraGroups_shouldGetEmptyList () throws InterruptedException {
        assertEquals(0, getValue(mCameraByLocationDao.loadData()).size());
    }

    @Test
    public void loadCameraGroups_successfullyRetrieveList() throws InterruptedException {
        CameraByLocation newCameraByLocation = generalTestCameraGroup("camera id 1", "name 1", "timezone 1");
        mCameraByLocationDao.saveData(newCameraByLocation);

        assertEquals(newCameraByLocation.getId(), getValue(mCameraByLocationDao.loadData()).get(0).getId());
    }

    @Test
    public void saveCameraGroups_successfullyInserted() throws InterruptedException {
        CameraByLocation newCameraByLocation = generalTestCameraGroup("camera id 1", "name 1", "timezone 1");
        mCameraByLocationDao.saveData(newCameraByLocation);

        assertEquals(newCameraByLocation.getId(), getValue(mCameraByLocationDao.loadData()).get(0).getId());
    }

    @Test
    public void deleteCameraGroups_successfullyDeleted() throws InterruptedException {
        CameraByLocation newGroup = generalTestCameraGroup("id 1", "name 1", "timezone 1");
        mCameraByLocationDao.saveData(newGroup);
        assertEquals(newGroup.getId(), getValue(mCameraByLocationDao.loadData()).get(0).getId());
        mCameraByLocationDao.deleteData(getValue(mCameraByLocationDao.loadData()).get(0));

        List<CameraByLocation> groupsAfterDeletion = getValue(mCameraByLocationDao.loadData());
        assertEquals(0, groupsAfterDeletion.size());
    }

    CameraByLocation generalTestCameraGroup (String id, String name, String timezone) {
        return new CameraByLocation(id, name, timezone);
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