package com.ivoafsilva.floowchallenge;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.ivoafsilva.floowchallenge.db.AppDatabase;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.db.entity.StepEntity;

import java.util.List;


/**
 * Repository handling the work with journeys and steps
 */
public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<JourneyEntity>> mObservableProducts;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        mObservableProducts.addSource(mDatabase.journeyDao().loadAllJourneys(), new Observer<List<JourneyEntity>>() {
            @Override
            public void onChanged(@Nullable List<JourneyEntity> journeyEntities) {
                if (mDatabase.getDatabaseCreated().getValue() != null) {
                    mObservableProducts.postValue(journeyEntities);
                }
            }
        });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
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
}
