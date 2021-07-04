package com.cleanup.todoc.ui;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.model.entities.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class DeleteTaskItemTouchHelperSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private final MainViewModel viewModel;
    private final TaskAdapter adapter;
    private final FloatingActionButton fabAddTask;
    private final int snkBckGndColor;
    private final int snckTxtActnColor;
    private final int swpBckGndColor;

    public DeleteTaskItemTouchHelperSimpleCallback(
            int dragDirs, int swipeDirs,
            MainViewModel viewModel,
            TaskAdapter adapter,
            FloatingActionButton fabAddTask,
            int snkBckGndColor,
            int snckTxtActnColor,
            int swpBckGndColor
    ) {
        super(dragDirs, swipeDirs);
        this.viewModel = viewModel;
        this.adapter = adapter;
        this.fabAddTask = fabAddTask;
        this.snkBckGndColor = snkBckGndColor;
        this.snckTxtActnColor = snckTxtActnColor;
        this.swpBckGndColor = swpBckGndColor;
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @SuppressLint("ShowToast")
    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        View taskItem = viewHolder.itemView;

        Task taskToDelete = adapter.getTaskAtPosition(position);
        viewModel.deleteTask(taskToDelete);
        String snackMessage = "Tâche supprimée";
        Snackbar.make(taskItem, snackMessage, Snackbar.LENGTH_LONG)
                .setBackgroundTint(snkBckGndColor)
                .setAnchorView(fabAddTask)
                .setActionTextColor(snckTxtActnColor)
                .setAction("ANNULER", v -> viewModel.insertTask(taskToDelete)
        ).show();
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c,
                            @NonNull @NotNull RecyclerView recyclerView,
                            @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive
    ) {
        View taskItem = viewHolder.itemView;
        ColorDrawable swipeBkgnd = new ColorDrawable(swpBckGndColor);
        Drawable deleteIcon = AppCompatResources.getDrawable(taskItem.getContext(), R.drawable.ic_delete);

        int iconMargin = (taskItem.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;

        if (dX > 0) {
            swipeBkgnd.setBounds(taskItem.getLeft(), taskItem.getTop(),
                    taskItem.getLeft() + (int) dX, taskItem.getBottom()
            );
            deleteIcon.setBounds(taskItem.getLeft() + iconMargin, taskItem.getTop() + iconMargin,
                    taskItem.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth(),
                    taskItem.getBottom() - iconMargin
            );
        } else {
            swipeBkgnd.setBounds(taskItem.getRight() + (int) dX, taskItem.getTop(),
                    taskItem.getRight(), taskItem.getBottom());
            deleteIcon.setBounds(taskItem.getRight() - iconMargin - deleteIcon.getIntrinsicWidth(),
                    taskItem.getTop() + iconMargin,
                    taskItem.getRight() - iconMargin, taskItem.getBottom() - iconMargin);
        }
        swipeBkgnd.draw(c);
        deleteIcon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
    }

}
