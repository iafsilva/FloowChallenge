/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivoafsilva.floowchallenge;

import android.app.Application;

import com.ivoafsilva.floowchallenge.db.AppDatabase;
import com.ivoafsilva.floowchallenge.util.AppExecutors;
import com.ivoafsilva.floowchallenge.util.L;
import com.squareup.leakcanary.LeakCanary;

/**
 * Android Application class. Used for accessing singletons.
 */
public class FloowApplication extends Application {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * TAG prefix for logging
     */
    private static final String TAG = FloowApplication.class.getSimpleName();
    // ------------------------------------ VARIABLES -----------------------------------------

    /**
     * Executors to be used when performing async operations
     */
    private AppExecutors mAppExecutors;
    // ------------------------------------ METHODS -----------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        L.v(TAG, "onCreate");

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase(), mAppExecutors);
    }
}
