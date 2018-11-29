package com.deploytools.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorTools {

    private ExecutorService mSingleThreadPool = Executors.newSingleThreadExecutor();
    private static ExecutorTools instance;

    public static ExecutorTools getInstance() {
        synchronized (ExecutorTools.class) {
            if (instance == null) {
                instance = new ExecutorTools();
            }
            return instance;
        }

    }

    public void execute(Runnable runnable) {
        mSingleThreadPool.execute(runnable);
    }
}
