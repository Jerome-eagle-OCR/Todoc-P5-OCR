package com.cleanup.todoc.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.cleanup.todoc.model.dao.ProjectDao;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.repository.ProjectRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectRepositoryTest {

    private ProjectRepository underTestProjectRepository;

    private final Project PROJECT_MOCKITO = new Project("Projet Mockito", 0);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    ProjectDao mockProjectDao;

    @Before
    public void setUnderTestProjectRepository() {
        underTestProjectRepository = new ProjectRepository(mockProjectDao);
    }


    @Test
    public void verifyInsertCallsDaoInsert() {
        //Given :

        //When :
        underTestProjectRepository.insert(PROJECT_MOCKITO);
        //Then :
        verify(mockProjectDao, after(100).times(1)).insert(PROJECT_MOCKITO);
    }

    @Test
    public void verifyDeleteCallsDaoDelete() {
        //Given :

        //When :
        underTestProjectRepository.delete(PROJECT_MOCKITO);
        //Then :
        verify(mockProjectDao, after(100).times(1)).delete(PROJECT_MOCKITO);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void verifyGetAllProjectsCallsDaoGetProjects() {
        //Given :

        //When :
        underTestProjectRepository.getAllProjects();
        //Then :
        verify(mockProjectDao, times(1)).getProjects();
    }
}
