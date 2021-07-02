package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
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
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.DeleteTaskListener {

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
     * The sort method to be used to display tasks
     */
    @NonNull
    private Utils.SortMethod sortMethod = Utils.SortMethod.NONE;

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

    private ActivityMainBinding mBinding;
    private MainViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        setListTasks();

        mBinding.fabAddTask.setOnClickListener(view1 -> showAddTaskDialog());
    }

    private void setListTasks() {
        /**
         * The RecyclerView which displays the list of tasks
         */
        final RecyclerView taskRecyclerview = mBinding.listTasks;
        taskRecyclerview.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                taskRecyclerview.post(() ->
                        taskRecyclerview.smoothScrollToPosition(Integer.MIN_VALUE));
            }
        });
        adapter = new TaskAdapter(this);
        taskRecyclerview.setAdapter(adapter);
        taskRecyclerview.setHasFixedSize(true);

        setItemTouchHelper(taskRecyclerview, adapter);

        viewModel.getSortedListForDisplay().observe(this, tasks -> {
            mTasks = tasks;
            updateTasks();
        });

        viewModel.getProjectsMappedById().observe(this, longProjectHashMap -> adapter.submitProjects(longProjectHashMap));
    }

    public void setItemTouchHelper(RecyclerView taskRecyclerview, TaskAdapter adapter) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView,
                                  @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull @NotNull RecyclerView.ViewHolder target
            ) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                                 int direction
            ) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = adapter.getTaskAtPosition(position);
                String snackMessage = "Voulez-vous vraiment supprimer la tâche ?" +
                        "\nTaper le bandeau pour annuler";
                final Snackbar snackbar = Snackbar.make(viewHolder.itemView, snackMessage, Snackbar.LENGTH_INDEFINITE)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark))
                        .setAction("OUI", v -> onDeleteTask(taskToDelete));
                snackbar.getView().setOnClickListener(v -> {
                    adapter.notifyItemChanged(position);
                    taskRecyclerview.postOnAnimation(() -> taskRecyclerview.smoothScrollToPosition(position));
                    snackbar.dismiss();
                });
                if (!snackbar.isShown()) snackbar.show();
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c,
                                    @NonNull @NotNull RecyclerView recyclerView,
                                    @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive
            ) {
                View itemView = viewHolder.itemView;

                swipeDrawing(c, dX, itemView);

                super.onChildDraw(c, recyclerView, viewHolder, dX / 2, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(taskRecyclerview);
    }

    private void swipeDrawing(@NotNull Canvas c, float dX, View itemView) {
        ColorDrawable swipeBkgnd = new ColorDrawable(getResources().getColor(R.color.colorAccent));
        Drawable deleteIcon = AppCompatResources.getDrawable(this, R.drawable.ic_delete);


        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;

        if (dX > 0) {
            swipeBkgnd.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + (int) dX, itemView.getBottom()
            );
            deleteIcon.setBounds(itemView.getLeft() + iconMargin, itemView.getTop() + iconMargin,
                    itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth(),
                    itemView.getBottom() - iconMargin
            );
            swipeBkgnd.draw(c);
            deleteIcon.draw(c);
        } else {
            swipeBkgnd.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom()
            );
            deleteIcon.setBounds(itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth(),
                    itemView.getTop() + iconMargin,
                    itemView.getRight() - iconMargin, itemView.getBottom() - iconMargin
            );
            swipeBkgnd.draw(c);
            deleteIcon.draw(c);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_project) {
            sortMethod = Utils.SortMethod.PROJECT_AZ;
        } else if (id == R.id.sort_oldest_first) {
            sortMethod = Utils.SortMethod.OLD_FIRST;
        } else if (id == R.id.sort_recent_first) {
            sortMethod = Utils.SortMethod.RECENT_FIRST;
        }

        viewModel.setSorting(sortMethod);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteTask(Task task) {
        viewModel.deleteTask(task);
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

                Task task = new Task(
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );
                //Adds the created task to the list of tasks
                viewModel.insertTask(task);

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
     * Updates the list of tasks in the UI
     */
    private void updateTasks() {
        if (mTasks.size() == 0) {
            mBinding.lblNoTask.setVisibility(View.VISIBLE);
            mBinding.listTasks.setVisibility(View.GONE);
        } else {
            mBinding.lblNoTask.setVisibility(View.GONE);
            mBinding.listTasks.setVisibility(View.VISIBLE);

            adapter.submitList(mTasks);
        }
    }

    /**
     * Returns the dialog allowing the user to create a new task.
     *
     * @return the dialog allowing the user to create a new task
     */
    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        DialogAddTaskBinding dialogBinding = DialogAddTaskBinding.inflate(getLayoutInflater());
        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(dialogBinding.getRoot());
        dialogEditText = dialogBinding.txtTaskName;
        dialogSpinner = dialogBinding.projectSpinner;
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(dialogInterface -> {
            dialogEditText = null;
            dialogSpinner = null;
            dialog = null;
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
        mProjects = new Project[0];
        viewModel.getAllProjects().observe(this, projects -> mProjects = projects);

        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }
}
