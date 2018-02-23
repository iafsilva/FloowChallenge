package com.ivoafsilva.floowchallenge.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ivoafsilva.floowchallenge.db.entity.StepEntity;

import java.util.List;

@Dao
public interface StepDao {

    @Query("SELECT * FROM steps WHERE journeyId = :journeyId")
    LiveData<List<StepEntity>> loadSteps(String journeyId);

    @Query("SELECT * FROM steps WHERE journeyId = :journeyId")
    List<StepEntity> loadStepsSync(String journeyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<StepEntity> steps);
}
