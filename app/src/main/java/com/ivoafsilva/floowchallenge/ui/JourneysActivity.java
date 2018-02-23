package com.ivoafsilva.floowchallenge.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.model.Journey;

public class JourneysActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journeys);

        // Add journeys list fragment if first creation
        if (savedInstanceState == null) {
            JourneyListFragment fragment = new JourneyListFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.journey_fragment_container, fragment, JourneyListFragment.TAG)
                    .commit();
        }

    }

    /**
     * Creates and shows {@link JourneyFragment} when Journey info is requested
     */
    public void show(Journey journey) {
        JourneyFragment journeyFragment = JourneyFragment.forJourney(journey.getId());
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("journey")
                .replace(R.id.journey_fragment_container, journeyFragment, JourneyFragment.TAG)
                .commit();
    }
}
