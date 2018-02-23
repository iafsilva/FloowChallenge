package com.ivoafsilva.floowchallenge.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.ViewModelFactory;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.viewmodel.JourneysViewModel;

import java.util.List;

public class JourneysActivity extends AppCompatActivity {
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

    // ------------------------------------ METHODS -----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journeys);

        mJourneysRecycler = findViewById(R.id.journeys_recycler);
        mLoadingTextView = findViewById(R.id.loading_tv);

        mJourneyAdapter = new JourneyRecycleAdapter(null);
        mJourneysRecycler.setAdapter(mJourneyAdapter);
        mJourneysRecycler.setLayoutManager(new LinearLayoutManager(this));

        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        JourneysViewModel viewModel = ViewModelProviders.of(this, factory).get(JourneysViewModel.class);
        subscribeToUIEvents(viewModel);
    }

    private void subscribeToUIEvents(JourneysViewModel journeysViewModel) {
        journeysViewModel.getJourneys().observe(this, new Observer<List<JourneyEntity>>() {
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
}
