package com.cleanup.todoc.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cleanup.todoc.model.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task_table ORDER BY creation_timestamp ASC")
    LiveData<List<Task>> getAllTasksOldNew();

/*    @Query("SELECT * FROM task_table ORDER BY project_id DESC")
    LiveData<List<Task>> getAllTasksProjectSorting();

    @Query("SELECT * FROM task_table WHERE projectId = :givenProjectId ORDER BY creation_timestamp ASC")
    List<Task> getTasksByProjectId(long givenProjectId);*/
}
