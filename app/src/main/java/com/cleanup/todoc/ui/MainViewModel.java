package com.cleanup.todoc.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.Utils;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.cleanup.todoc.model.repositories.ProjectRepository;
import com.cleanup.todoc.model.repositories.TaskRepository;

import java.util.Collections;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LiveData<Project[]> allProjects;
    private final LiveData<List<Task>> allTasksOldNew;
    private final LiveData<List<Task>> allTasksProjectSorting;
    private Utils.SortMethod sortMethod = Utils.SortMethod.NONE;


    public MainViewModel(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        allProjects = projectRepository.getAllProjects();
        allTasksOldNew = taskRepository.getAllTasksOldNew();
        allTasksProjectSorting = taskRepository.getAllTasksProjectSorting();
    }

    public void insertProject(Project project) {
        projectRepository.insert(project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }
/*
    public Project getProjectById(long givenId) {
        return projectRepository.getProjectById(givenId);
    }*/

    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }
/*
    public List<Task> getTasksByProjectId(long givenProjectId) {
        return taskRepository.getTasksByProjectId(givenProjectId);
    }*/

    public LiveData<Project[]> getAllProjects() {
        return allProjects;
    }
/*
    public LiveData<List<Task>> getAllTasksOldNew() {
        return allTasksOldNew;
    }*/
/*
    public LiveData<List<Task>> getAllTasksProjectSorting() {
        return allTasksProjectSorting;
    }*/

    public void setSortMethod(Utils.SortMethod sortMethod) {
        this.sortMethod = sortMethod;
    }

    public LiveData<List<Task>> getSortedTaskList() {
        switch (sortMethod) {
            case OLD_FIRST:
                return Transformations.map(allTasksOldNew, taskList -> taskList);
            case RECENT_FIRST:
                return Transformations.map(allTasksOldNew, taskList -> {
                    Collections.reverse(taskList);
                    return taskList;
                });
            case PROJECT_ID_ORDER:
                return Transformations.map(allTasksProjectSorting, taskList -> taskList);
            case NONE:
                return Transformations.map(allTasksOldNew, taskList -> {
                    Collections.shuffle(taskList);
                    return taskList;
                });
            default:
                throw new IllegalStateException("Unexpected value: " + sortMethod);
        }
    }
}
