package com.cleanup.todoc.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.cleanup.todoc.model.repositories.ProjectRepository;
import com.cleanup.todoc.model.repositories.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private LiveData<Project[]> allProjects;
    private LiveData<ArrayList<Task>> allTasksOld;
    private LiveData<List<Task>> allTasksProjectAZ;

    public MainViewModel(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        allProjects = projectRepository.getAllProjects();
        allTasksOld = taskRepository.getAllTasksOld();
        allTasksProjectAZ = taskRepository.getAllTasksProjectAZ();
    }

    public void insertProject(Project project) {
        projectRepository.insert(project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public Project getProjectById(long givenId) {
        return projectRepository.getProjectById(givenId);
    }

    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public List<Task> getTasksByProjectId(long givenProjectId) {
        return taskRepository.getTasksByProjectId(givenProjectId);
    }

    public LiveData<Project[]> getAllProjects() {
        return allProjects;
    }

    public LiveData<ArrayList<Task>> getAllTasksOld() {
        return allTasksOld;
    }

    public LiveData<List<Task>> getAllTasksProjectAZ() {
        return allTasksProjectAZ;
    }
}
