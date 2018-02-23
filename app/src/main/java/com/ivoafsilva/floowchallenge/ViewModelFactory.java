/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ivoafsilva.floowchallenge;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ivoafsilva.floowchallenge.viewmodel.JourneyListViewModel;
import com.ivoafsilva.floowchallenge.viewmodel.MapViewModel;

import java.lang.ref.WeakReference;

/**
 * A Factory for creating and inject dependencies into {@link ViewModel} instances
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * The instance of this Singleton
     */
    private static volatile ViewModelFactory INSTANCE;

    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * Provides access to Application (and lifecycle). {@link WeakReference} so it doesn't leak.
     */
    private final WeakReference<Application> mApplicationReference;
    /**
     * Access to Data Repository
     */
    private final DataRepository mDataRepository;

    // ------------------------------------ METHODS -----------------------------------------
    private ViewModelFactory(Application application, DataRepository repository) {
        mApplicationReference = new WeakReference<>(application);
        mDataRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            //noinspection unchecked
            return (T) new MapViewModel(mApplicationReference.get(), mDataRepository);
        } else if (modelClass.isAssignableFrom(JourneyListViewModel.class)) {
            //noinspection unchecked
            return (T) new JourneyListViewModel(mApplicationReference.get(), mDataRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

    // ------------------------------------ STATIC METHODS -----------------------------------------

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application,
                            ((FloowApplication) application).getRepository());
                }
            }
        }
        return INSTANCE;
    }
}
