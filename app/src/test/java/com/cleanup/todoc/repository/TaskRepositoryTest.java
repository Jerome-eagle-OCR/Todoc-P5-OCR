package com.cleanup.todoc.repository;

import com.cleanup.todoc.model.dao.TaskDao;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.repository.TaskRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskRepositoryTest {

    private TaskRepository underTestTaskRepository;

    public static final Task TASK_MOCKITO = new Task(1,"Bien utiliser mockito", 0);

    @Mock
    TaskDao mockTaskDao;

    @Before
    public void setUnderTestTaskRepository() {
        underTestTaskRepository = new TaskRepository(mockTaskDao);
    }

    @Test
    public void verifyInsertCallsDaoInsert() {
        //Given :
        //When :
        underTestTaskRepository.insert(TASK_MOCKITO);
        //Then :
        verify(mockTaskDao, after(50).times(1)).insert(TASK_MOCKITO);
    }

    @Test
    public void verifyUpdateCallsDaoUpdate() {
        //Given :
        //When :
        underTestTaskRepository.update(TASK_MOCKITO);
        //Then :
        verify(mockTaskDao, after(50).times(1)).update(TASK_MOCKITO);
    }

    @Test
    public void verifyDeleteCallsDaoDelete() {
        //Given :
        //When :
        underTestTaskRepository.delete(TASK_MOCKITO);
        //Then :
        verify(mockTaskDao, after(50).times(1)).delete(TASK_MOCKITO);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void verifyGetAllTaskWithProjectCallsGetAllTaskWithProject() {
        //Given :
        //When :
        underTestTaskRepository.getAllTaskWithProject();
        //Then :
        verify(mockTaskDao, times(1)).getAllTaskWithProject();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void verifyGetAllTaskWithProjectAZCallsGetAllTaskWithProjectAZ() {
        //Given :
        //When :
        underTestTaskRepository.getAllTaskWithProjectAZ();
        //Then :
        verify(mockTaskDao, times(1)).getAllTaskWithProjectAZ();
    }

}
