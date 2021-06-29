package com.cleanup.todoc.model.repositories;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.model.DAOs.ProjectDao;
import com.cleanup.todoc.model.entities.Project;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao mProjectDao;
    private final LiveData<Project[]> allProjects;
    private final Executor doInBackground;

    public ProjectRepository(ProjectDao projectDao) {
        mProjectDao = projectDao;
        allProjects = mProjectDao.getProjects();
        doInBackground = Executors.newFixedThreadPool(2);
    }

    public void insert(Project project) {
        doInBackground.execute(() -> mProjectDao.insert(project));
    }

    public void delete(Project project) {
        doInBackground.execute(() -> mProjectDao.delete(project));
    }

    public LiveData<Project[]> getAllProjects() {
        return allProjects;
    }

    public Project getProjectById(long givenId) {
        Project[] projects = allProjects.getValue();
        for (Project project : Objects.requireNonNull(projects)) {
            if (project.getId() == givenId) return project;
        }
        return null;
    }
}
