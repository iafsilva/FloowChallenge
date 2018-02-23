package com.ivoafsilva.floowchallenge.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.ivoafsilva.floowchallenge.DataRepository;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.db.entity.StepEntity;
import com.ivoafsilva.floowchallenge.ui.MapActivity;
import com.ivoafsilva.floowchallenge.util.L;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link ViewModel} for the {@link MapActivity}
 */
public class MapViewModel extends AndroidViewModel {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * TAG prefix for logging
     */
    private static final String TAG = MapViewModel.class.getSimpleName();
    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * Provides access to our Repository to laod/save
     */
    private final DataRepository mDataRepository;

    /**
     * The user's active journey
     */
    private JourneyEntity mActiveJourney;

    /**
     * The user's journey steps
     */
    private List<StepEntity> mActiveJourneySteps;

    // ------------------------------------ METHODS -----------------------------------------
    public MapViewModel(Application context,
                        DataRepository dataRepository) {
        super(context);
        mDataRepository = dataRepository;
    }

    /**
     * Adds a step in the user's active journey.
     *
     * @param location The location to add to the journey
     */
    public void addJourneyStep(Location location) {
        if (mActiveJourney == null) {
            L.w(TAG, "addJourneyStep Trying to add a Step but there is no active journey.");
        }
        L.v(TAG, "addJourneyStep");
        mActiveJourneySteps.add(new StepEntity(mActiveJourney.getId(), location));
    }

    /**
     * Creates a new journey and saves it to DB
     */
    public void startJourney() {
        L.v(TAG, "startJourney");
        mActiveJourney = new JourneyEntity("No Name", new Date(System.currentTimeMillis()), null);
        mActiveJourneySteps = new ArrayList<>();
        mDataRepository.saveJourney(mActiveJourney);
    }

    /**
     * Ends the previously created journey, updates it in DB.
     * Also saves journey's steps
     */
    public void endJourney() {
        L.v(TAG, "endJourney");
        mActiveJourney.setEndTime(new Date(System.currentTimeMillis()));
        mDataRepository.saveJourneySteps(mActiveJourneySteps);
        mDataRepository.saveJourney(mActiveJourney);
        mActiveJourneySteps = null;
        mActiveJourney = null;
    }
}
