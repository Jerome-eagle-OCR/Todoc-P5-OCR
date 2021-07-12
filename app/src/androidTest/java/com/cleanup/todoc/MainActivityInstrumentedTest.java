package com.cleanup.todoc;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cleanup.todoc.ui.MainActivity;
import com.cleanup.todoc.ui.MainViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.cleanup.todoc.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @author Gaëtan HERFRAY
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    //@Rule
    //public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

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
    public void addAndRemoveAndUndoRemoveTask() {
        //We add a new task
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("Peigner la girafe"));
        /*onView(withId(R.id.project_spinner)).perform(click());
        onData(anything())
                .inRoot(is(instanceOf(ArrayAdapter.class)))
                .atPosition(1)
                .perform(click());*/
        onView(withId(android.R.id.button1)).perform(click());

        //A snackbar is displayed to confirm creation
        onView(withText(MainViewModel.TASK_ADDED_SNK)).check(matches(isDisplayed()));

        //Set and wire widgets to be able to assert on their visibilities
        AtomicReference<TextView> lblNoTask = new AtomicReference<>();
        AtomicReference<RecyclerView> listTasks = new AtomicReference<>();

        activityScenario.onActivity(MainActivity -> {
            lblNoTask.set(MainActivity.findViewById(R.id.lbl_no_task));
            listTasks.set(MainActivity.findViewById(R.id.list_tasks));
        });

        // Check that lblTask is not displayed anymore
        assertEquals(lblNoTask.get().getVisibility(), View.GONE);
        // Check that recyclerView is displayed
        assertEquals(listTasks.get().getVisibility(), View.VISIBLE);
        // Check that it contains one element only
        assertEquals(listTasks.get().getAdapter().getItemCount(), 1);//Task added

        //Swiping on the right deletes the task
        onView(withRecyclerView(R.id.list_tasks).atPosition(0)).perform(swipeRight());
        //A snackbar is displayed to confirm deletion
        onView(withText(R.string.task_deleted_snk)).check(matches(isDisplayed()));
        //List should be empty
        assertEquals(listTasks.get().getAdapter().getItemCount(), 0);
        // Check that lblTask is displayed
        assertEquals(lblNoTask.get().getVisibility(), View.VISIBLE);
        // Check that recyclerView is not displayed anymore
        assertEquals(listTasks.get().getVisibility(), View.GONE);

        //Click on the snackbar UNDO to undelete the task
        onView(withText(R.string.undo_snk)).perform(click());

        // Check that lblTask is not displayed anymore
        assertEquals(lblNoTask.get().getVisibility(), View.GONE);
        // Check that recyclerView is displayed
        assertEquals(listTasks.get().getVisibility(), View.VISIBLE);
        // Check that it contains one element again
        assertEquals(listTasks.get().getAdapter().getItemCount(), 1);
    }

    @Test
    public void addAndUpdateTask() {
        //We add a new task
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("Rosser le mammouth"));
        onView(withId(android.R.id.button1)).perform(click());

        //Set and wire widgets to be able to assert on their visibilities
        AtomicReference<TextView> lblNoTask = new AtomicReference<>();
        AtomicReference<RecyclerView> listTasks = new AtomicReference<>();

        activityScenario.onActivity(MainActivity -> {
            lblNoTask.set(MainActivity.findViewById(R.id.lbl_no_task));
            listTasks.set(MainActivity.findViewById(R.id.list_tasks));
        });

        // Check that lblTask is not displayed anymore
        assertEquals(lblNoTask.get().getVisibility(), View.GONE);
        // Check that recyclerView is displayed
        assertEquals(listTasks.get().getVisibility(), View.VISIBLE);
        // Check that it contains one element only
        assertEquals(listTasks.get().getAdapter().getItemCount(), 1);//Task added

        onView(withRecyclerView(R.id.list_tasks).atPosition(0)).perform(longClick());

        //We modify the task name
        onView(withId(R.id.txt_task_name)).perform(replaceText("Brosser l'éléphant"));
        onView(withId(android.R.id.button1)).perform(click());

        //A snackbar is displayed to confirm updating
        onView(withText(MainViewModel.TASK_UPDATED_SNK)).check(matches(isDisplayed()));

        //The task name has been properly updated
        onView(withRecyclerView(R.id.list_tasks)
                .atPosition(0))
                .check((matches(hasDescendant(allOf(withId(R.id.lbl_task_name), withText("Brosser l'éléphant"))))));
    }

    @Test
    public void addAndUpdateWithNoChangesTask() {
        //We add a new task
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("Dresser le tigre"));
        onView(withId(android.R.id.button1)).perform(click());

        //Set and wire widgets to be able to assert on their visibilities
        AtomicReference<TextView> lblNoTask = new AtomicReference<>();
        AtomicReference<RecyclerView> listTasks = new AtomicReference<>();

        activityScenario.onActivity(MainActivity -> {
            lblNoTask.set(MainActivity.findViewById(R.id.lbl_no_task));
            listTasks.set(MainActivity.findViewById(R.id.list_tasks));
        });

        // Check that lblTask is not displayed anymore
        assertEquals(lblNoTask.get().getVisibility(), View.GONE);
        // Check that recyclerView is displayed
        assertEquals(listTasks.get().getVisibility(), View.VISIBLE);
        // Check that it contains one element only
        assertEquals(listTasks.get().getAdapter().getItemCount(), 1);//Task added

        onView(withRecyclerView(R.id.list_tasks).atPosition(0)).perform(longClick());

        //We modify the task name
        onView(withId(R.id.txt_task_name)).perform(replaceText("Doucher l'hippopotame"));
        onView(withId(android.R.id.button1)).perform(click());

        //A snackbar is displayed to advise no updating has been done
        onView(withText(MainViewModel.NO_UPDATED_TASK_SNK)).check(matches(isDisplayed()));

        //The task name has not been updated
        onView(withRecyclerView(R.id.list_tasks)
                .atPosition(0))
                .check((matches(hasDescendant(allOf(withId(R.id.lbl_task_name), withText("Doucher l'hippopotame"))))));
    }

    @Test
    public void sortTasks() {
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("zzz Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("hhh Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());

        //Sort old first by default
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                .check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                .check(matches(withText("hhh Tâche example")));

        // Sort old first
        onView(withId(R.id.action_sort)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                .check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                .check(matches(withText("hhh Tâche example")));

        // Sort recent first
        onView(withId(R.id.action_sort)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                .check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                .check(matches(withText("aaa Tâche example")));

        //TODO
        // Grouped by project (name sorted))
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("zzz Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("hhh Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.action_sort)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                .check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                .check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                .check(matches(withText("aaa Tâche example")));
    }
}
