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

    private final Executor mDiskIO = Executors.newSingleThreadExecutor(); //to do it in background

    private final Executor mMainThreadExecutor = new MainThreadExecutor(); //send info if you are on background thread to the main Thread

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

