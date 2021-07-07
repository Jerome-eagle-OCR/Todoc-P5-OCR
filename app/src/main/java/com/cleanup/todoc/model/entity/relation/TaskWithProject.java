package com.cleanup.todoc.model.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;

public class TaskWithProject {
    @Embedded
    public Task task;
    @Relation(
            parentColumn = "project_id",
            entityColumn = "id",
            entity = Project.class
    )
    public Project project;
}
