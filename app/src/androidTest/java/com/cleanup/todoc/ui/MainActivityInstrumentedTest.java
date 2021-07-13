package com.cleanup.todoc.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cleanup.todoc.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.cleanup.todoc.TestUtils.withRecyclerView;
import static com.cleanup.todoc.ui.MainViewModel.NO_UPDATED_TASK_SNK;
import static com.cleanup.todoc.ui.MainViewModel.TASK_ADDED_SNK;
import static com.cleanup.todoc.ui.MainViewModel.TASK_UPDATED_SNK;
import static com.cleanup.todoc.ui.UiTestModel.ADD_TASK_NAME;
import static com.cleanup.todoc.ui.UiTestModel.CIRCUS_PROJECT_INDEX;
import static com.cleanup.todoc.ui.UiTestModel.CIRCUS_TASK1;
import static com.cleanup.todoc.ui.UiTestModel.CIRCUS_TASK2;
import static com.cleanup.todoc.ui.UiTestModel.CIRCUS_TASK3;
import static com.cleanup.todoc.ui.UiTestModel.CORRECTED_TASK_NAME;
import static com.cleanup.todoc.ui.UiTestModel.EIGHT;
import static com.cleanup.todoc.ui.UiTestModel.FIVE;
import static com.cleanup.todoc.ui.UiTestModel.FOUR;
import static com.cleanup.todoc.ui.UiTestModel.LUCIDIA_PROJECT_INDEX;
import static com.cleanup.todoc.ui.UiTestModel.LUCIDIA_TASK1;
import static com.cleanup.todoc.ui.UiTestModel.LUCIDIA_TASK2;
import static com.cleanup.todoc.ui.UiTestModel.LUCIDIA_TASK3;
import static com.cleanup.todoc.ui.UiTestModel.NINE;
import static com.cleanup.todoc.ui.UiTestModel.ONE;
import static com.cleanup.todoc.ui.UiTestModel.PROJECT_CIRCUS;
import static com.cleanup.todoc.ui.UiTestModel.SEVEN;
import static com.cleanup.todoc.ui.UiTestModel.SIX;
import static com.cleanup.todoc.ui.UiTestModel.TARTAMPION_PROJECT_INDEX;
import static com.cleanup.todoc.ui.UiTestModel.TARTAMPION_TASK1;
import static com.cleanup.todoc.ui.UiTestModel.TARTAMPION_TASK2;
import static com.cleanup.todoc.ui.UiTestModel.TARTAMPION_TASK3;
import static com.cleanup.todoc.ui.UiTestModel.THREE;
import static com.cleanup.todoc.ui.UiTestModel.TWO;
import static com.cleanup.todoc.ui.UiTestModel.WRONG_TASK_NAME;
import static com.cleanup.todoc.ui.UiTestModel.ZERO;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @author Gaëtan HERFRAY
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    private ActivityScenario<MainActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        activityScenario.close();
    }

    @Test
    public void addAndRemoveAndUndoRemoveTaskSuccessfully() {
        // We check "no task" label is displayed
        this.assertNoTaskVisibility();

        // We add a new task
        this.addTask(CIRCUS_PROJECT_INDEX, ADD_TASK_NAME);

        // We check that "no task" label is not anymore displayed but now task list is
        this.assertAtLeastOneTaskVisibility();

        // We check that task list contains only one element
        this.assertTaskCount(ONE);

        // We delete the previously added task
        this.deleteTask(ZERO);

        //Adapter list should be empty
        activityScenario.onActivity(activity -> assertEquals(ZERO,
                activity.getTaskAdapterCount()));

        // We check "no task" label is displayed
        this.assertNoTaskVisibility();

        // We click UNDO on the snackbar to undelete the task
        onView(ViewMatchers.withText(R.string.undo_snk)).perform(click());

        // We check that "no task" label is not anymore displayed but now task list is
        this.assertAtLeastOneTaskVisibility();

        // We check that task list contains one element again
        this.assertTaskCount(ONE);
    }

    @Test
    public void addAndUpdateTaskSuccessfully() {
        // We check "no task" label is displayed
        this.assertNoTaskVisibility();

        //We add a new task
        this.addTask(TARTAMPION_PROJECT_INDEX, WRONG_TASK_NAME);

        // We check that "no task" label is not anymore displayed but now task list is
        this.assertAtLeastOneTaskVisibility();

        // we check that task list contains only one element
        this.assertTaskCount(ONE);

        // We long click on the task item in order to edit it
        onView(withRecyclerView(R.id.list_tasks).atPosition(ZERO)).perform(longClick());

        // We update the task to correct its name and associated project
        this.updateTask(true, CIRCUS_PROJECT_INDEX, CORRECTED_TASK_NAME);

        //The task name has been properly updated
        onView(withRecyclerView(R.id.list_tasks)
                .atPosition(ZERO))
                .check((matches(hasDescendant(allOf(withId(R.id.lbl_task_name),
                        withText(CORRECTED_TASK_NAME))))));
        onView(withRecyclerView(R.id.list_tasks)
                .atPosition(ZERO))
                .check((matches(hasDescendant(allOf(withId(R.id.lbl_project_name),
                        withText(PROJECT_CIRCUS))))));
    }

    @Test
    public void addAndUpdateTaskWithoutChangesSuccessfully() {
        // We check "no task" label is displayed
        this.assertNoTaskVisibility();

        //We add a new task
        addTask(CIRCUS_PROJECT_INDEX, ADD_TASK_NAME);

        // We check that "no task" label is not anymore displayed but now task list is
        this.assertAtLeastOneTaskVisibility();

        // We check that task list contains only one element
        this.assertTaskCount(1);

        // We perform long click on the task item in order to edit it
        onView(withRecyclerView(R.id.list_tasks).atPosition(ZERO)).perform(longClick());

        //We fakely modify the task name
        this.updateTask(false, CIRCUS_PROJECT_INDEX, ADD_TASK_NAME);

        //The task has not changed, same task name, same project name
        onView(withRecyclerView(R.id.list_tasks)
                .atPosition(ZERO))
                .check((matches(hasDescendant(allOf(withId(R.id.lbl_task_name),
                        withText(ADD_TASK_NAME))))));
        onView(withRecyclerView(R.id.list_tasks)
                .atPosition(ZERO))
                .check((matches(hasDescendant(allOf(withId(R.id.lbl_project_name),
                        withText(PROJECT_CIRCUS))))));
    }

    @Test
    public void sortTasksSuccessfully() {
        // We check "no task" label is displayed
        assertNoTaskVisibility();

        // We add Circus project tasks
        addTasks(CIRCUS_PROJECT_INDEX, CIRCUS_TASK1, CIRCUS_TASK2, CIRCUS_TASK3);

        // Check we have 3 tasks
        this.assertTaskCount(THREE);

        // By default sorting is old first
        this.assertOldNewSorting();

        // We set old first sorting by clicking the option in the menu
        onView(withId(R.id.action_sort)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());

        // Then sorting must be old first
        this.assertOldNewSorting();

        // We set recent first sorting by clicking the option in the menu
        onView(withId(R.id.action_sort)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());

        // Then sorting must be recent first
        this.assertNewOldSorting();

        // We add Lucidia and Tartampion projects tasks to test sorting grouped by project
        addTasks(LUCIDIA_PROJECT_INDEX, LUCIDIA_TASK1, LUCIDIA_TASK2, LUCIDIA_TASK3);
        addTasks(TARTAMPION_PROJECT_INDEX, TARTAMPION_TASK1, TARTAMPION_TASK2, TARTAMPION_TASK3);

        // Check we have now 9 tasks
        activityScenario.onActivity(activity -> assertEquals(NINE, activity.getTaskAdapterCount()));

        // We set grouped by project sorting by clicking the option in the menu
        onView(withId(R.id.action_sort)).perform(click());
        onView(withText(R.string.sort_by_project)).perform(click());

        // Then tasks must be grouped by projects (sorted AZ while tasks sorted old first)
        this.assertGroupedByProjectAZSorting();
    }


    private void addTasks(int projectPosition,
                          String taskName1, String taskName2, String taskName3) {
        addTask(projectPosition, taskName1);
        addTask(projectPosition, taskName2);
        addTask(projectPosition, taskName3);
    }

    private void addTask(int projectPosition, String taskName) {
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText(taskName));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything())
                .atPosition(projectPosition)
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        //A snackbar is displayed to confirm creation
        this.assertSnackbarMsg(TASK_ADDED_SNK);
    }

    private void deleteTask(int taskPosition) {
        //Swiping on the right deletes the task
        onView(withRecyclerView(R.id.list_tasks).atPosition(taskPosition)).perform(swipeRight());

        //A snackbar is displayed to confirm deletion
        onView(withText(R.string.task_deleted_snk)).check(matches(isDisplayed()));
    }

    private void updateTask(boolean updated, int projectPosition, String taskName) {
        onView(withId(R.id.txt_task_name)).perform(replaceText(taskName));
        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything())
                .atPosition(projectPosition)
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        //A snackbar is displayed to confirm updating
        if (updated) {
            this.assertSnackbarMsg(TASK_UPDATED_SNK);
        } else {
            this.assertSnackbarMsg(NO_UPDATED_TASK_SNK);
        }
    }

    private void assertSnackbarMsg(String message) {
        //A snackbar is displayed to confirm creation, updating or non updating
        onView(withText(message)).check(matches(isDisplayed()));
    }

    private void assertNoTaskVisibility() {
        // Check that the "no task" label is displayed
        onView(withId(R.id.lbl_no_task))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        // Check that the task list is not displayed
        onView(withId(R.id.list_tasks))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    private void assertAtLeastOneTaskVisibility() {
        // Check that the "no task" label is not displayed
        onView(withId(R.id.lbl_no_task))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        // Check that the task list is displayed
        onView(withId(R.id.list_tasks))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    private void assertTaskCount(int count) {
        if (count != ZERO) onView(withId(R.id.list_tasks)).check(matches(hasChildCount(count)));
    }

    private void assertOldNewSorting() {
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(ZERO, R.id.lbl_task_name))
                .check(matches(withText(CIRCUS_TASK1)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(ONE, R.id.lbl_task_name))
                .check(matches(withText(CIRCUS_TASK2)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(TWO, R.id.lbl_task_name))
                .check(matches(withText(CIRCUS_TASK3)));
    }

    private void assertNewOldSorting() {
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(ZERO, R.id.lbl_task_name))
                .check(matches(withText(CIRCUS_TASK3)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(ONE, R.id.lbl_task_name))
                .check(matches(withText(CIRCUS_TASK2)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(TWO, R.id.lbl_task_name))
                .check(matches(withText(CIRCUS_TASK1)));
    }

    private void assertGroupedByProjectAZSorting() {
        // For project "Projet Circus"
        this.assertOldNewSorting();

        // For project "Projet Lucidia"
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(THREE, R.id.lbl_task_name))
                .check(matches(withText(LUCIDIA_TASK1)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(FOUR, R.id.lbl_task_name))
                .check(matches(withText(LUCIDIA_TASK2)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(FIVE, R.id.lbl_task_name))
                .check(matches(withText(LUCIDIA_TASK3)));

        // For project "Projet Tartampion"
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(SIX, R.id.lbl_task_name))
                .check(matches(withText(TARTAMPION_TASK1)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(SEVEN, R.id.lbl_task_name))
                .check(matches(withText(TARTAMPION_TASK2)));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(EIGHT, R.id.lbl_task_name))
                .check(matches(withText(TARTAMPION_TASK3)));
    }
}
