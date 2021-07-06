package com.cleanup.todoc.ui;

public class AddEditTaskDialogViewState {

    private final int dialogTitle;
    private final String dialogEditText;
    private final int projectIndex;
    private final int positiveBtnTxt;


    public AddEditTaskDialogViewState(
            int dialogTitle,
            String dialogEditText,
            int projectIndex,
            int positiveBtnTxt
    ) {
        this.dialogTitle = dialogTitle;
        this.dialogEditText = dialogEditText;
        this.projectIndex = projectIndex;
        this.positiveBtnTxt = positiveBtnTxt;
    }


    public int getDialogTitle() {
        return dialogTitle;
    }

    public String getDialogEditText() {
        return dialogEditText;
    }

    public int getProjectIndex() {
        return projectIndex;
    }

    public int getPositiveBtnTxt() {
        return positiveBtnTxt;
    }
}
