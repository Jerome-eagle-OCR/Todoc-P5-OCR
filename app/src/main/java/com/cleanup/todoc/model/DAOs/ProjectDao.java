package com.cleanup.todoc.model.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cleanup.todoc.model.entities.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    @Insert
    void insert(Project project);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM project_table ORDER BY id")
    LiveData<Project[]> getProjects();

    @Query("SELECT * FROM project_table WHERE id = :givenId")
    Project getProjectById(long givenId);
}
