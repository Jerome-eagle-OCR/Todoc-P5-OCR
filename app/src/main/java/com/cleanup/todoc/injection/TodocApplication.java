package com.cleanup.todoc.injection;

import android.app.Application;

public class TodocApplication extends Application {

    public static TodocDependencyContainer sDependencyContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        sDependencyContainer = new TodocDependencyContainer(this);
    }
}