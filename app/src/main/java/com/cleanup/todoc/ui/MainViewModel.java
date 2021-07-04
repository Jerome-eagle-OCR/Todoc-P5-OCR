package com.cleanup.todoc.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.Utils;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.cleanup.todoc.model.repositories.ProjectRepository;
import com.cleanup.todoc.model.repositories.TaskRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LiveData<Project[]> allProjects;
    private final LiveData<HashMap<Long, Project>> projectsMappedById;
    private final LiveData<List<Task>> allTasksOldNew;
    private final LiveData<List<Task>> allTasksProjectAZ;
    private final MutableLiveData<Utils.SortMethod> sortMethod = new MutableLiveData<>(Utils.SortMethod.NONE);
    private final MediatorLiveData<List<Task>> sortedListForDisplay = new MediatorLiveData<>();
    private boolean allTasksEmpty;
    private boolean firstTasksEmptyTest = true;


    public MainViewModel(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        allProjects = projectRepository.getAllProjects();
        projectsMappedById = projectRepository.getProjectsHashMappedById();
        allTasksOldNew = taskRepository.getAllTasksOldNew();
        allTasksProjectAZ = projectRepository.getAllTasksProjectAZ();
        sortedListForDisplay.addSource(allTasksProjectAZ, tasks -> sortTaskList());
        sortedListForDisplay.addSource(allTasksOldNew, tasks -> sortTaskList());
    }


    public void insertProject(Project project) {
        projectRepository.insert(project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public LiveData<Project[]> getAllProjects() {
        return allProjects;
    }

    public LiveData<HashMap<Long, Project>> getProjectsMappedById() {
        return projectsMappedById;
    }


    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public void setSorting(Utils.SortMethod sortMethod) {
        if (sortMethod != null) {
            this.sortMethod.setValue(sortMethod);
            sortTaskList();
        }
    }

    public LiveData<Utils.SortMethod> getSortMethod() {
        return sortMethod;
    }

    private void sortTaskList() {
        List<Task> sortedTaskList = new ArrayList<>();
        Utils.SortMethod sorting = sortMethod.getValue();

        switch (sorting) {
            case OLD_FIRST:
            case NONE:
                sortedTaskList = allTasksOldNew.getValue();
                break;
            case RECENT_FIRST:
                sortedTaskList.addAll(allTasksOldNew.getValue());
                Collections.reverse(sortedTaskList);
                break;
            case PROJECT_AZ:
                sortedTaskList.addAll(allTasksProjectAZ.getValue());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortMethod);
        }

        sortedListForDisplay.setValue(sortedTaskList);
    }

    public LiveData<List<Task>> getSortedListForDisplay() {
        return sortedListForDisplay;
    }

    public LiveData<Boolean> isTaskListEmpty() {
        return Transformations.map(allTasksOldNew, input -> {
            boolean actualListEmpty = input.isEmpty();
            if (firstTasksEmptyTest || allTasksEmpty != actualListEmpty) {
                allTasksEmpty = actualListEmpty;
                firstTasksEmptyTest = false;
                return allTasksEmpty;
            }
            return null;
        });
    }
}
