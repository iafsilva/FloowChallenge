package com.ivoafsilva.floowchallenge.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;

import com.ivoafsilva.floowchallenge.model.Step;

import java.util.Date;
import java.util.UUID;

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
    @NonNull
    @PrimaryKey
    private String id;
    @NonNull
    private String journeyId;
    private double latitude;
    private double longitude;
    private double altitude;
    private Date timestamp;

    public StepEntity() {
    }

    public StepEntity(String journeyId, Location location) {
        this(UUID.randomUUID().toString(), journeyId, location.getLatitude(), location.getLongitude(), location.getAltitude(), new Date(location.getTime()));
    }

    public StepEntity(Step step) {
        this(step.getId(), step.getJourneyId(), step.getLatitude(), step.getLongitude(), step.getAltitude(), step.getTimestamp());
    }

    public StepEntity(@NonNull String journeyId, double latitude, double longitude, double altitude, Date timestamp) {
        this(UUID.randomUUID().toString(), journeyId, latitude, longitude, altitude, timestamp);
    }

    private StepEntity(@NonNull String id, @NonNull String journeyId, double latitude, double longitude, double altitude, Date timestamp) {
        this.id = id;
        this.journeyId = journeyId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
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
    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(@NonNull String journeyId) {
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
