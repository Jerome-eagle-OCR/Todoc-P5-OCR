package com.cleanup.todoc.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cleanup.todoc.LiveDataTestUtil;
import com.cleanup.todoc.db.TodocDatabase;
import com.cleanup.todoc.model.dao.TaskDao;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.cleanup.todoc.TestModel.FIRST_POSITION;
import static com.cleanup.todoc.TestModel.PROJECT_ENTREVOISINS;
import static com.cleanup.todoc.TestModel.PROJECT_MAGICGITHUB;
import static com.cleanup.todoc.TestModel.PROJECT_MAREU;
import static com.cleanup.todoc.TestModel.SECOND_POSITION;
import static com.cleanup.todoc.TestModel.TASKS_COUNT;
import static com.cleanup.todoc.TestModel.TASK_CREATE_MEETING_APP;
import static com.cleanup.todoc.TestModel.TASK_CREATE_MEETING_APP_ID;
import static com.cleanup.todoc.TestModel.TASK_MAKE_TESTS_PASS;
import static com.cleanup.todoc.TestModel.TASK_MAKE_TESTS_PASS_ID;
import static com.cleanup.todoc.TestModel.TASK_NEIGHBOUR_DETAILS;
import static com.cleanup.todoc.TestModel.TASK_NEIGHBOUR_DETAILS_ID;
import static com.cleanup.todoc.TestModel.THIRD_POSITION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {
    private TaskDao taskDao;
    private TodocDatabase db;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createsDb() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TodocDatabase.class)
                .allowMainThreadQueries()
                .build();
        taskDao = db.taskDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllTaskWithProjectWhenNoTaskInserted() throws InterruptedException {
        //Given : we have three projects in database but no task inserted yet
        insertTestProjects();

        //When : we get the list of tasks
        List<TaskWithProject> actualTaskWithProjectList = LiveDataTestUtil.getValue(taskDao.getAllTasksWithProject());

        //Then : the retrieved list is empty
        assertTrue(actualTaskWithProjectList.isEmpty());
    }

    @Test
    public void getAllTaskWithProjectAZWhenNoTaskInserted() throws InterruptedException {
        //Given : we have three projects in database but no task inserted yet
        insertTestProjects();

        //When : we get the list of tasks
        List<TaskWithProject> actualTaskWithProjectList = LiveDataTestUtil.getValue(taskDao.getAllTasksWithProjectAZ());

        //Then : the retrieved list is empty
        assertTrue(actualTaskWithProjectList.isEmpty());
    }

    @Test
    public void insertAndGetAllTaskWithProject() throws InterruptedException {
        //Given : we have three projects and we insert a task in each one
        insertTestProjects();

        insertTestTasks();

        setTestTasksIds();

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList = LiveDataTestUtil.getValue(taskDao.getAllTasksWithProject());

        //Then : the retrieved list contains the three tasks in insertion order
        assertEquals(TASKS_COUNT, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksInsertTest(actualTaskWithProjectList);

        //First task is the task inserted firstly
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted secondly
        assertEquals(TASK_NEIGHBOUR_DETAILS, actualTasks.get(SECOND_POSITION));
        //Third task is the task inserted thirdly
        assertEquals(TASK_CREATE_MEETING_APP, actualTasks.get(THIRD_POSITION));
    }

    @Test
    public void insertAndGetAllTaskWithProjectAZ() throws InterruptedException {
        //Given : we have three projects and we insert a task in each one
        insertTestProjects();

        insertTestTasks();

        setTestTasksIds();

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList = LiveDataTestUtil.getValue(taskDao.getAllTasksWithProjectAZ());

        //Then : the retrieved list contains the three tasks in insertion order
        assertEquals(TASKS_COUNT, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksInsertTest(actualTaskWithProjectList);

        //First task is the task inserted secondly (project name begins with "E")
        assertEquals(TASK_NEIGHBOUR_DETAILS, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted firstly (project name begins with "Mag")
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(SECOND_POSITION));
        //Second task is the task inserted thirdly (project name begins with "Mar")
        assertEquals(TASK_CREATE_MEETING_APP, actualTasks.get(THIRD_POSITION));
    }

    @Test
    public void deleteAndGetAllTaskWithProject() throws InterruptedException {
        //Given : we have three projects, we insert a task in each one and we delete the third one
        insertTestProjects();

        insertTestTasks();

        setTestTasksIds();

        taskDao.delete(TASK_CREATE_MEETING_APP);

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList = LiveDataTestUtil.getValue(taskDao.getAllTasksWithProject());

        //Then : the retrieved list contains the two tasks in insertion order
        assertEquals(TASKS_COUNT - 1, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksDeleteTest(actualTaskWithProjectList);

        //First task is the task inserted firstly
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted secondly
        assertEquals(TASK_NEIGHBOUR_DETAILS, actualTasks.get(SECOND_POSITION));
    }

    @Test
    public void deleteAndGetAllTaskWithProjectAZ() throws InterruptedException {
        //Given : we have three projects, we insert a task in each one and we delete the third one
        insertTestProjects();

        insertTestTasks();

        setTestTasksIds();

        taskDao.delete(TASK_CREATE_MEETING_APP);

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList = LiveDataTestUtil.getValue(taskDao.getAllTasksWithProjectAZ());

        //Then : the retrieved list contains the two tasks in project name alphabetical order
        assertEquals(TASKS_COUNT - 1, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksDeleteTest(actualTaskWithProjectList);

        //First task is the task inserted secondly (project name begins with "E")
        assertEquals(TASK_NEIGHBOUR_DETAILS, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted firstly (project name begins with "Mag")
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(SECOND_POSITION));
    }


    private void insertTestProjects() {
        db.projectDao().insert(PROJECT_MAGICGITHUB);
        db.projectDao().insert(PROJECT_ENTREVOISINS);
        db.projectDao().insert(PROJECT_MAREU);
    }

    private void insertTestTasks() {
        taskDao.insert(TASK_MAKE_TESTS_PASS);
        taskDao.insert(TASK_NEIGHBOUR_DETAILS);
        taskDao.insert(TASK_CREATE_MEETING_APP);
    }

    private void setTestTasksIds() {
        TASK_MAKE_TESTS_PASS.setId(TASK_MAKE_TESTS_PASS_ID);
        TASK_NEIGHBOUR_DETAILS.setId(TASK_NEIGHBOUR_DETAILS_ID);
        TASK_CREATE_MEETING_APP.setId(TASK_CREATE_MEETING_APP_ID);
    }

    private List<Task> getActualTasksInsertTest(List<TaskWithProject> actualTaskWithProjectList) {
        return Arrays.asList(
                actualTaskWithProjectList.get(FIRST_POSITION).getTask(),
                actualTaskWithProjectList.get(SECOND_POSITION).getTask(),
                actualTaskWithProjectList.get(THIRD_POSITION).getTask()
        );
    }

    private List<Task> getActualTasksDeleteTest(List<TaskWithProject> actualTaskWithProjectList) {
        return Arrays.asList(
                actualTaskWithProjectList.get(FIRST_POSITION).getTask(),
                actualTaskWithProjectList.get(SECOND_POSITION).getTask()
        );
    }
}