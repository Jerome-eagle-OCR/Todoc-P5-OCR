package com.cleanup.todoc.model.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cleanup.todoc.model.entities.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task_table ORDER BY creation_timestamp ASC")
    LiveData<List<Task>> getAllTasksOld();

    @Query("SELECT * FROM task_table ORDER BY projectId DESC")
    LiveData<List<Task>> getAllTasksProjectAZ();

    @Query("SELECT * FROM task_table WHERE projectId = :givenProjectId ORDER BY creation_timestamp ASC")
    List<Task> getTasksByProjectId(long givenProjectId);
}
