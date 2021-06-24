package com.cleanup.todoc.model.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.DB.TodocDatabase;
import com.cleanup.todoc.model.DAOs.TaskDao;
import com.cleanup.todoc.model.entities.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRepository {

    private final TaskDao taskDao;
    private LiveData<ArrayList<Task>> allTasksOld;
    private LiveData<List<Task>> allTasksProjectAZ;
    private final Executor doInBackground;

    public TaskRepository(Application application) {
        TodocDatabase database = TodocDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasksOld = taskDao.getAllTasks();
        allTasksProjectAZ = taskDao.getAllTasksProjectAZ();
        doInBackground = Executors.newFixedThreadPool(2);
    }

    public void insert(Task task) {
        doInBackground.execute(() -> taskDao.insert(task));
    }

    public void delete(Task task) {
        doInBackground.execute(() -> taskDao.delete(task));
    }

    public LiveData<ArrayList<Task>> getAllTasksOld() {
        return allTasksOld;
    }

    public LiveData<List<Task>> getAllTasksProjectAZ() {
        return allTasksProjectAZ;
    }

    public List<Task> getTasksByProjectId(long givenProjectId) {
        return taskDao.getTasksByProjectId(givenProjectId);
    }
}
