package com.cleanup.todoc.DI;

import android.app.Application;

import com.cleanup.todoc.DB.TodocDatabase;
import com.cleanup.todoc.model.repositories.ProjectRepository;
import com.cleanup.todoc.model.repositories.TaskRepository;

public class TodocDependencyContainer {

    public final ProjectRepository projectRepository;
    public final TaskRepository taskRepository;

    public TodocDependencyContainer(Application application) {
        TodocDatabase database = TodocDatabase.getInstance(application);
        projectRepository = new ProjectRepository(database.projectDao());
        taskRepository = new TaskRepository(database.taskDao());
    }


}
