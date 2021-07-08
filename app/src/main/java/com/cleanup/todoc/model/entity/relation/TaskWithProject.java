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
            entityColumn = "id",
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskWithProject that = (TaskWithProject) o;

        if (!getTask().equals(that.getTask())) return false;
        return getProject().equals(that.getProject());
    }

    @Override
    public int hashCode() {
        int result = getTask().hashCode();
        result = 31 * result + getProject().hashCode();
        return result;
    }
}
