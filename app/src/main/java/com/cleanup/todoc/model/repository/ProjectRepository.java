package com.cleanup.todoc.model.repository;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.model.dao.ProjectDao;
import com.cleanup.todoc.model.entity.Project;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao mProjectDao;
    private final LiveData<List<Project>> allProjects;
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

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }
}
