package com.ivoafsilva.floowchallenge.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ivoafsilva.floowchallenge.db.converter.DateConverter;
import com.ivoafsilva.floowchallenge.db.dao.JourneyDao;
import com.ivoafsilva.floowchallenge.db.dao.StepDao;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.db.entity.StepEntity;
import com.ivoafsilva.floowchallenge.util.AppExecutors;
import com.ivoafsilva.floowchallenge.util.L;

import java.util.List;


@Database(entities = {JourneyEntity.class, StepEntity.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    // ------------------------ CONSTANTS -------------------------
    /**
     * TAG prefix for logging
     */
    private static final String TAG = AppDatabase.class.getSimpleName();

    /**
     * The name of the database
     */
    private static final String DATABASE_NAME = "floow-location-challenge.db";

    /**
     * The instance of this Singleton
     */
    private static volatile AppDatabase sInstance;
    // ------------------------ VARIABLES -------------------------
    /**
     * Observable stating whether the database is created
     */
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();
    // ------------------------------------ METHODS -----------------------------------------

    /**
     * To be implemented by {@link Room}
     */
    public abstract JourneyDao journeyDao();

    /**
     * To be implemented by {@link Room}
     */
    public abstract StepDao stepDao();

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        L.v(TAG, "setDatabaseCreated");
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    // ------------------------------------ STATIC METHODS -----------------------------------------

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        L.v(TAG, "buildDatabase");
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                // Generate the data for pre-population
                                AppDatabase database = AppDatabase.getInstance(appContext, executors);
                                JourneyEntity journey = DataGenerator.generateJourney();
                                List<StepEntity> steps = DataGenerator.generateStepsForJourney(journey);
                                // insert it
                                insertData(database, journey, steps);
                                // notify that the database was created and it's ready to be used
                                database.setDatabaseCreated();
                            }
                        });
                    }
                }).build();
    }

    /**
     * Private method used for inserting pre-populated data
     */
    private static void insertData(final AppDatabase database, final JourneyEntity journey,
                                   final List<StepEntity> steps) {
        database.runInTransaction(new Runnable() {
            @Override
            public void run() {
                database.journeyDao().insert(journey);
                database.stepDao().insertAll(steps);
            }
        });
    }
}
