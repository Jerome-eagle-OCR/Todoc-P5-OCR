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

import static com.cleanup.todoc.dao.DaoTestModel.DELAY;
import static com.cleanup.todoc.dao.DaoTestModel.FIRST_POSITION;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_ANDROID;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_ANDROID_ID;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_ENTREVOISINS;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_MAGICGITHUB;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_MAREU;
import static com.cleanup.todoc.dao.DaoTestModel.SECOND_POSITION;
import static com.cleanup.todoc.dao.DaoTestModel.TASKS_COUNT;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_CREATE_MEETING_APP;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_CREATE_MEETING_APP_ID;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_MAKE_TESTS_PASS;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_MAKE_TESTS_PASS_ID;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_MAKE_TESTS_PASS_TIMESTAMP;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_NEIGHBOUR_DETAILS;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_NEIGHBOUR_DETAILS_ID;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_NEIGHBOUR_DETAILS_NAME;
import static com.cleanup.todoc.dao.DaoTestModel.TASK_NEIGHBOUR_DETAILS_PROJECT_ID;
import static com.cleanup.todoc.dao.DaoTestModel.THIRD_POSITION;
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
        this.insertTestProjects(); //We need project(s) to have tasks

        //When : we get the list of tasks
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProject());

        //Then : the retrieved list is empty
        assertTrue(actualTaskWithProjectList.isEmpty());
    }

    @Test
    public void getAllTaskWithProjectAZWhenNoTaskInserted() throws InterruptedException {
        //Given : we have three projects in database but no task inserted yet
        this.insertTestProjects(); //We need project(s) to have tasks

        //When : we get the list of tasks
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProjectAZ());

        //Then : the retrieved list is empty
        assertTrue(actualTaskWithProjectList.isEmpty());
    }

    @Test
    public void insertAndGetAllTaskWithProject() throws InterruptedException {
        //Given : we have three projects and we insert a task in each one
        this.insertTestProjects(); //We need project(s) to have tasks

        this.insertTestTasks();

        this.setTestTasksIds(); // We set ids for our test tasks as in database

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProject());

        //Then : the retrieved list contains the three tasks in insertion order (date sorted)
        assertEquals(TASKS_COUNT, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksInsertOrUpdateTest(actualTaskWithProjectList);

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
        this.insertTestProjects(); //We need project(s) to create tasks

        this.insertTestTasks();

        this.setTestTasksIds(); // We set ids for our test tasks as in database

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProjectAZ());

        //Then : the retrieved list contains the three tasks in project name alphabetical order
        assertEquals(TASKS_COUNT, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksInsertOrUpdateTest(actualTaskWithProjectList);

        //First task is the task inserted secondly (project name begins with "E")
        assertEquals(TASK_NEIGHBOUR_DETAILS, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted firstly (project name begins with "Mag")
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(SECOND_POSITION));
        //Second task is the task inserted thirdly (project name begins with "Mar")
        assertEquals(TASK_CREATE_MEETING_APP, actualTasks.get(THIRD_POSITION));
    }

    @Test
    public void insertAllThenUpdateOneAndGetAllTaskWithProject() throws InterruptedException {
        //Given : we have three projects, one task in each one and we update the secondly added task
        //        with the same id and changing the other attributes thru a new task
        this.insertTestProjects(); //We need project(s) to create tasks

        this.insertTestTasks();

        this.setTestTasksIds(); // We set ids for our test tasks as in database

        Task TASK_NEIGHBOUR_DETAILS_UPDATED = new Task(
                TASK_NEIGHBOUR_DETAILS.getId(), //we get the id of the task we want to update
                TASK_NEIGHBOUR_DETAILS_PROJECT_ID + 1, //we set the next project
                TASK_NEIGHBOUR_DETAILS_NAME.replace("nouvelle ", ""),
                TASK_MAKE_TESTS_PASS_TIMESTAMP - 2 * DELAY);

        taskDao.update(TASK_NEIGHBOUR_DETAILS_UPDATED);

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProject());

        //Then : the retrieved list contains the three tasks in insertion order (date sorted)
        assertEquals(TASKS_COUNT, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksInsertOrUpdateTest(actualTaskWithProjectList);

        //First task is the task inserted secondly but because updated is now the oldest one
        assertEquals(TASK_NEIGHBOUR_DETAILS_UPDATED, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted firstly but now not the oldest anymore
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(SECOND_POSITION));
        //Third task is the task inserted thirdly and still the newest one
        assertEquals(TASK_CREATE_MEETING_APP, actualTasks.get(THIRD_POSITION));
    }

    @Test
    public void insertAllThenUpdateOneAndGetAllTaskWithProjectAZ() throws InterruptedException {
        //Given : we have three projects, one task in each one and we update the secondly added task
        //        with the same id and changing the other attributes thru a new task
        this.insertTestProjects(); //We need project(s) to create tasks

        db.projectDao().insert(PROJECT_ANDROID);

        this.insertTestTasks();

        this.setTestTasksIds(); // We set ids for our test tasks as in database

        Task TASK_NEIGHBOUR_DETAILS_UPDATED = new Task(
                TASK_NEIGHBOUR_DETAILS.getId(),
                PROJECT_ANDROID_ID,
                "Commencer la formation",
                TASK_MAKE_TESTS_PASS_TIMESTAMP - 2 * DELAY
        );

        taskDao.update(TASK_NEIGHBOUR_DETAILS_UPDATED);

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProjectAZ());

        //Then : the retrieved list contains the three tasks in project name alphabetical order
        assertEquals(TASKS_COUNT, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksInsertOrUpdateTest(actualTaskWithProjectList);

        //First task is the task with project name beginning with "* And"
        assertEquals(TASK_NEIGHBOUR_DETAILS_UPDATED, actualTasks.get(FIRST_POSITION));
        //Second task is the task with project name beginning with "* Mag"
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(SECOND_POSITION));
        //Third task is the task with project name beginning with "* Mar"
        assertEquals(TASK_CREATE_MEETING_APP, actualTasks.get(THIRD_POSITION));
    }

    @Test
    public void insertAllThenDeleteOneAndGetAllTaskWithProject() throws InterruptedException {
        //Given : we have three projects, we insert a task in each one and we delete the third one
        this.insertTestProjects(); //We need project(s) to create tasks

        this.insertTestTasks();

        this.setTestTasksIds(); // We set ids for our test tasks as in database

        //Deletion of thirdly inserted task
        taskDao.delete(TASK_CREATE_MEETING_APP);

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProject());

        //Then : the retrieved list contains the two tasks in insertion order (date sorted)
        assertEquals(TASKS_COUNT - 1, actualTaskWithProjectList.size());

        List<Task> actualTasks = getActualTasksDeleteTest(actualTaskWithProjectList);

        //First task is the task inserted firstly
        assertEquals(TASK_MAKE_TESTS_PASS, actualTasks.get(FIRST_POSITION));
        //Second task is the task inserted secondly
        assertEquals(TASK_NEIGHBOUR_DETAILS, actualTasks.get(SECOND_POSITION));
    }

    @Test
    public void insertAllThenDeleteOneAndGetAllTaskWithProjectAZ() throws InterruptedException {
        //Given : we have three projects, we insert a task in each one and we delete the third one
        this.insertTestProjects(); //We need project(s) to create tasks

        this.insertTestTasks();

        this.setTestTasksIds(); // We set ids for our test tasks as in database

        //Deletion of thirdly inserted task
        taskDao.delete(TASK_CREATE_MEETING_APP);

        //When : we get the list of taskWithProject
        List<TaskWithProject> actualTaskWithProjectList =
                LiveDataTestUtil.getValue(taskDao.getAllTaskWithProjectAZ());

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
        // We set ids for our test tasks as in database after insertion to avoid interference
        // with autogenerate
        TASK_MAKE_TESTS_PASS.setId(TASK_MAKE_TESTS_PASS_ID);
        TASK_NEIGHBOUR_DETAILS.setId(TASK_NEIGHBOUR_DETAILS_ID);
        TASK_CREATE_MEETING_APP.setId(TASK_CREATE_MEETING_APP_ID);
    }

    private List<Task> getActualTasksInsertOrUpdateTest(List<TaskWithProject> actualTaskWithProjectList) {
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