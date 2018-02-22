package com.ivoafsilva.floowchallenge.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.ivoafsilva.floowchallenge.model.Journey;

import java.util.Date;

/**
 * Entity to represent a {@link Journey}
 */
@Entity(tableName = "journeys")
public class JourneyEntity implements Journey {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Date startTime;
    private Date endTime;

    public JourneyEntity() {
    }

    public JourneyEntity(int id, String name, Date startTime, Date endTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public JourneyEntity(Journey journey) {
        this.id = journey.getId();
        this.name = journey.getName();
        this.startTime = journey.getStartTime();
        this.endTime = journey.getEndTime();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
