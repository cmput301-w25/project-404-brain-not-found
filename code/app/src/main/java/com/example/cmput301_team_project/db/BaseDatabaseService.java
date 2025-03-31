package com.example.cmput301_team_project.db;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

/**
 * the FirebaseAuthenticationServices singleton class includes methods for handling user
 * authentication via Firebase Authentication.
 *
 * Provides methods on registering a new user, logging in and signing out the existing
 * authenticated user, and getting the authenticated user's display name.
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
