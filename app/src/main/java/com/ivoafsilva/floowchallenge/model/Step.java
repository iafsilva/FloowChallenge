package com.ivoafsilva.floowchallenge.model;

import java.util.Date;

/**
 * Interface for a Step Model
 */
public interface Step {
    int getId();

    int getJourneyId();

    double getLatitude();

    double getLongitude();

    double getAltitude();

    Date getTimestamp();
}

