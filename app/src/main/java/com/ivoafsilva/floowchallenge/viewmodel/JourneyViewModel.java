package com.ivoafsilva.floowchallenge.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ivoafsilva.floowchallenge.DataRepository;
import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;

import java.lang.ref.WeakReference;

/**
 * {@link ViewModel} for the {@link com.ivoafsilva.floowchallenge.ui.JourneyFragment}
 */
public class JourneyViewModel extends AndroidViewModel {
    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * The Id of the Journey to show with this model
     */
    private final String mJourneyId;
    /**
     * LiveData containing the {@link JourneyEntity}
     */
    private final LiveData<JourneyEntity> mObservableJourney;

    // ------------------------------------ METHODS -----------------------------------------

    public JourneyViewModel(@NonNull Application application, DataRepository dataRepository,
                            final String journeyId) {
        super(application);
        mJourneyId = journeyId;
        mObservableJourney = dataRepository.loadJourney(mJourneyId);
    }

    public LiveData<JourneyEntity> getObservableJourney() {
        return mObservableJourney;
    }


    // ------------------------------------ STATIC CLASSES -----------------------------------------

    /**
     * A Factory for creating and inject a journey dependency into {@link JourneyListViewModel} instance
     */
    public static class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        // ------------------------------------ VARIABLES -----------------------------------------
        /**
         * Provides access to Application (and lifecycle). {@link WeakReference} so it doesn't leak.
         */
        private final WeakReference<Application> mApplicationReference;
        /**
         * Access to Data Repository
         */
        private final DataRepository mDataRepository;
        /**
         * Journey Id to be injected when needed
         */
        private final String mJourneyId;

        // ------------------------------------ METHODS -----------------------------------------
        public ViewModelFactory(Application application, DataRepository repository, String journeyId) {
            mApplicationReference = new WeakReference<>(application);
            mDataRepository = repository;
            mJourneyId = journeyId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new JourneyViewModel(mApplicationReference.get(), mDataRepository, mJourneyId);
        }
    }
}
