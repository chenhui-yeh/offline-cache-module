package com.umbocv.cachedatautil.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraGroup;

@Database(entities = {Camera.class, CameraGroup.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CameraDao cameraDao();
    public abstract CameraGroupDao cameraGroupDao();

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "umbo_cameras";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
//        Log.d(LOG_TAG, "Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
//                        .addMigrations()
                        .build();
//                Log.d(LOG_TAG, "Made new database");
            }
        }
        return sInstance;
    }

    // sample migration
//    static final Migration FROM_1_TO_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(final SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE cameras
//                    ADD COLUMN createdAt TEXT");
//        }
//    };


}
