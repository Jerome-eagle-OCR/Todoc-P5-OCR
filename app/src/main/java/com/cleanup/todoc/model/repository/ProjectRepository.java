package com.cleanup.todoc.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.cleanup.todoc.model.dao.ProjectDao;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.entity.relation.ProjectWithTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao mProjectDao;
    private final LiveData<List<Project>> allProjects;
    private final LiveData<List<ProjectWithTasks>> allProjectsAZWithTasks;
    private final Executor doInBackground;

    public ProjectRepository(ProjectDao projectDao) {
        mProjectDao = projectDao;
        allProjects = mProjectDao.getProjects();
        allProjectsAZWithTasks = mProjectDao.getProjectsAZWithTasks();
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

    public LiveData<HashMap<Long, Project>> getProjectsHashMappedById() {
        return Transformations.map(allProjects, input -> {
            HashMap<Long, Project> output = new HashMap<>();
            for (Project project : input) output.put(project.getId(), project);

            return output;
        });
    }

    public LiveData<List<Task>> getAllTasksProjectAZ() {
        return Transformations.map(allProjectsAZWithTasks, input -> {
            List<Task> output = new ArrayList<>();
            for (ProjectWithTasks projectWithTasks : input) output.addAll(projectWithTasks.tasks);

            return output;
        });
    }
}
