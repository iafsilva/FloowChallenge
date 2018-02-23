package com.ivoafsilva.floowchallenge.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.ivoafsilva.floowchallenge.model.Journey;

import java.util.Date;
import java.util.UUID;

/**
 * Entity to represent a {@link Journey}
 */
@Entity(tableName = "journeys")
public class JourneyEntity implements Journey {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private Date startTime;
    private Date endTime;

    public JourneyEntity() {
    }

    public JourneyEntity(String name, Date startTime, Date endTime) {
        this(UUID.randomUUID().toString(), name, startTime, endTime);
    }

    public JourneyEntity(Journey journey) {
        this(journey.getId(), journey.getName(), journey.getStartTime(), journey.getEndTime());
    }

    private JourneyEntity(@NonNull String id, String name, Date startTime, Date endTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
