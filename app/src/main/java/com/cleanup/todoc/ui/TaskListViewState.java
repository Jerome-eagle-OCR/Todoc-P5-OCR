package com.cleanup.todoc.ui;

public class TaskListViewState {

    private final int taskListVisibility;

    private final int noTaskLblVisibility;


    public TaskListViewState(
            int taskListVisibility,
            int noTaskLblVisibility
    ) {
        this.taskListVisibility = taskListVisibility;
        this.noTaskLblVisibility = noTaskLblVisibility;
    }


    public int getTaskListVisibility() {
        return taskListVisibility;
    }

    public int getNoTaskLblVisibility() {
        return noTaskLblVisibility;
    }
}
