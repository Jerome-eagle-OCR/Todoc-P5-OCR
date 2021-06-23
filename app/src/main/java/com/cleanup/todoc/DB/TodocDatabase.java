package com.cleanup.todoc.DB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.model.DAOs.ProjectDao;
import com.cleanup.todoc.model.DAOs.TaskDao;
import com.cleanup.todoc.model.entities.Project;
import com.cleanup.todoc.model.entities.Task;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

@Database(entities = {Project.class, Task.class}, version = 1)
public abstract class TodocDatabase extends RoomDatabase {

    private static TodocDatabase instance;

    public abstract ProjectDao projectDao();

    public abstract TaskDao taskDao();


    public static synchronized TodocDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TodocDatabase.class, "todoc_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateProjectsInDb();
        }
    };

    private static void populateProjectsInDb() {
        Executors.newFixedThreadPool(2).execute(() -> {
            instance.projectDao().insert(new Project("Projet Tartampion", 0xFFEADAD1));
            instance.projectDao().insert(new Project("Projet Lucidia", 0xFFB4CDBA));
            instance.projectDao().insert(new Project("Projet Circus", 0xFFA3CED2));
        });
    }
}