package com.example.cmput301_team_project.db;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.UUID;

public class FirebaseAuthenticationService {
    private static FirebaseAuthenticationService instance = null;
    private final FirebaseAuth mAuth;

    private FirebaseAuthenticationService() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    public static FirebaseAuthenticationService getInstance() {
        if(instance == null) {
            instance = new FirebaseAuthenticationService();
        }
        return instance;
    }

    public Task<String> registerUser(String username, String password) {
        String email = UUID.randomUUID() + "@example.com";

        return mAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(task -> {
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
        if(mAuth.getCurrentUser() == null) {
            return null;
        }

        // we are storing username in display name because firebase auth does not allow username-based auth
        return mAuth.getCurrentUser().getDisplayName();
    }
}
