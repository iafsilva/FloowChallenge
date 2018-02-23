package com.ivoafsilva.floowchallenge.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.location.Location;

import com.ivoafsilva.floowchallenge.DataRepository;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.db.entity.StepEntity;
import com.ivoafsilva.floowchallenge.util.L;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapViewModel extends AndroidViewModel {

    /**
     * TAG prefix for logging
     */
    private static final String TAG = MapViewModel.class.getSimpleName();

    private final DataRepository mDataRepository;

    private JourneyEntity activeJourney;

    private List<StepEntity> activeJourneySteps;

    public MapViewModel(Application context,
                        DataRepository dataRepository) {
        super(context);
        mDataRepository = dataRepository;
    }

    public void addJourneyStep(Location location) {
        if (activeJourney == null) {
            L.w(TAG, "addJourneyStep Trying to add Steps but there is no active journey.");
        }
        L.v(TAG, "addJourneyStep");
        activeJourneySteps.add(new StepEntity(activeJourney.getId(), location));
    }

    public void startJourney() {
        L.v(TAG, "startJourney");
        activeJourney = new JourneyEntity("No Name", new Date(System.currentTimeMillis()), null);
        activeJourneySteps = new ArrayList<>();
        mDataRepository.saveJourney(activeJourney);
    }

    public void endJourney() {
        L.v(TAG, "endJourney");
        activeJourney.setEndTime(new Date(System.currentTimeMillis()));
        mDataRepository.saveJourneySteps(activeJourneySteps);
        mDataRepository.saveJourney(activeJourney);
        activeJourneySteps = null;
        activeJourney = null;
    }
}
