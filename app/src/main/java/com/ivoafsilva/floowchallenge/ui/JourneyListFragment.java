package com.ivoafsilva.floowchallenge.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.ViewModelFactory;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.model.Journey;
import com.ivoafsilva.floowchallenge.viewmodel.JourneyListViewModel;

import java.util.List;

/**
 * Fragment that shows the list of available Journeys
 */
public class JourneyListFragment extends Fragment {
    /**
     * TAG prefix for logging
     */
    public static final String TAG = JourneyListFragment.class.getSimpleName();
    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * The RecyclerView instance
     */
    private RecyclerView mJourneysRecycler;
    /**
     * The Adapter that goes along with the {@link RecyclerView}
     */
    private JourneyRecycleAdapter mJourneyAdapter;
    /**
     * TextView to show while data is loading.
     */
    private TextView mLoadingTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_journeys, container, false);
        mJourneysRecycler = view.findViewById(R.id.journeys_recycler);
        mLoadingTextView = view.findViewById(R.id.loading_tv);

        mJourneyAdapter = new JourneyRecycleAdapter(mJourneyClickCallback);
        mJourneysRecycler.setAdapter(mJourneyAdapter);
        mJourneysRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());
        JourneyListViewModel viewModel = ViewModelProviders.of(this, factory).get(JourneyListViewModel.class);
        observeModel(viewModel);
    }

    /**
     * Observes the content from the ViewModel and updates onChange
     */
    private void observeModel(JourneyListViewModel journeyListViewModel) {
        journeyListViewModel.getJourneys().observe(this, new Observer<List<JourneyEntity>>() {
            @Override
            public void onChanged(@Nullable List<JourneyEntity> myJourneys) {
                if (myJourneys != null) {
                    mLoadingTextView.setVisibility(View.GONE);
                    mJourneysRecycler.setVisibility(View.VISIBLE);
                    mJourneyAdapter.setJourneyList(myJourneys);
                } else {
                    mLoadingTextView.setVisibility(View.VISIBLE);
                    mJourneysRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Click listener to be setted onto each item on the list
     */
    private final JourneyClickCallback mJourneyClickCallback = new JourneyClickCallback() {
        @Override
        public void onClick(Journey journey) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ((JourneysActivity) getActivity()).show(journey);
            }
        }
    };

}
