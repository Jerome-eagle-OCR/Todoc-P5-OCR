package com.cleanup.todoc.model.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;

public class TaskWithProject {
    @Embedded
    private Task task;
    @Relation(
            parentColumn = "project_id",
            entityColumn = "projectId",
            entity = Project.class
    )
    private Project project;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
