package com.ivoafsilva.floowchallenge.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import com.ivoafsilva.floowchallenge.model.Step;

import java.util.Date;

/**
 * Entity to represent a {@link Step}
 */
@Entity(tableName = "steps",
        foreignKeys = {
                @ForeignKey(entity = JourneyEntity.class,
                        parentColumns = "id",
                        childColumns = "journeyId",
                        onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "journeyId")})
public class StepEntity implements Step {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int journeyId;
    private double latitude;
    private double longitude;
    private double altitude;
    private Date timestamp;

    public StepEntity() {
    }

    public StepEntity(int id, int journeyId, double latitude, double longitude, double altitude, Date timestamp) {
        this.id = id;
        this.journeyId = journeyId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    public StepEntity(Step step) {
        this.id = step.getId();
        this.journeyId = step.getJourneyId();
        this.latitude = step.getLatitude();
        this.longitude = step.getLongitude();
        this.altitude = step.getAltitude();
        this.timestamp = step.getTimestamp();
    }

    public StepEntity(int journeyId, Location location) {
        this.id = 0;
        this.journeyId = journeyId;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.timestamp = new Date(location.getTime());
    }


    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
