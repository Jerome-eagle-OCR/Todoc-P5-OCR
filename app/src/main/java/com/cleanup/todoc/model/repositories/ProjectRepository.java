package com.cleanup.todoc.model.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.cleanup.todoc.model.DAOs.ProjectDao;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.cleanup.todoc.model.entities.relation.ProjectWithTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao mProjectDao;
    private final LiveData<Project[]> allProjects;
    private final LiveData<List<ProjectWithTasks>> projectsAZWithTasks;
    private final Executor doInBackground;

    public ProjectRepository(ProjectDao projectDao) {
        mProjectDao = projectDao;
        allProjects = mProjectDao.getProjects();
        projectsAZWithTasks = mProjectDao.getProjectsAZWithTasks();
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

    public LiveData<HashMap<Long, Project>> getProjectsHashMappedById() {
        return Transformations.map(allProjects, input -> {
            HashMap<Long, Project> output = new HashMap<>();
            for (Project project : input) output.put(project.getId(), project);

            return output;
        });
    }

    public LiveData<List<Task>> getAllTasksProjectAZ() {
        return Transformations.map(projectsAZWithTasks, input -> {
            ArrayList<Task> output = new ArrayList<>();
            for (ProjectWithTasks projectWithTasks : input) output.addAll(projectWithTasks.tasks);

            return output;
        });
    }
}
