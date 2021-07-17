package com.cleanup.todoc.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.ViewModelFactory;
import com.cleanup.todoc.databinding.ActivityMainBinding;
import com.cleanup.todoc.databinding.DialogAddTaskBinding;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static com.cleanup.todoc.Utils.SortMethod;

/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY / Modified by Jérôme Rigault
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.EditTaskListener {

    /**
     * List of all projects available in the application
     */
    private Project[] mProjects;

    /**
     * Current sorting method (valorized by viewmodel livedata)
     */
    private SortMethod sortMethod;

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
    private TaskWithProject taskToEdit = null;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    /**
     * Initialize the widgets, listeners and observers for the list and fab
     */
    private void init() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        // The floating action button to add a new task
        fabAddTask = binding.fabAddTask;
        fabAddTask.setOnClickListener(view1 -> showAddEditTaskDialog());

        // The RecyclerView which displays the list of tasks
        final RecyclerView taskRecyclerview = binding.listTasks;
        taskRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TaskAdapter(this);
        taskRecyclerview.setAdapter(adapter);
        taskRecyclerview.setHasFixedSize(true);

        // Set delete on swipe
        setItemTouchHelper(taskRecyclerview, adapter);

        // Set the list and keep it up to date
        viewModel.getSortedList().observe(this, adapter::submitList);

        // Scroll list to top when a sorting is selected
        viewModel.getSortMethod().observe(this, sortMethod -> {
            this.sortMethod = sortMethod;
            taskRecyclerview.smoothScrollToPosition(Integer.MIN_VALUE);
        });

        // Observe up to date project list to populate project spinner
        viewModel.getAllProjects().observe(this,
                projects -> mProjects = projects.toArray(new Project[0]));

        // Manage the display depending on whether the list is empty or not; viewModel handles it
        TextView noTaskLbl = binding.lblNoTask;
        viewModel.getTaskListViewState().observe(this, taskListViewState -> {
            if (taskListViewState != null) {
                noTaskLbl.setVisibility(taskListViewState.getNoTaskLblVisibility());
                taskRecyclerview.setVisibility(taskListViewState.getTaskListVisibility());
            }
        });
    }

    /**
     * Set deletion by swiping a task item
     *
     * @param taskRecyclerview the RecyclerView which displays the list of tasks
     * @param adapter          the adapter which handles the list of tasks
     */
    private void setItemTouchHelper(RecyclerView taskRecyclerview, TaskAdapter adapter) {
        new ItemTouchHelper(new DeleteTaskItemTouchHelperSimpleCallback(0, ItemTouchHelper.RIGHT,
                viewModel, adapter, taskRecyclerview, fabAddTask)
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
        SortMethod sortMethod = null;

        if (id == R.id.sort_by_project) {
            sortMethod = SortMethod.PROJECT_AZ;
        } else if (id == R.id.sort_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.sort_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }

        // Sort method is set in VM which takes care of sorting the list
        viewModel.setSorting(sortMethod);

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when task item is long clicked
     *
     * @param taskWithProject the task that needs to be edited
     */
    @Override
    public void onEditTask(TaskWithProject taskWithProject) {
        taskToEdit = taskWithProject;
        showAddEditTaskDialog();
    }

    /**
     * Shows the Dialog for adding or editing a Task
     */
    private void showAddEditTaskDialog() {
        final AlertDialog dialog = getAddEditTaskDialog();

        dialog.show();

        populateDialogSpinner();
    }

    /**
     * Returns the dialog allowing the user to create a new task or edit an existing one.
     *
     * @return the dialog allowing the user to create a new task  or edit an existing one
     */
    @NonNull
    private AlertDialog getAddEditTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        AddEditTaskDialogViewState viewState = viewModel.getAddEditDialogViewState(taskToEdit);

        DialogAddTaskBinding dialogBinding = DialogAddTaskBinding.inflate(getLayoutInflater());
        alertBuilder.setView(dialogBinding.getRoot());
        alertBuilder.setTitle(viewState.getDialogTitle());
        dialogEditText = dialogBinding.txtTaskName;
        dialogSpinner = dialogBinding.projectSpinner;
        dialogEditText.setText(viewState.getDialogEditText());
        dialogSpinner.post(() -> dialogSpinner.setSelection(viewState.getProjectIndex()));
        alertBuilder.setPositiveButton(viewState.getPositiveBtnTxt(), null);
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
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) dialogSpinner.setAdapter(adapter);
    }

    /**
     * Called when the user clicks on the positive button of the Create/Edit Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    @SuppressLint("ShowToast")
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // VM manages the task adding or editing, and sets all needed values for the view
        viewModel.createEditTask(taskToEdit, dialogEditText, dialogSpinner);

        // emptyTaskNameError is false if dialogEditText is null
        boolean emptyTaskNameError = viewModel.getEmptyTaskNameError();
        if (emptyTaskNameError && dialogEditText != null) {
            dialogEditText.setError(getString(R.string.empty_task_name));
        }

        // Snack the taskCreatedEditedMsg if not empty
        String snackThis = viewModel.getTaskCreatedEditedMsg();
        if (!snackThis.equals("")) {
            Snackbar.make(fabAddTask, snackThis, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark))
                    .setAnchorView(fabAddTask)
            .show();
        }
        // When list is sorted recent tasks first scroll to first position after adding a new task
        if (snackThis.equals(MainViewModel.TASK_ADDED_SNK) && sortMethod == SortMethod.RECENT_FIRST) {
            binding.listTasks.smoothScrollToPosition(Integer.MIN_VALUE);
        }

        boolean dialogDismiss = viewModel.getDialogDismiss();
        if (dialogDismiss) dialogInterface.dismiss();
    }

    @VisibleForTesting
    public int getTaskAdapterCount() {
        return adapter.getItemCount();
    }
}
