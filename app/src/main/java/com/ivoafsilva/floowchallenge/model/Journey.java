package com.ivoafsilva.floowchallenge.model;

import java.util.Date;

/**
 * Interface for a Journey Model.
 * A Journey is sequence of recorded locations in time.
 */
public interface Journey {
    String getId();

    String getName();

    Date getStartTime();

    Date getEndTime();
}

