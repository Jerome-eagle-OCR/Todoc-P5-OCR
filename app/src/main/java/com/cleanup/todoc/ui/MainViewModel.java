package com.cleanup.todoc.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.R;
import com.cleanup.todoc.Utils;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;
import com.cleanup.todoc.model.repository.ProjectRepository;
import com.cleanup.todoc.model.repository.TaskRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainViewModel extends ViewModel {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LiveData<List<Project>> allProjects;
    private final LiveData<List<TaskWithProject>> allTasksWithProject;
    private final LiveData<List<TaskWithProject>> allTasksWithProjectAZ;
    private final MutableLiveData<Utils.SortMethod> sortMethodMutableLiveData;
    private final MediatorLiveData<List<TaskWithProject>> sortedListForDisplayMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<TaskListViewState> taskListViewStateMutableLiveData;
    private boolean emptyTaskNameError;
    private String taskCreatedEditedMsg = null;
    private boolean dialogDismiss;


    public MainViewModel(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        allProjects = projectRepository.getAllProjects();
        allTasksWithProject = taskRepository.getAllTasksWithProject();
        allTasksWithProjectAZ = taskRepository.getAllTasksWithProjectAZ();
        sortMethodMutableLiveData = new MutableLiveData<>(Utils.SortMethod.NONE);
        sortedListForDisplayMediatorLiveData.addSource(allTasksWithProjectAZ, tasks -> sortTaskList());
        sortedListForDisplayMediatorLiveData.addSource(allTasksWithProject, tasks -> sortTaskList());
        taskListViewStateMutableLiveData = new MutableLiveData<>(new TaskListViewState(View.VISIBLE, View.GONE));
    }

    /**
     * Insert a new project in database
     *
     * @param project the project to insert
     */
    public void insertProject(Project project) {
        projectRepository.insert(project);
    }

    /**
     * Delete a project in database
     *
     * @param project the project to delete
     */
    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    /**
     * Get all the projects that are in database
     *
     * @return the list of projects
     */
    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    /**
     * Insert a new task in database
     *
     * @param task the task to insert
     */
    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    /**
     * Update a task in database
     *
     * @param task the task to update
     */
    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    /**
     * Delete a task in database
     *
     * @param task the task to delete
     */
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    /**
     * Set the sorting method for the task list in the mutable live data and launch the sorting
     *
     * @param sortMethod the sorting method selected
     */
    public void setSorting(Utils.SortMethod sortMethod) {
        if (sortMethod == null) { // Useful when starting the app
            sortMethod = Utils.SortMethod.NONE;
        }
        this.sortMethodMutableLiveData.setValue(sortMethod);
        sortTaskList();

    }

    /**
     * Get the active sort method
     *
     * @return the active sort method
     */
    public LiveData<Utils.SortMethod> getSortMethod() {
        return sortMethodMutableLiveData;
    }

    /**
     * Sort the task list depending on active sorting method and set related live data and
     * launch view state setting
     */
    private void sortTaskList() {
        List<TaskWithProject> sortedList = new ArrayList<>();
        Utils.SortMethod sorting = sortMethodMutableLiveData.getValue();

        switch (sorting) { // Method is not called when sort method is null
            case OLD_FIRST:
            case NONE:
                sortedList = allTasksWithProject.getValue(); // List is pre-sorted from SQL request
                break;
            case RECENT_FIRST:
                sortedList.addAll(allTasksWithProject.getValue());
                //sortedList.addAll(allTasksOldNew.getValue());
                Collections.reverse(sortedList); // Pre-sorted list is reversed
                break;
            case PROJECT_AZ:
                sortedList.addAll(allTasksWithProjectAZ.getValue()); // List is pre-sorted from SQL request
                //sortedList.addAll(allTasksProjectAZ.getValue()); // List is computed in repository
                break;                                               // from embedded project list
            default:
                throw new IllegalStateException("Unexpected value: " + sortMethodMutableLiveData);
        }

        // Set the live data with the properly sorted list
        sortedListForDisplayMediatorLiveData.setValue(sortedList);

        // Set task list view state depending on task list empty or not (null testing to avoid trouble)
        if (sortedList != null) setTaskListViewState(sortedList);
    }

    /**
     * Get the list of tasks properly sorted according to active sort method
     *
     * @return the sorted task list
     */
    public LiveData<List<TaskWithProject>> getSortedList() {
        return sortedListForDisplayMediatorLiveData;
    }

    /**
     * Create a view state based on sorted task list empty or not and set related live data
     *
     * @param sortedTaskList the sorted task list
     */
    private void setTaskListViewState(List<TaskWithProject> sortedTaskList) {
        boolean sortedTaskListEmpty = sortedTaskList.isEmpty();

        int taskListVisibility = sortedTaskListEmpty ? View.GONE : View.VISIBLE; //taskList not visible when list is empty and vice versa
        int noTaskLblVisibility = sortedTaskListEmpty ? View.VISIBLE : View.GONE; //noTaskLbl visible when list is empty and vice versa

        // Create a new view state
        TaskListViewState taskListViewState = new TaskListViewState(taskListVisibility, noTaskLblVisibility);

        // Set the live data with the freshly made view state
        taskListViewStateMutableLiveData.setValue(taskListViewState);
    }

    /**
     * Get task list view state based on sorted task list empty or not
     *
     * @return the view state
     */
    public LiveData<TaskListViewState> getTaskListViewState() {
        return taskListViewStateMutableLiveData;
    }

    /**
     * Manage creation or edition or nothing of a task depending on taskToEdit value (null or
     * containing an existing task and if something has been changed)
     * Also set emptyTaskNameError, taskCreatedEditedMsg and dialogDismiss
     *
     * @param taskToEdit     the task to edit (or null if a task has to be created)
     * @param dialogEditText the widget containing the task name
     * @param dialogSpinner  the widget containing the project associated to the task
     */
    public void createEditTask(TaskWithProject taskToEdit, EditText dialogEditText, Spinner dialogSpinner) {
        emptyTaskNameError = false;
        taskCreatedEditedMsg = "";
        dialogDismiss = false;
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            if (taskName.trim().isEmpty()) { // If a name has not been set
                emptyTaskNameError = true;
            } else if (taskProject != null) { // If both project and name of the task have been set
                long taskId;
                long timeStamp;
                long projectId = taskProject.getId();

                if (taskToEdit == null) {
                    timeStamp = new Date().getTime();

                    //Manage the creation in the list of tasks
                    insertTask(new Task(projectId, taskName, timeStamp));
                    taskCreatedEditedMsg = "Tâche ajoutée";
                } else {
                    taskId = taskToEdit.getTask().getId();
                    timeStamp = taskToEdit.getTask().getCreationTimestamp();

                    //Manage task update testing first if task is actually modified
                    boolean taskNotModified = taskName.equals(taskToEdit.getTask().getName()) &&
                            projectId == taskToEdit.getTask().getProjectId();
                    if (taskNotModified) {
                        taskCreatedEditedMsg = "Aucune tâche modifiée";
                    } else {
                        updateTask(new Task(taskId, projectId, taskName, timeStamp));
                        taskCreatedEditedMsg = "Tâche modifiée";
                    }
                }
                dialogDismiss = true; // Will null widgets and remove observers
            } else { // If name has been set, but project has not been set (this should never occur)
                dialogDismiss = true;
            }
        } else { // If dialog is already closed
            dialogDismiss = true;
        }
    }

    /**
     * Get the empty task name error boolean (true in case of an error)
     *
     * @return the empty task name error boolean
     */
    public boolean getEmptyTaskNameError() {
        return emptyTaskNameError;
    }

    /**
     * Get the task created or edited message to show in a snackbar when action is done
     *
     * @return the task created or edited message
     */
    public String getTaskCreatedEditedMsg() {
        return taskCreatedEditedMsg;
    }

    /**
     * Get the dialog dismiss boolean (true for dialog dismissing)
     *
     * @return dialog dismiss boolean
     */
    public boolean getDialogDismiss() {
        return dialogDismiss;
    }

    /**
     * Get the view state created to set properly the add, or edit, task dialog details
     *
     * @param taskToEdit the task to edit (or null if a task has to be created)
     * @return the created view state
     */
    public AddEditTaskDialogViewState getAddEditDialogViewState(TaskWithProject taskToEdit) {
        int dialogTitle; // The title of the dialog (add or edit purpose)
        String dialogEditText; // The task name (empty or pre-filled)
        int projectIndex; // The project to associate (first in list or pre-selected)
        int positiveBtnTxt; // The text for the dialog positive button (add or edit)

        // If editing is involved
        if (taskToEdit != null) {
            dialogTitle = R.string.edit_task;
            dialogEditText = taskToEdit.getTask().getName();
            Project taskProject = taskToEdit.getProject();
            projectIndex = Objects.requireNonNull(allProjects.getValue()).indexOf(taskProject);
            positiveBtnTxt = R.string.edit;
        }
        // If adding is involved
        else {
            dialogTitle = R.string.add_task;
            dialogEditText = "";
            projectIndex = 0;
            positiveBtnTxt = R.string.add;
        }
        // Create and return a new adhoc view state
        return new AddEditTaskDialogViewState(dialogTitle, dialogEditText, projectIndex, positiveBtnTxt);
    }
}