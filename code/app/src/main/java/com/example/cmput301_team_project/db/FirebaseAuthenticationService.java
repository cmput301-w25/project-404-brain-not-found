package com.example.cmput301_team_project.db;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.UUID;

/**
 * the FirebaseAuthenticationServices singleton class includes methods for handling user
 * authentication via Firebase Authentication.
 *
 * Provides methods on registering a new user, logging in and signing out the existing
 * authenticated user, and getting the authenticated user's display name.
 */
public class FirebaseAuthenticationService {
    private static FirebaseAuthenticationService instance = null;
    private final FirebaseAuth mAuth;

    private FirebaseAuthenticationService() {
        mAuth = FirebaseAuth.getInstance();
    }

    private FirebaseAuthenticationService(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    /**
     * Gets/creates an instance of the FirebaseAuthenticationService
     *
     * @return The (possibly newly created) instance of the FirebaseAuthenticationService
     */
    public static FirebaseAuthenticationService getInstance() {
        if(instance == null) {
            instance = new FirebaseAuthenticationService();
        }
        return instance;
    }

    /**
     *  Sets up an instance of the FirebaseAuthenticationService for testing purposes.
     *
     * @param mAuth the FirebaseAuth to be added to the test Authenticators instance.
     */
    public static void setInstanceForTesting(FirebaseAuth mAuth) {
        instance = new FirebaseAuthenticationService(mAuth);
    }

    /**
     * Registers a new user with Firebase Authentication with a randomly generated email, with the
     * username set as the display name.
     *
     * @param username The username of the newly registered user.
     * @param password The password of the newly registered user.
     * @return A Task containing the user's generated email as a String if successful. If the task
     * was not successful, then return a null String and an exception.
     */
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

    /**
     * Logs in the user based on their email and password.
     *
     * @param email The email of the user logging in.
     * @param password the password to authenticate the user logging in.
     * @return A task containing the FirebaseAuth of the user. if authentication fails then an
     * exception is thrown instead.
     */
    public Task<AuthResult> loginUser(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Logs out the currently authenticated user from the Firebase Authentication.
     */
    public void logoutUser() {
        mAuth.signOut();
    }

    /**
     * Gets the authenticated user's display name from Firebase Authentication.
     *
     * @return The display name of the Authenticated user as a String.
     */
    public String getCurrentUser() {
        if(mAuth.getCurrentUser() == null) {
            return null;
        }

        // we are storing username in display name because firebase auth does not allow username-based auth
        return mAuth.getCurrentUser().getDisplayName();
    }
}
