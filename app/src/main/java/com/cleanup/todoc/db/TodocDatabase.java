package com.cleanup.todoc.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.model.dao.ProjectDao;
import com.cleanup.todoc.model.dao.TaskDao;
import com.cleanup.todoc.model.entity.Project;
import com.cleanup.todoc.model.entity.Task;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

@Database(entities = {Project.class, Task.class}, exportSchema = false, version = 1)
public abstract class TodocDatabase extends RoomDatabase {

    public static final String DB_NAME = "todoc_database";
    private static TodocDatabase instance;

    public abstract ProjectDao projectDao();

    public abstract TaskDao taskDao();


    public static synchronized TodocDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TodocDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateProjectsInDb();
        }
    };

    private static void populateProjectsInDb() {
        Executors.newFixedThreadPool(3).execute(() -> {
            instance.projectDao().insert(new Project("Projet Tartampion", 0xFFEADAD1));
            instance.projectDao().insert(new Project("Projet Lucidia", 0xFFB4CDBA));
            instance.projectDao().insert(new Project("Projet Circus", 0xFFA3CED2));
        });
    }
}