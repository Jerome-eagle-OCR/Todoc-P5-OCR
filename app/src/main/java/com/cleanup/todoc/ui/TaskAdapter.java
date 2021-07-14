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

import com.cleanup.todoc.databinding.ItemTaskBinding;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Adapter which handles the list of tasks to display in the dedicated RecyclerView.</p>
 *
 * @author Gaëtan HERFRAY / Modified by Jérôme Rigault
 */
public class TaskAdapter extends ListAdapter<TaskWithProject, TaskAdapter.TaskViewHolder> {
    /**
     * The listener for when a task needs to be edited
     */
    @NonNull
    private final EditTaskListener editTaskListener;

    /**
     * Instantiates a new TaskAdapter.
     */
    TaskAdapter(@NonNull final EditTaskListener editTaskListener) {
        super(DIFF_CALLBACK);
        this.editTaskListener = editTaskListener;
    }

    private static final DiffUtil.ItemCallback<TaskWithProject> DIFF_CALLBACK = new DiffUtil.ItemCallback<TaskWithProject>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull TaskWithProject oldItem, @NonNull @NotNull TaskWithProject newItem) {
            return oldItem.getTask().getId() == newItem.getTask().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull TaskWithProject oldItem, @NonNull @NotNull TaskWithProject newItem) {
            return (oldItem.getTask().getName().equals(newItem.getTask().getName()) &&
                    oldItem.getTask().getCreationTimestamp() == newItem.getTask().getCreationTimestamp() &&
                    oldItem.getTask().getProjectId() == newItem.getTask().getProjectId());
        }

    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemTaskBinding itemTaskBinding = ItemTaskBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false);
        return new TaskViewHolder(itemTaskBinding, editTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.bind(getItem(position));
    }

    /**
     * Getter to retrieve any taskWithProject from its position in the list adapter
     * Useful for the ItemTouchHelper implemented in MainActivity, to delete a task swiping the item
     *
     * @param position position of taskWithProject in adapter
     * @return the taskWithProject at the specified position
     */
    public TaskWithProject getTaskAtPosition(int position) {
        return getItem(position);
    }

    /**
     * Listener for deleting tasks
     */
    public interface EditTaskListener {
        /**
         * Called when a task needs to be edited.
         *
         * @param task the task that needs to be edited
         */
        void onEditTask(TaskWithProject task);
    }

    /**
     * <p>ViewHolder for task items in the tasks list</p>
     *
     * @author Gaëtan HERFRAY / Modified by Jérôme Rigault
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
         * The listener for when a task needs to be deleted
         */
        private final EditTaskListener editTaskListener;

        /**
         * Instantiates a new TaskViewHolder.
         *
         * @param itemTaskBinding  the binding of the task item
         * @param editTaskListener the listener for when a task needs to be deleted to set
         */
        TaskViewHolder(@NonNull ItemTaskBinding itemTaskBinding, @NonNull EditTaskListener editTaskListener) {
            super(itemTaskBinding.getRoot());

            this.editTaskListener = editTaskListener;

            imgProject = itemTaskBinding.imgProject;
            lblTaskName = itemTaskBinding.lblTaskName;
            lblProjectName = itemTaskBinding.lblProjectName;

            itemView.setOnLongClickListener(view -> {
                final Object tag = view.getTag();
                if (tag instanceof TaskWithProject) {
                    TaskViewHolder.this.editTaskListener.onEditTask((TaskWithProject) tag);
                }
                return false;
            });
        }

        /**
         * Binds a task to the item view.
         *
         * @param taskWithProject the task to bind in the item view
         */
        void bind(TaskWithProject taskWithProject) {
            lblTaskName.setText(taskWithProject.getTask().getName());
            itemView.setTag(taskWithProject);

            final Project taskProject = taskWithProject.getProject();
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
