package com.cleanup.todoc.model.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;


public class ProjectWithTasks {
    @Embedded public Project project;
    @Relation(
            parentColumn = "id",
            entityColumn = "project_id"
    )

    public List<Task> projectTasks;
}
