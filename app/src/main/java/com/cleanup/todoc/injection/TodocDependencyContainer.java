package com.cleanup.todoc.injection;

import android.app.Application;

import com.cleanup.todoc.db.TodocDatabase;
import com.cleanup.todoc.model.repository.ProjectRepository;
import com.cleanup.todoc.model.repository.TaskRepository;

public class TodocDependencyContainer {

    public final ProjectRepository projectRepository;
    public final TaskRepository taskRepository;

    public TodocDependencyContainer(Application application) {
        TodocDatabase database = TodocDatabase.getInstance(application);
        projectRepository = new ProjectRepository(database.projectDao());
        taskRepository = new TaskRepository(database.taskDao());
    }


}
