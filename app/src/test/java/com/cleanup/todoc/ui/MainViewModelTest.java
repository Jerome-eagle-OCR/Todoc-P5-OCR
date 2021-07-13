package com.cleanup.todoc.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.cleanup.todoc.R;
import com.cleanup.todoc.Utils;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ResultOfMethodCallIgnored")
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


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        //Mock LiveDatas from repositories
        mocking_allProjectsLiveData();
        mocking_allTaskWithProjectLiveData();
        mocking_allTaskWithProjectAZLiveData();

        //Instantiate MainViewModel for testing, passing mocked repositories
        underTestMainViewModel = new MainViewModel(mockProjectRepository, mockTaskRepository);
    }

    @After
    public void tearDown() throws Exception {
    }


    //Verifying ProjectRepository callings

    @Test
    public void verifyInsertProjectCallsProjectRepositoryInsert() {
        //Given :
        Project projectToInsert = this.allProjectsMutableLiveData.getValue().get(0);
        //When :
        underTestMainViewModel.insertProject(projectToInsert);
        //Then :
        verify(mockProjectRepository, times(1)).insert(projectToInsert);
    }

    @Test
    public void verifyDeleteProjectCallsProjectRepositoryDelete() {
        //Given :
        Project projectToDelete = this.allProjectsMutableLiveData.getValue().get(0);
        //When :
        underTestMainViewModel.deleteProject(projectToDelete);
        //Then :
        verify(mockProjectRepository, times(1)).delete(projectToDelete);
    }

    @Test
    public void getAllProjectsShouldCallProjectRepositoryGetAllProjectsAndReturnAllProjects() {
        //Given :
        //When :
        underTestMainViewModel.getAllProjects();
        //Then :
        verify(mockProjectRepository, times(1)).getAllProjects();
        assert (underTestMainViewModel.getAllProjects().getValue() == this.allProjectsMutableLiveData.getValue());
    }


    //Verifying TaskRepository callings

    @Test
    public void verifyInsertTaskCallsTaskRepositoryInsert() {
        //Given :
        Task taskToInsert = this.allTaskWithProjectMutableLiveData.getValue().get(0).getTask();
        //When :
        underTestMainViewModel.insertTask(taskToInsert);
        //Then :
        verify(mockTaskRepository, times(1)).insert(taskToInsert);
    }

    @Test
    public void verifyUpdateTaskCallsTaskRepositoryUpdate() {
        //Given :
        Task taskToUpdate = this.allTaskWithProjectMutableLiveData.getValue().get(0).getTask();
        //When :
        underTestMainViewModel.updateTask(taskToUpdate);
        //Then :
        verify(mockTaskRepository, times(1)).update(taskToUpdate);
    }

    @Test
    public void verifyDeleteTaskCallsTaskRepositoryDelete() {
        //Given :
        Task taskToDelete = this.allTaskWithProjectMutableLiveData.getValue().get(0).getTask();
        //When :
        underTestMainViewModel.deleteTask(taskToDelete);
        //Then :
        verify(mockTaskRepository, times(1)).delete(taskToDelete);
    }


    //Testing scenarios for setSorting(SortMethod)

    @Test
    public void givenNoTaskThenSetSortingAnySortMethodShouldMeetAllAssertions() {
        //Given :
        allTaskWithProjectMutableLiveData.setValue(new ArrayList<>());
        allTaskWithProjectAZMutableLiveData.setValue(new ArrayList<>());
        //When :
        underTestMainViewModel.setSorting(Utils.SortMethod.PROJECT_AZ);
        //Then :
        assert (underTestMainViewModel.getSortMethod().getValue() == Utils.SortMethod.PROJECT_AZ);
        //The task list to be displayed is empty
        assert (underTestMainViewModel.getSortedList().getValue().isEmpty());
        //No task label is visible and the task list is gone
        assert (underTestMainViewModel.getTaskListViewState().getValue().getNoTaskLblVisibility() == View.VISIBLE);
        assert (underTestMainViewModel.getTaskListViewState().getValue().getTaskListVisibility() == View.GONE);
    }

    @Test
    public void givenAllTestTasksThenSetSortingNONEShouldMeetAllAssertions() {
        //Given :
        //When :
        underTestMainViewModel.setSorting(Utils.SortMethod.NONE);
        //Then :
        assert (underTestMainViewModel.getSortMethod().getValue() == Utils.SortMethod.NONE);
        //The task list to be displayed is full and properly sorted
        assert (underTestMainViewModel.getSortedList().getValue() == allTaskWithProjectMutableLiveData.getValue());
        //No task label is gone and the task list is visible
        assert (underTestMainViewModel.getTaskListViewState().getValue().getNoTaskLblVisibility() == View.GONE);
        assert (underTestMainViewModel.getTaskListViewState().getValue().getTaskListVisibility() == View.VISIBLE);
    }

    @Test
    public void givenAllTestTasksThenSetSortingOLD_FIRSTShouldMeetAllAssertions() {
        //Given :
        //When :
        underTestMainViewModel.setSorting(Utils.SortMethod.OLD_FIRST);
        //Then :
        assert (underTestMainViewModel.getSortMethod().getValue() == Utils.SortMethod.OLD_FIRST);
        //The task list to be displayed is full and properly sorted
        assert (underTestMainViewModel.getSortedList().getValue() == allTaskWithProjectMutableLiveData.getValue());
        //No task label is gone and the task list is visible
        assert (underTestMainViewModel.getTaskListViewState().getValue().getNoTaskLblVisibility() == View.GONE);
        assert (underTestMainViewModel.getTaskListViewState().getValue().getTaskListVisibility() == View.VISIBLE);
    }

    @Test
    public void givenAllTestTasksThenSetSortingRECENT_FIRSTShouldMeetAllAssertions() {
        //Given :
        //When :
        underTestMainViewModel.setSorting(Utils.SortMethod.RECENT_FIRST);
        //Then :
        assert (underTestMainViewModel.getSortMethod().getValue() == Utils.SortMethod.RECENT_FIRST);
        //The task list to be displayed is full and properly sorted
        List<TaskWithProject> expectedSortedList = new ArrayList<>(this.allTaskWithProjectMutableLiveData.getValue());
        Collections.reverse(expectedSortedList); //Get a list sorted by date descending
        assert (underTestMainViewModel.getSortedList().getValue().equals(expectedSortedList));
        //No task label is gone and the task list is visible
        assert (underTestMainViewModel.getTaskListViewState().getValue().getNoTaskLblVisibility() == View.GONE);
        assert (underTestMainViewModel.getTaskListViewState().getValue().getTaskListVisibility() == View.VISIBLE);
    }

    @Test
    public void givenAllTestTasksThenSetSortingPROJECT_AZShouldMeetAllAssertions() {
        //Given :
        //When :
        underTestMainViewModel.setSorting(Utils.SortMethod.PROJECT_AZ);
        //Then :
        assert (underTestMainViewModel.getSortMethod().getValue() == Utils.SortMethod.PROJECT_AZ);
        //The task list to be displayed is full and properly sorted
        assert (underTestMainViewModel.getSortedList().getValue() == this.allTaskWithProjectAZMutableLiveData.getValue());
        //No task label is gone and the task list is visible
        assert (underTestMainViewModel.getTaskListViewState().getValue().getNoTaskLblVisibility() == View.GONE);
        assert (underTestMainViewModel.getTaskListViewState().getValue().getTaskListVisibility() == View.VISIBLE);
    }


    //Testing scenarios for getAddEditDialogViewState(TaskWithProject)

    @Test
    public void givenNoTaskToEditThenGetAddEditDialogViewStateShouldReturnViewStateForAddingPurpose() {
        //Given :
        //When :
        underTestMainViewModel.getAddEditDialogViewState(null);
        //Then :
        assert (underTestMainViewModel.getAddEditDialogViewState(null).equals(this.getAddDialogViewState()));
    }

    @Test
    public void givenATaskToEditThenGetAddEditDialogViewStateShouldReturnViewStateForEditingPurpose() {
        //Given :
        TaskWithProject testTask5 = this.allTaskWithProjectMutableLiveData.getValue().get(4);
        //When :
        underTestMainViewModel.getAddEditDialogViewState(testTask5);
        //Then :
        assert (underTestMainViewModel.getAddEditDialogViewState(testTask5).equals(this.getEditDialogViewStateForTestTask5()));
    }


    //Testing scenarios for createEditTask(TaskWithProject, EditText, Spinner)

    @Test
    public void givenNoTaskToEditNullEditTextNullSpinnerThenCreateEditTaskShouldMeetAllAssertions() {
        //Given :
        //When :
        underTestMainViewModel.createEditTask(null, null, null);
        //Then :
        //No task should be created
        verify(mockTaskRepository, times(0)).insert(any(Task.class));
        verify(mockTaskRepository, times(0)).update(any(Task.class));
        //Task name empty text error boolean should be false
        assert (!underTestMainViewModel.getEmptyTaskNameError());
        //Snackbar message should be empty
        assert (underTestMainViewModel.getTaskCreatedEditedMsg().isEmpty());
        //Dialog dismiss boolean should be true
        assert (underTestMainViewModel.getDialogDismiss());
    }

    @Test
    public void givenNoTaskToEditButNoTaskNameThenCreateEditTaskShouldMeetAllAssertions() {
        //Given :
        EditText editText = getEditText("");
        Spinner spinner = getSpinner();
        //When :
        underTestMainViewModel.createEditTask(null, editText, spinner);
        //Then :
        //No task should be created
        verify(mockTaskRepository, times(0)).insert(any(Task.class));
        verify(mockTaskRepository, times(0)).update(any(Task.class));
        //Task name empty text error boolean should be true
        assert (underTestMainViewModel.getEmptyTaskNameError());
        //Snackbar message should be empty
        assert (underTestMainViewModel.getTaskCreatedEditedMsg().isEmpty());
        //Dialog should not be dismissed
        assert (!underTestMainViewModel.getDialogDismiss());
    }

    @Test
    public void givenNoTaskToEditThenCreateEditTaskShouldMeetAllAssertions() {
        //Given :
        EditText editText = getEditText("Nouvelle tâche");
        Spinner spinner = getSpinner();
        //When :
        underTestMainViewModel.createEditTask(null, editText, spinner);
        //Then :
        //A task should be created
        verify(mockTaskRepository, times(1)).insert(any(Task.class));
        verify(mockTaskRepository, times(0)).update(any(Task.class));
        //Task name empty text error boolean should be false
        assert (!underTestMainViewModel.getEmptyTaskNameError());
        //Snackbar message should announce that a task has been created
        assert (underTestMainViewModel.getTaskCreatedEditedMsg().equals(MainViewModel.TASK_ADDED_SNK));
        //Dialog dismiss boolean should be true
        assert (underTestMainViewModel.getDialogDismiss());
    }

    @Test
    public void givenATaskToEditButNoChangeThenCreateEditTaskShouldMeetAllAssertions() {
        //Given :
        TaskWithProject testTask5 = this.allTaskWithProjectMutableLiveData.getValue().get(4);
        EditText editText = getEditText(testTask5.getTask().getName());//Same name
        Spinner spinner = getSpinner();//Spinner is set to project "Lucidia" as testTask5"
        //When :
        underTestMainViewModel.createEditTask(testTask5, editText, spinner);
        //Then :
        //No task should be updated nor created
        verify(mockTaskRepository, times(0)).update(any(Task.class));
        verify(mockTaskRepository, times(0)).insert(any(Task.class));
        //Task name empty text error boolean should be false
        assert (!underTestMainViewModel.getEmptyTaskNameError());
        //Snackbar message should announce that no task has been updated
        assert (underTestMainViewModel.getTaskCreatedEditedMsg().equals(MainViewModel.NO_UPDATED_TASK_SNK));
        //Dialog dismiss boolean should be true
        assert (underTestMainViewModel.getDialogDismiss());
    }

    @Test
    public void givenATaskToEditThenCreateEditTaskShouldMeetAllAssertions() {
        //Given :
        TaskWithProject testTask5 = this.allTaskWithProjectMutableLiveData.getValue().get(4);
        EditText editText = this.getEditText("Different name");//Different name
        Spinner spinner = this.getSpinner();//Spinner is set to project "Lucidia" as testTask5"
        //When :
        underTestMainViewModel.createEditTask(testTask5, editText, spinner);
        //Then :
        //The task should be updated
        verify(mockTaskRepository, times(0)).insert(any(Task.class));
        verify(mockTaskRepository, times(1)).update(any(Task.class));
        //Task name empty text error boolean should be false
        assert (!underTestMainViewModel.getEmptyTaskNameError());
        //Snackbar message should announce that the task has been updated
        assert (underTestMainViewModel.getTaskCreatedEditedMsg().equals(MainViewModel.TASK_UPDATED_SNK));
        //Dialog dismiss boolean should be true
        assert (underTestMainViewModel.getDialogDismiss());
    }


    //Mocking repositories LiveDatas

    private void mocking_allProjectsLiveData() {
        allProjectsMutableLiveData.setValue(getAllProjectsForTest());
        doReturn(allProjectsMutableLiveData).when(mockProjectRepository).getAllProjects();
    }

    private void mocking_allTaskWithProjectLiveData() {
        List<TaskWithProject> allTaskWithProject = getAllTaskWithProjectForTest();

        allTaskWithProjectMutableLiveData.setValue(allTaskWithProject);
        doReturn(allTaskWithProjectMutableLiveData).when(mockTaskRepository).getAllTaskWithProject();
    }

    private void mocking_allTaskWithProjectAZLiveData() {
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


    //Setting projects, tasks and taskWithProjects

    private List<Project> getAllProjectsForTest() {
        List<Project> allProjects = Arrays.asList(
                new Project("Projet Tartampion", 0xFFEADAD1),
                new Project("Projet Lucidia", 0xFFB4CDBA),
                new Project("Projet Circus", 0xFFA3CED2)
        );

        //set id (normally autogenerated)
        allProjects.get(0).setId(1);
        allProjects.get(1).setId(2);
        allProjects.get(2).setId(3);

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
        taskWithProject2.setProject(getAllProjectsForTest().get(1));
        taskWithProject2.setTask(task2);

        TaskWithProject taskWithProject3 = new TaskWithProject();
        taskWithProject3.setProject(getAllProjectsForTest().get(2));
        taskWithProject3.setTask(task3);

        TaskWithProject taskWithProject4 = new TaskWithProject();
        taskWithProject4.setProject(getAllProjectsForTest().get(0));
        taskWithProject4.setTask(task4);

        TaskWithProject taskWithProject5 = new TaskWithProject();
        taskWithProject5.setProject(getAllProjectsForTest().get(1));
        taskWithProject5.setTask(task5);

        TaskWithProject taskWithProject6 = new TaskWithProject();
        taskWithProject6.setProject(getAllProjectsForTest().get(2));
        taskWithProject6.setTask(task6);

        return Arrays.asList(
                taskWithProject1, taskWithProject2, taskWithProject3,
                taskWithProject4, taskWithProject5, taskWithProject6
        );
    }


    //Setting AddEditTaskDialogViewState

    private AddEditTaskDialogViewState getAddDialogViewState() {
        return new AddEditTaskDialogViewState(R.string.add_task, "", 0, R.string.add);
    }

    private AddEditTaskDialogViewState getEditDialogViewStateForTestTask5() {
        return new AddEditTaskDialogViewState(R.string.edit_task, "task 5", 1, R.string.edit);
    }


    //Setting EditText and Spinner

    private EditText getEditText(String taskName) {
        final EditText editText = mock(EditText.class);
        when(editText.getText()).thenReturn(new MockEditable(taskName));
        return editText;
    }

    private Spinner getSpinner() {
        final Spinner spinner = mock(Spinner.class);
        final Project project = Objects.requireNonNull(allProjectsMutableLiveData.getValue()).get(1);
        doReturn(project).when(spinner).getSelectedItem();
        return spinner;
    }
}