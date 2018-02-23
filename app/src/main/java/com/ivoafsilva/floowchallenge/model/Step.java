package com.ivoafsilva.floowchallenge.model;

import java.util.Date;

/**
 * Interface for a Step Model.
 * A Step is a location associated to a journey.
 */
public interface Step {
    String getId();

    String getJourneyId();

    double getLatitude();

    double getLongitude();

    double getAltitude();

    Date getTimestamp();
}

