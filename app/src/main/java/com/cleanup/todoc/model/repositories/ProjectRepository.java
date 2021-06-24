package com.cleanup.todoc.model.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.DB.TodocDatabase;
import com.cleanup.todoc.model.DAOs.ProjectDao;
import com.cleanup.todoc.model.entities.Project;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao projectDao;
    private LiveData<Project[]> allProjects;
    private final Executor doInBackground;

    public ProjectRepository(Application application) {
        TodocDatabase database = TodocDatabase.getInstance(application);
        projectDao = database.projectDao();
        allProjects = projectDao.getProjects();
        doInBackground = Executors.newFixedThreadPool(2);
    }

    public void insert(Project project) {
        doInBackground.execute(() -> projectDao.insert(project));
    }

    public void delete(Project project) {
        doInBackground.execute(() -> projectDao.delete(project));
    }

    public LiveData<Project[]> getAllProjects() {
        return allProjects;
    }

    public Project getProjectById(long givenId) {
        return projectDao.getProjectById(givenId);
    }
}
