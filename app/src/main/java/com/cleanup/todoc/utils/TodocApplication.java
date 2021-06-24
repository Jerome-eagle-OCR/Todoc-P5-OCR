package com.cleanup.todoc.utils;

import android.app.Application;

public class TodocApplication extends Application {

    private static Application application;

    public TodocApplication() {
        application = this;
    }

    public static Application getInstance() {
        return application;
    }
}