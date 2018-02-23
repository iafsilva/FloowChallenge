package com.ivoafsilva.floowchallenge.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.ivoafsilva.floowchallenge.DataRepository;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;

import java.util.List;

/**
 * {@link ViewModel} for the {@link com.ivoafsilva.floowchallenge.ui.JourneysActivity}
 */
public class JourneysViewModel extends AndroidViewModel {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * TAG prefix for logging
     */
    private static final String TAG = JourneysViewModel.class.getSimpleName();
    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * Provides access to our Repository to laod/save
     */
    private final DataRepository mDataRepository;

    /**
     * Multi-source observable containing all journeys
     */
    private final MediatorLiveData<List<JourneyEntity>> mObservableJourneys;

    // ------------------------------------ METHODS -----------------------------------------
    public JourneysViewModel(Application context,
                             DataRepository dataRepository) {
        super(context);
        mDataRepository = dataRepository;

        mObservableJourneys = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableJourneys.setValue(null);

        LiveData<List<JourneyEntity>> journeys = dataRepository.getAllJourneys();

        // observe the changes of the journeys from the database and forward them
        mObservableJourneys.addSource(journeys, new Observer<List<JourneyEntity>>() {
            @Override
            public void onChanged(@Nullable List<JourneyEntity> journeyEntities) {
                mObservableJourneys.setValue(journeyEntities);
            }
        });
    }

    public LiveData<List<JourneyEntity>> getJourneys() {
        return mObservableJourneys;
    }
}
