package com.cleanup.todoc.model.entities.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;

import java.util.List;


public class ProjectWithTasks {
    @Embedded public Project project;
    @Relation(
            parentColumn = "id",
            entityColumn = "project_id",
            entity = Task.class
    )
    public List<Task> tasks;
}