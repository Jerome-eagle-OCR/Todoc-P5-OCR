package com.cleanup.todoc.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.Utils;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.repository.ProjectRepository;
import com.cleanup.todoc.model.repository.TaskRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LiveData<List<Project>> allProjects;
    private final LiveData<HashMap<Long, Project>> projectsMappedById;
    private final LiveData<List<Task>> allTasksOldNew;
    private final LiveData<List<Task>> allTasksProjectAZ;
    private final MutableLiveData<Utils.SortMethod> sortMethodMutableLiveData;
    private final MediatorLiveData<List<Task>> sortedListForDisplayMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<TaskListViewState> taskListViewStateMutableLiveData;
    private final MutableLiveData<Boolean> taskNameErrorMutableLiveData;
    private final MutableLiveData<String> taskCreatedEditedMsgMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dialogDismissMutableLiveData;


    public MainViewModel(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        allProjects = projectRepository.getAllProjects();
        projectsMappedById = projectRepository.getProjectsHashMappedById();
        allTasksOldNew = taskRepository.getAllTasksOldNew();
        allTasksProjectAZ = projectRepository.getAllTasksProjectAZ();
        sortMethodMutableLiveData = new MutableLiveData<>(Utils.SortMethod.NONE);
        sortedListForDisplayMediatorLiveData.addSource(allTasksProjectAZ, tasks -> sortTaskList());
        sortedListForDisplayMediatorLiveData.addSource(allTasksOldNew, tasks -> sortTaskList());
        taskListViewStateMutableLiveData = new MutableLiveData<>(new TaskListViewState(View.VISIBLE, View.GONE));
        taskNameErrorMutableLiveData = new MutableLiveData<>(false);
        dialogDismissMutableLiveData = new MutableLiveData<>(false);
    }


    public void insertProject(Project project) {
        projectRepository.insert(project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public LiveData<HashMap<Long, Project>> getProjectsMappedById() {
        return projectsMappedById;
    }


    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public void setSorting(Utils.SortMethod sortMethod) {
        if (sortMethod != null) {
            this.sortMethodMutableLiveData.setValue(sortMethod);
            sortTaskList();
        }
    }

    public LiveData<Utils.SortMethod> getSortMethod() {
        return sortMethodMutableLiveData;
    }

    private void sortTaskList() {
        List<Task> sortedTaskList = new ArrayList<>();
        Utils.SortMethod sorting = sortMethodMutableLiveData.getValue();

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
                throw new IllegalStateException("Unexpected value: " + sortMethodMutableLiveData);
        }

        sortedListForDisplayMediatorLiveData.setValue(sortedTaskList);
        if (sortedTaskList != null) setTaskListViewState(sortedTaskList);
    }

    private void setTaskListViewState(List<Task> sortedTaskList) {
        boolean sortedTaskListEmpty = sortedTaskList.isEmpty();

        int taskListVisibility = sortedTaskListEmpty ? View.GONE : View.VISIBLE; //taskList not visible when list is empty and vice versa
        int noTaskLblVisibility = sortedTaskListEmpty ? View.VISIBLE : View.GONE; //noTaskLbl visible when list is empty and vice versa

        TaskListViewState taskListViewState = new TaskListViewState(taskListVisibility, noTaskLblVisibility);

        taskListViewStateMutableLiveData.setValue(taskListViewState);
    }

    public LiveData<List<Task>> getSortedList() {
        return sortedListForDisplayMediatorLiveData;
    }

    public LiveData<TaskListViewState> getTaskListViewState() {
        return taskListViewStateMutableLiveData;
    }

    public void createEditTask(Task taskToEdit, EditText dialogEditText, Spinner dialogSpinner) {
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                taskNameErrorMutableLiveData.setValue(true);
            } else if (taskProject != null) { // If both project and name of the task have been set
                long taskId;
                long timeStamp;
                long projectId = taskProject.getId();

                String taskCreatedEditedMsg;
                if (taskToEdit == null) {
                    timeStamp = new Date().getTime();

                    //Manage the creation in the list of tasks
                    insertTask(new Task(projectId, taskName, timeStamp));
                    taskCreatedEditedMsg = "Tâche ajoutée";
                } else {
                    taskId = taskToEdit.getId();
                    timeStamp = taskToEdit.getCreationTimestamp();

                    //Manage task update testing first if task is actually modified
                    boolean taskNotModified = taskName.equals(taskToEdit.getName()) &&
                            projectId == taskToEdit.getProjectId();
                    if (taskNotModified) {
                        taskCreatedEditedMsg = "Tâche non modifiée";
                    } else {
                        updateTask(new Task(taskId, projectId, taskName, timeStamp));
                        taskCreatedEditedMsg = "Tâche modifiée";
                    }
                }
                taskCreatedEditedMsgMutableLiveData.setValue(taskCreatedEditedMsg);
                taskNameErrorMutableLiveData.setValue(false);
                dialogDismissMutableLiveData.setValue(true); // Will null widgets and remove observers
            } else { // If name has been set, but project has not been set (this should never occur)
                dialogDismissMutableLiveData.setValue(true);
            }
        } else { // If dialog is already closed
            dialogDismissMutableLiveData.setValue(true);
        }
    }

    public MutableLiveData<Boolean> getTaskNameError() {
        return taskNameErrorMutableLiveData;
    }

    public LiveData<String> getTaskCreatedEditedMsg() {
        return taskCreatedEditedMsgMutableLiveData;
    }

    public MutableLiveData<Boolean> getDialogDismiss() {
        return dialogDismissMutableLiveData;
    }
}