package com.umbocv.cachedatautil.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.umbocv.cachedatautil.data.local.dao.CameraByLocationDao;
import com.umbocv.cachedatautil.data.local.dao.CameraDao;
import com.umbocv.cachedatautil.data.model.Camera;
import com.umbocv.cachedatautil.data.model.CameraByLocation;

// new daos can be added here
@Database(entities = {Camera.class, CameraByLocation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // DAOs
    public abstract CameraDao cameraDao();
    public abstract CameraByLocationDao cameraByLocationDao ();

    private static final String DATABASE_NAME = "umbocv_cameras";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
//                        .addMigrations(MIGRATION_1_2) // ADD FUTURE MIGRATIONS HERE
                        .build();
            }
        }
        return sInstance;
    }

    /** sample migration*/
    // @VisibleForTesting
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(final SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE cameras
//                    ADD COLUMN createdAt TEXT");
//        }
//    };


}
