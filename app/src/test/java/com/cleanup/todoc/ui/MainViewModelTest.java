package com.cleanup.todoc.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;
import com.cleanup.todoc.model.entity.relation.TaskWithProject;
import com.cleanup.todoc.model.repository.ProjectRepository;
import com.cleanup.todoc.model.repository.TaskRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {


    @Mock
    private ProjectRepository mockProjectRepository;
    @Mock
    private TaskRepository mockTaskRepository;

    private MainViewModel underTestMainViewModel;

    //MainViewModel LiveDatas from repositories to be mocked
    private final MutableLiveData<List<Project>> allProjectsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<TaskWithProject>> allTaskWithProjectMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<TaskWithProject>> allTaskWithProjectAZMutableLiveData = new MutableLiveData<>();
    //private final MutableLiveData<Utils.SortMethod> sortMethodMutableLiveData = new MutableLiveData<>();
    //private final MediatorLiveData<List<TaskWithProject>> sortedListForDisplayMediatorLiveData = new MediatorLiveData<>();
    //private final MutableLiveData<TaskListViewState> taskListViewStateMutableLiveData = new MutableLiveData<>();


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        allProjectsLiveDataMocking();
        allTaskWithProjectLiveDataMocking();
        allTaskWithProjectAZLiveDataMocking();

        underTestMainViewModel = new MainViewModel(mockProjectRepository, mockTaskRepository);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void insertProject() {
        //Given :
        Project projectToInsert = getAllProjectsForTest().get(0);
        //When :
        underTestMainViewModel.insertProject(projectToInsert);
        //Then :
        verify(mockProjectRepository, times(1)).insert(projectToInsert);
    }

    @Test
    public void deleteProject() {
        //Given :
        Project projectToDelete = getAllProjectsForTest().get(0);
        //When :
        underTestMainViewModel.deleteProject(projectToDelete);
        //Then :
        verify(mockProjectRepository, times(1)).delete(projectToDelete);
    }

    @Test
    public void getAllProjects() {
        //Given :
        //When :
        underTestMainViewModel.getAllProjects();
        //Then :
        verify(mockProjectRepository, times(1)).getAllProjects();
    }

    @Test
    public void insertTask() {
    }

    @Test
    public void updateTask() {
    }

    @Test
    public void deleteTask() {
    }

    @Test
    public void setSorting() {
    }

    @Test
    public void getSortMethod() {
    }

    @Test
    public void getSortedList() {
    }

    @Test
    public void getTaskListViewState() {
    }

    @Test
    public void createEditTask() {
    }

    @Test
    public void getEmptyTaskNameError() {
    }

    @Test
    public void getTaskCreatedEditedMsg() {
    }

    @Test
    public void getDialogDismiss() {
    }

    @Test
    public void getAddEditDialogViewState() {
    }


    private void allProjectsLiveDataMocking() {
        allProjectsMutableLiveData.setValue(getAllProjectsForTest());
        doReturn(allProjectsMutableLiveData).when(mockProjectRepository).getAllProjects();
    }

    private void allTaskWithProjectLiveDataMocking() {
        List<TaskWithProject> allTaskWithProject = getAllTaskWithProjectForTest();

        allTaskWithProjectMutableLiveData.setValue(allTaskWithProject);
        doReturn(allTaskWithProjectMutableLiveData).when(mockTaskRepository).getAllTaskWithProject();
    }

    private void allTaskWithProjectAZLiveDataMocking() {
        List<TaskWithProject> allTaskWithProjectAZ = Arrays.asList(
                getAllTaskWithProjectForTest().get(2),
                getAllTaskWithProjectForTest().get(5),
                getAllTaskWithProjectForTest().get(1),
                getAllTaskWithProjectForTest().get(4),
                getAllTaskWithProjectForTest().get(0),
                getAllTaskWithProjectForTest().get(3)
        );

        allTaskWithProjectAZMutableLiveData.setValue(allTaskWithProjectAZ);
        doReturn(allTaskWithProjectAZMutableLiveData).when(mockTaskRepository).getAllTaskWithProjectAZ();
    }

    private List<Project> getAllProjectsForTest() {
        List<Project> allProjects = Arrays.asList(
                new Project("Projet Tartampion", 0xFFEADAD1),
                new Project("Projet Lucidia", 0xFFB4CDBA),
                new Project("Projet Circus", 0xFFA3CED2)
        );

        return allProjects;
    }

    private List<TaskWithProject> getAllTaskWithProjectForTest() {
        Task task1 = new Task(1, 1, "task 1", new Date().getTime());
        Task task2 = new Task(2, 2, "task 2", new Date().getTime());
        Task task3 = new Task(3, 3, "task 3", new Date().getTime());
        Task task4 = new Task(4, 1, "task 4", new Date().getTime());
        Task task5 = new Task(5, 2, "task 5", new Date().getTime());
        Task task6 = new Task(6, 3, "task 6", new Date().getTime());

        TaskWithProject taskWithProject1 = new TaskWithProject();
        taskWithProject1.setProject(getAllProjectsForTest().get(0));
        taskWithProject1.setTask(task1);

        TaskWithProject taskWithProject2 = new TaskWithProject();
        taskWithProject1.setProject(getAllProjectsForTest().get(1));
        taskWithProject1.setTask(task2);

        TaskWithProject taskWithProject3 = new TaskWithProject();
        taskWithProject1.setProject(getAllProjectsForTest().get(2));
        taskWithProject1.setTask(task3);

        TaskWithProject taskWithProject4 = new TaskWithProject();
        taskWithProject1.setProject(getAllProjectsForTest().get(0));
        taskWithProject1.setTask(task4);

        TaskWithProject taskWithProject5 = new TaskWithProject();
        taskWithProject1.setProject(getAllProjectsForTest().get(1));
        taskWithProject1.setTask(task5);

        TaskWithProject taskWithProject6 = new TaskWithProject();
        taskWithProject1.setProject(getAllProjectsForTest().get(2));
        taskWithProject1.setTask(task6);

        List<TaskWithProject> allTaskWithProject = Arrays.asList(
                taskWithProject1, taskWithProject2, taskWithProject3,
                taskWithProject4, taskWithProject5, taskWithProject6
        );

        return allTaskWithProject;
    }
}