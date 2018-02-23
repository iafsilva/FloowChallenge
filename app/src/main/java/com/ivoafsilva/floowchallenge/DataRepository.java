package com.ivoafsilva.floowchallenge;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.ivoafsilva.floowchallenge.db.AppDatabase;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.db.entity.StepEntity;
import com.ivoafsilva.floowchallenge.util.AppExecutors;

import java.util.List;


/**
 * Repository handling all the datasources. Currently only local (Database)
 */
public class DataRepository {
    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * The instance of this Singleton
     */
    private static volatile DataRepository sInstance;

    /**
     * Local Database to use by this repository
     */
    private final AppDatabase mDatabase;

    /**
     * Executors to be used when performing async operations
     */
    private final AppExecutors mAppExecutors;

    /**
     * Observable containing all the journeys
     */
    private MediatorLiveData<List<JourneyEntity>> mObservableJourneys;

    // ------------------------------------ METHODS -----------------------------------------
    private DataRepository(final AppDatabase database, final AppExecutors executors) {
        mDatabase = database;
        mObservableJourneys = new MediatorLiveData<>();
        mAppExecutors = executors;

        mObservableJourneys.addSource(mDatabase.journeyDao().loadAllJourneys(), new Observer<List<JourneyEntity>>() {
            @Override
            public void onChanged(@Nullable List<JourneyEntity> journeyEntities) {
                if (mDatabase.getDatabaseCreated().getValue() != null) {
                    mObservableJourneys.postValue(journeyEntities);
                }
            }
        });
    }

    /**
     * Get the list of journeys from the database and get notified when the data changes.
     */
    public LiveData<List<JourneyEntity>> getAllJourneys() {
        return mObservableJourneys;
    }

    /**
     * Get a journey from the database and get notified when the data changes.
     */
    public LiveData<JourneyEntity> loadJourney(final int journeyId) {
        return mDatabase.journeyDao().loadJourney(journeyId);
    }

    /**
     * Get the steps of a journey from the database and get notified when the data changes.
     */
    public LiveData<List<StepEntity>> loadSteps(final int journeyId) {
        return mDatabase.stepDao().loadSteps(journeyId);
    }

    /**
     * Saves a {@link JourneyEntity} async
     */
    public void saveJourney(final JourneyEntity journeyEntity) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.journeyDao().insert(journeyEntity);
            }
        });
    }

    /**
     * Saves a {@link List} of {@link StepEntity} async
     */
    public void saveJourneySteps(final List<StepEntity> journeySteps) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.stepDao().insertAll(journeySteps);
                journeySteps.clear();
            }
        });
    }

    // ------------------------------------ STATIC METHODS -----------------------------------------

    /**
     * Gets the instance of this Singleton
     */
    public static DataRepository getInstance(final AppDatabase database, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database, executors);
                }
            }
        }
        return sInstance;
    }
}
