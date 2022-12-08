package com.p.note.db;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecuter {

    private static AppExecuter instance;
    private final Executor mainIO;
    private final Executor subIO;

    public AppExecuter(Executor mainIO, Executor subIO) {
        this.mainIO = mainIO;
        this.subIO = subIO;
    }

    public static AppExecuter getInstance(){
        if (instance == null) instance = new AppExecuter(new MainThreadHandler(), Executors.newSingleThreadExecutor());
        return instance;
    }

    public static class MainThreadHandler implements Executor{
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {

            mainHandler.post(command);
        }
    }

    public Executor getMainIO() {
        return mainIO;
    }

    public Executor getSubIO() {
        return subIO;
    }
}
