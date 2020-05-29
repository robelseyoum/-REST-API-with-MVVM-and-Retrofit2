package com.robelseyoum3.foodrecipes;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static AppExecutors instance;

    public static AppExecutors getInstance() {
        if(instance == null){
            instance = new AppExecutors();
        }
        return  instance;
    }

    //to do it in background
    //responsible for all db operation on cache mainly, responsible all CRUD tasks from cache
    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

    //send info if you are on background thread to the main Thread
    private final Executor mMainThreadExecutor = new MainThreadExecutor();

    public Executor getDiskIO() {
        return mDiskIO;
    }

    public Executor mainThread() {
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }

    }

}

