package com.cleanup.todoc;

import android.graphics.Color;

import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;

import java.util.Calendar;

public class TestModel {

    //Test projects

    //Project MagicGithub
    public static final long PROJECT_MAGICGITHUB_ID = 1;
    private static final String PROJECT_MAGICGITHUB_NAME = "Projet MagicGithub";
    private static final int PROJECT_MAGICGITHUB_COLOR = Color.parseColor("#76ff03");
    public static final Project PROJECT_MAGICGITHUB = new Project(PROJECT_MAGICGITHUB_NAME, PROJECT_MAGICGITHUB_COLOR);

    //Project Entrevoisins
    public static final long PROJECT_ENTREVOISINS_ID = 2;
    private static final String PROJECT_ENTREVOISINS_NAME = "Projet Entrevoisins";
    private static final int PROJECT_ENTREVOISINS_COLOR = Color.parseColor("#ffc400");
    public static final Project PROJECT_ENTREVOISINS = new Project(PROJECT_ENTREVOISINS_NAME, PROJECT_ENTREVOISINS_COLOR);

    //Project Maréu
    public static final int PROJECT_MAREU_ID = 3;
    private static final String PROJECT_MAREU_NAME = "Projet Maréu";
    private static final int PROJECT_MAREU_COLOR = Color.parseColor("#ab000d");
    public static final Project PROJECT_MAREU = new Project(PROJECT_MAREU_NAME, PROJECT_MAREU_COLOR);

    //Project Android
    public static final int PROJECT_ANDROID_ID = 4;
    private static final String PROJECT_ANDROID_NAME = "Projet Android";
    private static final int PROJECT_ANDROID_COLOR = Color.parseColor("#fafafa");
    public static final Project PROJECT_ANDROID = new Project(PROJECT_ANDROID_NAME, PROJECT_ANDROID_COLOR);


    //Test tasks

    //Tasks count
    public static final int TASKS_COUNT = 3;
    //Index in list
    public static final int FIRST_POSITION = 0;
    public static final int SECOND_POSITION = 1;
    public static final int THIRD_POSITION = 2;

    //Time delay between two tasks
    public static final long DELAY = 5184000000L;

    //Task "make tests go green"
    public static final long TASK_MAKE_TESTS_PASS_ID = 1;
    public static final int TASK_MAKE_TESTS_PASS_PROJECT_ID = 1;
    public static final String TASK_MAKE_TESTS_PASS_NAME = "Faire passer les tests au vert";
    public static final long TASK_MAKE_TESTS_PASS_TIMESTAMP = Calendar.getInstance().getTimeInMillis();
    public static final Task TASK_MAKE_TESTS_PASS =
            new Task(TASK_MAKE_TESTS_PASS_PROJECT_ID,
                    TASK_MAKE_TESTS_PASS_NAME,
                    TASK_MAKE_TESTS_PASS_TIMESTAMP
            );

    //Task "add a new functionality"
    public static final long TASK_NEIGHBOUR_DETAILS_ID = 2;
    public static final int TASK_NEIGHBOUR_DETAILS_PROJECT_ID = 2;
    public static final String TASK_NEIGHBOUR_DETAILS_NAME = "Ajouter une nouvelle fonctionnalité";
    public static final long TASK_NEIGHBOUR_DETAILS_TIMESTAMP = TASK_MAKE_TESTS_PASS_TIMESTAMP + DELAY;
    public static final Task TASK_NEIGHBOUR_DETAILS =
            new Task(TASK_NEIGHBOUR_DETAILS_PROJECT_ID,
                    TASK_NEIGHBOUR_DETAILS_NAME,
                    TASK_NEIGHBOUR_DETAILS_TIMESTAMP
            );

    //Task "create a meeting management app"
    public static final long TASK_CREATE_MEETING_APP_ID = 3;
    public static final int TASK_CREATE_MEETING_APP_PROJECT_ID = 3;
    public static final String TASK_CREATE_MEETING_APP_NAME = "Créer une app de gestion de réunions";
    public static final long TASK_CREATE_MEETING_APP_TIMESTAMP = TASK_NEIGHBOUR_DETAILS_TIMESTAMP + DELAY;
    public static final Task TASK_CREATE_MEETING_APP =
            new Task(TASK_CREATE_MEETING_APP_PROJECT_ID,
                    TASK_CREATE_MEETING_APP_NAME,
                    TASK_CREATE_MEETING_APP_TIMESTAMP
            );
}
