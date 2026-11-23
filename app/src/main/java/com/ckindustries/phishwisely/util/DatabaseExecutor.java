package com.ckindustries.phishwisely.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** All Room reads/writes happen off the Android UI thread. */
public final class DatabaseExecutor {
    public static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private DatabaseExecutor() {}
}
