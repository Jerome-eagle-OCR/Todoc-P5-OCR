package com.cleanup.todoc.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cleanup.todoc.LiveDataTestUtil;
import com.cleanup.todoc.db.TodocDatabase;
import com.cleanup.todoc.model.dao.ProjectDao;
import com.cleanup.todoc.model.entity.Project;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.cleanup.todoc.dao.DaoTestModel.FIRST_POSITION;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_ENTREVOISINS;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_ENTREVOISINS_ID;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_MAGICGITHUB;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_MAGICGITHUB_ID;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_MAREU;
import static com.cleanup.todoc.dao.DaoTestModel.PROJECT_MAREU_ID;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProjectDaoTest {
    private ProjectDao projectDao;
    private TodocDatabase db;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createsDb() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TodocDatabase.class)
                .allowMainThreadQueries()
                .build();
        projectDao = db.projectDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getProjectsWhenNoProjectInserted() throws InterruptedException {
        //Given : no project inserted in database
        //When : we get the list of projects
        List<Project> actualProjects = LiveDataTestUtil.getValue(projectDao.getProjects());
        //Then : the retrieved list is empty
        assertTrue(actualProjects.isEmpty());
    }

    @Test
    public void insertAndGetProjects() throws InterruptedException {
        //Given : we insert three projects
        insertProjects();

        setProjectIds(); //We set id after insertion in database not to interfere with autogenerate

        List<Project> expectedProjects = Arrays.asList( //We prepare id sorted expected list
                PROJECT_MAGICGITHUB,
                PROJECT_ENTREVOISINS,
                PROJECT_MAREU
        );

        //When : we get the list of projects
        List<Project> actualProjects = LiveDataTestUtil.getValue(projectDao.getProjects());

        //Then : the retrieved list contains the three projects sorted by id (insertion order)
        assertArrayEquals(expectedProjects.toArray(), actualProjects.toArray());
    }

    @Test
    public void insertAllDeleteOneAndGetProjects() throws InterruptedException {
        //Given : we insert three projects and delete the first one
        insertProjects();

        setProjectIds(); //We set id after insertion in database not to interfere with autogenerate

        Project projectToDelete =
                LiveDataTestUtil.getValue(projectDao.getProjects()).get(FIRST_POSITION);

        projectDao.delete(projectToDelete); //We delete the project in database

        List<Project> expectedProjects = Arrays.asList( //We prepare id sorted expected list
                PROJECT_ENTREVOISINS,
                PROJECT_MAREU
        );

        //When : we get the list of projects
        List<Project> actualProjects = LiveDataTestUtil.getValue(projectDao.getProjects());

        //Then : the retrieved list contains the two projects sorted by id (insertion order)
        assertArrayEquals(expectedProjects.toArray(), actualProjects.toArray());
    }

    private void insertProjects() {
        projectDao.insert(PROJECT_MAGICGITHUB);
        projectDao.insert(PROJECT_ENTREVOISINS);
        projectDao.insert(PROJECT_MAREU);
    }

    private void setProjectIds() {
        PROJECT_MAGICGITHUB.setId(PROJECT_MAGICGITHUB_ID);
        PROJECT_ENTREVOISINS.setId(PROJECT_ENTREVOISINS_ID);
        PROJECT_MAREU.setId(PROJECT_MAREU_ID);
    }
}