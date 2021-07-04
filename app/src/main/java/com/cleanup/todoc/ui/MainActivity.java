package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.Utils;
import com.cleanup.todoc.ViewModelFactory;
import com.cleanup.todoc.databinding.ActivityMainBinding;
import com.cleanup.todoc.databinding.DialogAddTaskBinding;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.EditTaskListener {

    /**
     * List of all projects available in the application
     */
    private Project[] mProjects;

    /**
     * List of all current tasks of the application
     */
    private List<Task> mTasks;

    /**
     * The adapter which handles the list of tasks
     */
    private TaskAdapter adapter;

    /**
     * The floating action button to add a new task
     */
    private FloatingActionButton fabAddTask;

    /**
     * Dialog to create a new task
     */
    @Nullable
    public AlertDialog dialog = null;

    /**
     * EditText that allows user to set the name of a task
     */
    @Nullable
    private EditText dialogEditText = null;

    /**
     * Spinner that allows the user to associate a project to a task
     */
    @Nullable
    private Spinner dialogSpinner = null;

    /**
     * Task to edit using the same dialog as for creating a task
     */
    @Nullable
    private Task taskToEdit = null;

    private ActivityMainBinding mBinding;
    private MainViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        init();
    }

    /**
     *
     */
    private void init() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        fabAddTask = mBinding.fabAddTask;
        fabAddTask.setOnClickListener(view1 -> showAddTaskDialog());

        //The RecyclerView which displays the list of tasks
        final RecyclerView taskRecyclerview = mBinding.listTasks;
        taskRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TaskAdapter(this);
        taskRecyclerview.setAdapter(adapter);
        taskRecyclerview.setHasFixedSize(true);

        //Set delete on swipe
        setItemTouchHelper(taskRecyclerview, adapter);

        viewModel.getSortedListForDisplay().observe(this, tasks -> {
            mTasks = tasks;
            updateTasks();
        });

        //Scrolls list to top when a sorting is selected
        viewModel.getSortMethod().observe(this, sortMethod -> {
            taskRecyclerview.smoothScrollToPosition(Integer.MIN_VALUE);
        });

        //Submits up to date projects to adapter
        viewModel.getProjectsMappedById().observe(this, longProjectHashMap -> {
            adapter.submitProjects(longProjectHashMap);
        });

        //Manage the display depending on whether the list is empty or populated
        TextView noTaskLbl = mBinding.lblNoTask;
        noTaskLbl.setVisibility(View.GONE); //initially gone to have a blank screen starting the app
        //ViewModel trigger
        viewModel.isTaskListEmpty().observe(this, aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    noTaskLbl.setVisibility(View.VISIBLE);
                    taskRecyclerview.setVisibility(View.GONE);
                } else {
                    noTaskLbl.setVisibility(View.GONE);
                    taskRecyclerview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setItemTouchHelper(RecyclerView taskRecyclerview, TaskAdapter adapter) {
        int snkBckGndColor = getResources().getColor(R.color.colorPrimaryDark);
        int sncTxtActnColor = getResources().getColor(android.R.color.holo_green_light);
        int swpBckGndColor = getResources().getColor(R.color.colorAccent);

        new ItemTouchHelper(new DeleteTaskItemTouchHelperSimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                viewModel, adapter, fabAddTask,
                snkBckGndColor, sncTxtActnColor, swpBckGndColor)
        ).attachToRecyclerView(taskRecyclerview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // The sort method to be used to display tasks
        Utils.SortMethod sortMethod = null;

        if (id == R.id.sort_by_project) {
            sortMethod = Utils.SortMethod.PROJECT_AZ;
        } else if (id == R.id.sort_oldest_first) {
            sortMethod = Utils.SortMethod.OLD_FIRST;
        } else if (id == R.id.sort_recent_first) {
            sortMethod = Utils.SortMethod.RECENT_FIRST;
        }

        //sort method is set in VM which takes care of sorting the list
        viewModel.setSorting(sortMethod);

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the list of tasks in the UI
     */
    private void updateTasks() {
        adapter.submitList(mTasks);
    }

    @Override
    public void onEditTask(Task task) {
        taskToEdit = task;
        showAddTaskDialog();
    }

    /**
     * Shows the Dialog for adding a Task
     */
    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    /**
     * Returns the dialog allowing the user to create a new task or edit an existing one.
     *
     * @return the dialog allowing the user to create a new task  or edit an existing one
     */
    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        DialogAddTaskBinding dialogBinding = DialogAddTaskBinding.inflate(getLayoutInflater());
        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(dialogBinding.getRoot());
        dialogEditText = dialogBinding.txtTaskName;
        dialogSpinner = dialogBinding.projectSpinner;
        if (taskToEdit != null) {
            dialogEditText.setText(taskToEdit.getName());
            Project taskProject = viewModel.getProjectsMappedById().getValue().get(taskToEdit.getProjectId());
            int projectIndex = viewModel.getAllProjects().getValue().indexOf(taskProject);
            dialogSpinner.post(() -> dialogSpinner.setSelection(projectIndex));
            alertBuilder.setPositiveButton("Modifier", null);
        } else {
            alertBuilder.setPositiveButton(R.string.add, null);
        }
        alertBuilder.setOnDismissListener(dialogInterface -> {
            dialogEditText = null;
            dialogSpinner = null;
            dialog = null;
            taskToEdit = null;
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> onPositiveButtonClick(dialog));
        });

        return dialog;
    }

    /**
     * Sets the data of the Spinner with projects to associate to a new task
     */
    private void populateDialogSpinner() {
        viewModel.getAllProjects().observe(this, projects -> mProjects = projects.toArray(new Project[0]));

        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }

    /**
     * Called when the user clicks on the positive button of the Create Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
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
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {
                long taskId;
                long timeStamp;
                if (taskToEdit == null) {
                    taskId = -1;
                    timeStamp = new Date().getTime();
                } else {
                    taskId = taskToEdit.getId();
                    timeStamp = taskToEdit.getCreationTimestamp();
                }
                long projectId = taskProject.getId();

                //Manage the creation or edition in the list of tasks
                viewModel.createEditTask(taskId, projectId, taskName, timeStamp);

                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else {
                dialogInterface.dismiss();
            }
        }
        // If dialog is already closed
        else {
            dialogInterface.dismiss();
        }
    }
}
