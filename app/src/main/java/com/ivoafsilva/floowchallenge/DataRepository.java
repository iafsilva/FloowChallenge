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
 * Repository handling the work with journeys and steps
 */
public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    private final AppExecutors appExecutors;

    private MediatorLiveData<List<JourneyEntity>> mObservableProducts;

    private DataRepository(final AppDatabase database, final AppExecutors executors) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();
        appExecutors = executors;

        mObservableProducts.addSource(mDatabase.journeyDao().loadAllJourneys(), new Observer<List<JourneyEntity>>() {
            @Override
            public void onChanged(@Nullable List<JourneyEntity> journeyEntities) {
                if (mDatabase.getDatabaseCreated().getValue() != null) {
                    mObservableProducts.postValue(journeyEntities);
                }
            }
        });
    }

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

    /**
     * Get the list of journeys from the database and get notified when the data changes.
     */
    public LiveData<List<JourneyEntity>> getAllJourneys() {
        return mObservableProducts;
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

    public void saveJourney(final JourneyEntity journeyEntity) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.journeyDao().insert(journeyEntity);
            }
        });
    }

    public void saveJourneySteps(final List<StepEntity> journeySteps) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.stepDao().insertAll(journeySteps);
                journeySteps.clear();
            }
        });
    }
}
