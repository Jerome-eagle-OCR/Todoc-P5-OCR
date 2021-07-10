package com.cleanup.todoc.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Transaction
    @Query("SELECT * FROM task_table ORDER BY creation_timestamp ASC")
    LiveData<List<TaskWithProject>> getAllTaskWithProject();

    @Transaction
    @Query("SELECT * FROM project_table INNER JOIN task_table " +
            "ON project_table.id= task_table.project_id ORDER BY project_table.name ASC")
    LiveData<List<TaskWithProject>> getAllTaskWithProjectAZ();
}
