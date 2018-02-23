package com.ivoafsilva.floowchallenge.model;

import java.util.Date;

/**
 * Interface for a Journey Model
 */

public interface Journey {
    String getId();

    String getName();

    Date getStartTime();

    Date getEndTime();
}

