package com.cleanup.todoc.ui;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.DI.TodocApplication;
import com.cleanup.todoc.databinding.ItemTaskBinding;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;
import com.cleanup.todoc.model.repositories.ProjectRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * <p>Adapter which handles the list of tasks to display in the dedicated RecyclerView.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class TasksAdapter extends ListAdapter<Task, TasksAdapter.TaskViewHolder> {
    /**
     * The listener for when a task needs to be deleted
     */
    @NonNull
    private final DeleteTaskListener deleteTaskListener;
    private HashMap<Long, Project> projects;

    /**
     * Instantiates a new TasksAdapter.
     *
     */
    TasksAdapter(@NonNull final DeleteTaskListener deleteTaskListener) {
        super(DIFF_CALLBACK);
        this.deleteTaskListener = deleteTaskListener;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Task oldItem, @NonNull @NotNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Task oldItem, @NonNull @NotNull Task newItem) {
            return (oldItem.getProjectId() == newItem.getProjectId() &&
                    oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCreationTimestamp() == newItem.getCreationTimestamp());
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemTaskBinding itemTaskBinding = ItemTaskBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new TaskViewHolder(itemTaskBinding, deleteTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.bind(getItem(position));
    }

    public void submitProjects(HashMap<Long, Project> projects) {
        this.projects = projects;
    }

    /**
     * Listener for deleting tasks
     */
    public interface DeleteTaskListener {
        /**
         * Called when a task needs to be deleted.
         *
         * @param task the task that needs to be deleted
         */
        void onDeleteTask(Task task);
    }

    /**
     * <p>ViewHolder for task items in the tasks list</p>
     *
     * @author Gaëtan HERFRAY
     */
    class TaskViewHolder extends RecyclerView.ViewHolder {
        /**
         * The circle icon showing the color of the project
         */
        private final AppCompatImageView imgProject;

        /**
         * The TextView displaying the name of the task
         */
        private final TextView lblTaskName;

        /**
         * The TextView displaying the name of the project
         */
        private final TextView lblProjectName;

        /**
         * The delete icon
         */
        private final AppCompatImageView imgDelete;

        /**
         * The listener for when a task needs to be deleted
         */
        private final DeleteTaskListener deleteTaskListener;

        /**
         * Instantiates a new TaskViewHolder.
         *
         * @param itemTaskBinding    the binding of the task item
         * @param deleteTaskListener the listener for when a task needs to be deleted to set
         */
        TaskViewHolder(@NonNull ItemTaskBinding itemTaskBinding, @NonNull DeleteTaskListener deleteTaskListener) {
            super(itemTaskBinding.getRoot());

            this.deleteTaskListener = deleteTaskListener;

            imgProject = itemTaskBinding.imgProject;
            lblTaskName = itemTaskBinding.lblTaskName;
            lblProjectName = itemTaskBinding.lblProjectName;
            imgDelete = itemTaskBinding.imgDelete;

            imgDelete.setOnClickListener(view -> {
                final Object tag = view.getTag();
                if (tag instanceof Task) {
                    TaskViewHolder.this.deleteTaskListener.onDeleteTask((Task) tag);
                }
            });
        }

        /**
         * Binds a task to the item view.
         *
         * @param task the task to bind in the item view
         */
        void bind(Task task) {
            lblTaskName.setText(task.getName());
            imgDelete.setTag(task);

            final Project taskProject = projects.get(task.getProjectId());
            if (taskProject != null) {
                imgProject.setImageTintList(ColorStateList.valueOf(taskProject.getColor()));
                lblProjectName.setText(taskProject.getName());
            } else {
                imgProject.setVisibility(View.INVISIBLE);
                lblProjectName.setText("");
            }

        }
    }
}
