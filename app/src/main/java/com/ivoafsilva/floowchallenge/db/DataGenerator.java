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

package com.ivoafsilva.floowchallenge.db;

import com.ivoafsilva.floowchallenge.db.entity.JourneyEntity;
import com.ivoafsilva.floowchallenge.db.entity.StepEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Generates data to pre-populate the database
 */
public class DataGenerator {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * Mock Data for a journey:
     * [0] Id: first_journey
     * [1] Name: First Journey
     * [2] StartTime: 02/20/2018 @ 2:00pm (UTC)
     * [3] EndTime: 02/20/2018 @ 3:00pm (UTC)
     */
    private static final String[] JOURNEY = new String[]{"first_journey", "First Journey", "1519135200000", "1519138800000"};

    /**
     * Mock Data for a step
     * [0] Latitude: 53.3872
     * [1] Longitude: -1.4636
     * [2] Altitude: 0
     */
    private static final double[] STEP = new double[]{39.7495, 8.8077, 0};

    // ------------------------------------ STATIC METHODS -----------------------------------------

    public static JourneyEntity generateJourney() {
        JourneyEntity journeyEntity = new JourneyEntity();
        journeyEntity.setId(JOURNEY[0]);
        journeyEntity.setName(JOURNEY[1]);
        journeyEntity.setStartTime(new Date(Long.parseLong(JOURNEY[2])));
        journeyEntity.setEndTime(new Date(Long.parseLong(JOURNEY[3])));
        return journeyEntity;
    }

    public static List<StepEntity> generateStepsForJourney(final JourneyEntity journeyEntity) {
        StepEntity stepEntity = new StepEntity(journeyEntity.getId(), STEP[0], STEP[1], STEP[2], new Date(Long.parseLong(JOURNEY[2]) + 10000));
        return Collections.singletonList(stepEntity);
    }
}
