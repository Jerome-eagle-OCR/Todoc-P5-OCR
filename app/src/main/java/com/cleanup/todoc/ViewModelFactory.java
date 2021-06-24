package com.cleanup.todoc;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.todoc.model.repositories.ProjectRepository;
import com.cleanup.todoc.model.repositories.TaskRepository;
import com.cleanup.todoc.ui.MainViewModel;
import com.cleanup.todoc.utils.TodocApplication;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
                            new ProjectRepository(TodocApplication.getInstance()),
                            new TaskRepository(TodocApplication.getInstance())
                    );
                }
            }
        }
        return factory;
    }

    @NonNull
    private final ProjectRepository projectRepository;
    @NonNull
    private final TaskRepository taskRepository;

    private ViewModelFactory(
            @NonNull ProjectRepository projectRepository,
            @NonNull TaskRepository taskRepository
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(projectRepository, taskRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
