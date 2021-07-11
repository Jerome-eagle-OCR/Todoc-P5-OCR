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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddEditTaskDialogViewState that = (AddEditTaskDialogViewState) o;

        if (getDialogTitle() != that.getDialogTitle()) return false;
        if (getProjectIndex() != that.getProjectIndex()) return false;
        if (getPositiveBtnTxt() != that.getPositiveBtnTxt()) return false;
        return getDialogEditText().equals(that.getDialogEditText());
    }

    @Override
    public int hashCode() {
        int result = getDialogTitle();
        result = 31 * result + getDialogEditText().hashCode();
        result = 31 * result + getProjectIndex();
        result = 31 * result + getPositiveBtnTxt();
        return result;
    }
}
