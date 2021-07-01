package com.cleanup.todoc.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.Utils;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.cleanup.todoc.model.repositories.ProjectRepository;
import com.cleanup.todoc.model.repositories.TaskRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LiveData<Project[]> allProjects;
    private final LiveData<HashMap<Long, Project>> projectsMappedById;
    private final LiveData<List<Task>> allTasksOldNew;
    private final LiveData<List<Task>> allTasksProjectSorted;
    private final MutableLiveData<Utils.SortMethod> sortMethodMutableLiveData = new MutableLiveData<>();
    private final MediatorLiveData<List<Task>> sortedListForDisplay = new MediatorLiveData<>();


    public MainViewModel(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        allProjects = projectRepository.getAllProjects();
        projectsMappedById = projectRepository.getProjectsHashMappedById();
        allTasksOldNew = taskRepository.getAllTasksOldNew();
        allTasksProjectSorted = projectRepository.getAllTasksProjectAZ();
        sortedListForDisplay.addSource(sortMethodMutableLiveData, sortMethod -> sortTaskList());
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

    public void setSortMethod(Utils.SortMethod sortMethod) {
        sortMethodMutableLiveData.setValue(sortMethod);
    }

    public LiveData<List<Task>> getSortedListForDisplay() {
        return sortedListForDisplay;
    }

    private void sortTaskList() {
        Utils.SortMethod sortMethod = sortMethodMutableLiveData.getValue();
        if (sortMethod == null) sortMethod = Utils.SortMethod.NONE;

        List<Task> sortedTaskList = allTasksOldNew.getValue();

        switch (sortMethod) {
            case OLD_FIRST:
            case NONE:
                break;
            case RECENT_FIRST:
                Collections.reverse(sortedTaskList);
                break;
            case PROJECT_AZ:
                sortedTaskList = allTasksProjectSorted.getValue();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortMethod);
        }

        sortedListForDisplay.setValue(sortedTaskList);
    }
}
