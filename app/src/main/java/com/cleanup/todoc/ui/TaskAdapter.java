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
import com.cleanup.todoc.model.entity.Task;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * <p>Adapter which handles the list of tasks to display in the dedicated RecyclerView.</p>
 *
 * @author Gaëtan HERFRAY / Modified by Jérôme Rigault
 */
public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
    /**
     * The listener for when a task needs to be deleted
     */
    @NonNull
    private final EditTaskListener editTaskListener;
    private HashMap<Long, Project> projectHashMap;

    /**
     * Instantiates a new TaskAdapter.
     *
     */
    TaskAdapter(@NonNull final EditTaskListener editTaskListener) {
        super(DIFF_CALLBACK);
        this.editTaskListener = editTaskListener;
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
        return new TaskViewHolder(itemTaskBinding, editTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.bind(getItem(position));
    }

    /**
     * Setter to submit up to date projects hash map to retrieve easily a project from its id
     * Useful for bind(Task) method to display proper project name and color for each task
     * @param projects
     */
    public void submitProjects(HashMap<Long, Project> projects) {
        this.projectHashMap = projects;
    }

    /**
     * Getter to retrieve any task from its position in the list adapter
     * Useful for the ItemTouchHelper implemented in MainActivity, to delete a task swiping the item
     * @param position
     * @return
     */
    public Task getTaskAtPosition(int position) {
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
        void onEditTask(Task task);
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
        private final EditTaskListener editTaskListener;

        /**
         * Instantiates a new TaskViewHolder.
         *
         * @param itemTaskBinding    the binding of the task item
         * @param editTaskListener the listener for when a task needs to be deleted to set
         */
        TaskViewHolder(@NonNull ItemTaskBinding itemTaskBinding, @NonNull EditTaskListener editTaskListener) {
            super(itemTaskBinding.getRoot());

            this.editTaskListener = editTaskListener;

            imgProject = itemTaskBinding.imgProject;
            lblTaskName = itemTaskBinding.lblTaskName;
            lblProjectName = itemTaskBinding.lblProjectName;
            imgDelete = itemTaskBinding.imgDelete;

            itemView.setOnLongClickListener(view -> {
                final Object tag = view.getTag();
                if (tag instanceof Task) {
                    TaskViewHolder.this.editTaskListener.onEditTask((Task) tag);
                }
                return false;
            });
        }

        /**
         * Binds a task to the item view.
         *
         * @param task the task to bind in the item view
         */
        void bind(Task task) {
            lblTaskName.setText(task.getName());
            itemView.setTag(task);

            final long projectId = task.getProjectId();
            final Project taskProject = projectHashMap.get(projectId);
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
