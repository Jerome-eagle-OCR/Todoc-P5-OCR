package com.cleanup.todoc.model.repository;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.model.dao.TaskDao;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRepository {

    private final TaskDao mTaskDao;
    private final LiveData<List<TaskWithProject>> allTaskWithProject;
    private final LiveData<List<TaskWithProject>> allTaskWithProjectAZ;
    private final Executor doInBackground;


    public TaskRepository(TaskDao taskDao) {
        mTaskDao = taskDao;
        doInBackground = Executors.newFixedThreadPool(3);
        allTaskWithProject = mTaskDao.getAllTaskWithProject();
        allTaskWithProjectAZ = mTaskDao.getAllTaskWithProjectAZ();
    }

    public void insert(Task task) {
        doInBackground.execute(() -> mTaskDao.insert(task));
    }

    public void update(Task task) {
        doInBackground.execute(() -> mTaskDao.update(task));
    }

    public void delete(Task task) {
        doInBackground.execute(() -> mTaskDao.delete(task));
    }

    public LiveData<List<TaskWithProject>> getAllTaskWithProject() {
        return allTaskWithProject;
    }

    public LiveData<List<TaskWithProject>> getAllTaskWithProjectAZ() {
        return allTaskWithProjectAZ;
    }
}
