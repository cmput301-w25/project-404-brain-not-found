package com.example.cmput301_team_project.db;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

import java.util.concurrent.Executor;

/**
 * Base class of DatabaseService
 * Provides a shared FirebaseFirestore instance for all database services
 */
public abstract class BaseDatabaseService {
    protected FirebaseFirestore db;
    protected Executor taskExecutor;

    protected BaseDatabaseService() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();

        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        taskExecutor = TaskExecutors.MAIN_THREAD;
    }

    protected BaseDatabaseService(FirebaseFirestore db, Executor executor) {
        this.db = db;
        this.taskExecutor = executor;
    }
}
