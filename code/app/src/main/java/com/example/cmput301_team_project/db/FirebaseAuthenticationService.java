package com.example.cmput301_team_project.db;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.UUID;
import java.util.concurrent.Executor;

public class FirebaseAuthenticationService {
    private static FirebaseAuthenticationService instance = null;
    private final FirebaseAuth mAuth;
    private final Executor taskExecutor;
    // sometimes we need to test something from MainActivity, without signing in, so the purpose of this attribute is to have the username of that user
    private final String userForTesting;

    private FirebaseAuthenticationService() {
        mAuth = FirebaseAuth.getInstance();
        taskExecutor = TaskExecutors.MAIN_THREAD;
        userForTesting = null;
    }

    private FirebaseAuthenticationService(FirebaseAuth mAuth, Executor executor, String userForTesting) {
        this.mAuth = mAuth;
        this.taskExecutor = executor;
        this.userForTesting = userForTesting;
    }

    public static FirebaseAuthenticationService getInstance() {
        if(instance == null) {
            instance = new FirebaseAuthenticationService();
        }
        return instance;
    }

    public static void setInstanceForTesting(FirebaseAuth mAuth, Executor executor, String userForTesting) {
        instance = new FirebaseAuthenticationService(mAuth, executor, userForTesting);
    }

    public Task<String> registerUser(String username, String password) {
        String email = UUID.randomUUID() + "@example.com";

        return mAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(taskExecutor, task -> {
                    if(task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();

                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if(firebaseUser != null) {
                            firebaseUser.updateProfile(profileUpdates);
                        }

                        return email;
                    }
                    Tasks.forException(task.getException());
                    return null;
                });
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void logoutUser() {
        mAuth.signOut();
    }

    public String getCurrentUser() {
        if(userForTesting != null) {
            return userForTesting;
        }

        if(mAuth.getCurrentUser() == null) {
            return null;
        }

        // we are storing username in display name because firebase auth does not allow username-based auth
        return mAuth.getCurrentUser().getDisplayName();
    }
}
