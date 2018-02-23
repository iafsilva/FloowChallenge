package com.ivoafsilva.floowchallenge.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivoafsilva.floowchallenge.FloowApplication;
import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.util.L;
import com.ivoafsilva.floowchallenge.viewmodel.JourneyViewModel;

/**
 * Fragment in charge of showing a specific {@link com.ivoafsilva.floowchallenge.model.Journey}
 */
public class JourneyFragment extends Fragment {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * TAG prefix for logging
     */
    public static final String TAG = JourneyFragment.class.getSimpleName();

    /**
     * Key to be used when saving/retrieving a journey_id
     */
    private static final String KEY_JOURNEY_ID = "journey_id";

    // ------------------------------------ VARIABLES -----------------------------------------

    /**
     * TextView containing the Journey Name
     */
    private TextView mJourneyNameTextView;
    /**
     * TextView containing the Journey Start Time
     */
    private TextView mJourneyStartTimeTextView;
    /**
     * TextView containing the Journey End Time
     */
    private TextView mJourneyEndTimeTextView;

    // ------------------------------------ METHODS -----------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_journey, container, false);
        mJourneyNameTextView = view.findViewById(R.id.journey_name);
        mJourneyStartTimeTextView = view.findViewById(R.id.journey_start_time);
        mJourneyEndTimeTextView = view.findViewById(R.id.journey_end_time);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloowApplication application = (FloowApplication) getActivity().getApplication();
        JourneyViewModel.ViewModelFactory factory = new JourneyViewModel.ViewModelFactory(application, application.getRepository(), getArguments().getString(KEY_JOURNEY_ID));
        JourneyViewModel model = ViewModelProviders.of(this, factory).get(JourneyViewModel.class);
        observeModel(model);
    }

    /**
     * Observes the content from the ViewModel and updates onChange
     */
    private void observeModel(final JourneyViewModel model) {
        model.getObservableJourney().observe(this, new Observer<JourneyEntity>() {
            @Override
            public void onChanged(@Nullable JourneyEntity journeyEntity) {
                if (journeyEntity == null) {
                    L.w(TAG, "observeModel journeyEntity IS NULL. Returning.");
                    return;
                }
                mJourneyNameTextView.setText(journeyEntity.getName());
                mJourneyStartTimeTextView.setText(journeyEntity.getStartTime().toString());
                mJourneyEndTimeTextView.setText(journeyEntity.getEndTime().toString());
            }
        });
    }

    /**
     * Creates journey fragment for specific journey ID
     */
    public static JourneyFragment forJourney(String journeyId) {
        JourneyFragment fragment = new JourneyFragment();
        Bundle args = new Bundle();
        args.putString(KEY_JOURNEY_ID, journeyId);
        fragment.setArguments(args);
        return fragment;
    }
}
