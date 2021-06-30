package com.cleanup.todoc.model.repositories;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cleanup.todoc.model.DAOs.ProjectDao;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.ProjectWithTasks;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao mProjectDao;
    private final LiveData<Project[]> allProjects;
    private final LiveData<ProjectWithTasks> projectsWithTasks;
    private final Executor doInBackground;
    private final MutableLiveData<HashMap<Long, Project>> projectsMappedById = new MutableLiveData<>();

    public ProjectRepository(ProjectDao projectDao) {
        mProjectDao = projectDao;
        allProjects = mProjectDao.getProjects();
        projectsWithTasks = mProjectDao.getProjectsWithTasks();
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

    public LiveData<HashMap<Long, Project>> getProjectsMappedById() {
        return Transformations.switchMap(allProjects, new Function<Project[], LiveData<HashMap<Long, Project>>>() {
            @Override
            public LiveData<HashMap<Long, Project>> apply(Project[] input) {
                HashMap<Long, Project> output = new HashMap<>();
                for (Project project : input) {
                    output.put(project.getId(), project);
                }
                projectsMappedById.setValue(output);
                return projectsMappedById;
            }
        });
    }

    public LiveData<ProjectWithTasks> getProjectsWithTasks() {
        return projectsWithTasks;
    }
}
