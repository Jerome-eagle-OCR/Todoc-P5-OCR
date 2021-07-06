package com.cleanup.todoc.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.relation.ProjectWithTasks;

import java.util.List;

@Dao
public interface ProjectDao {

    @Insert
    void insert(Project project);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM project_table ORDER BY id")
    LiveData<List<Project>> getProjects();

    @Transaction
    @Query("SELECT * FROM project_table ORDER BY name ASC")
    LiveData<List<ProjectWithTasks>> getProjectsAZWithTasks();

/*    @Query("SELECT * FROM project_table WHERE id = :givenId")
    Project getProjectById(long givenId);*/
}