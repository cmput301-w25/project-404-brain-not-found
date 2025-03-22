package com.example.cmput301_team_project.db;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

/**
 * Base class of DatabaseService
 * Provides a shared FirebaseFirestore instance for all database services
 */
public abstract class BaseDatabaseService {
    protected FirebaseFirestore db;
    protected Executor taskExecutor;

    protected BaseDatabaseService() {
        db = FirebaseFirestore.getInstance();

        taskExecutor = TaskExecutors.MAIN_THREAD;
    }

    protected BaseDatabaseService(FirebaseFirestore db, Executor executor) {
        this.db = db;
        this.taskExecutor = executor;
    }
}
