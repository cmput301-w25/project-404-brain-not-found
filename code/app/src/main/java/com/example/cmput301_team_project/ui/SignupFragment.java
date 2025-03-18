package com.example.cmput301_team_project.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import android.util.Base64;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.SessionManager;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.model.AppUser;

/**
 * A simple {@link Fragment} subclass for signup.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private final UserDatabaseService userDatabaseService;
    private final SessionManager sessionManager;
    private LoginSignupFragment.onButtonClickListener listener;

    public SignupFragment() {
        userDatabaseService = UserDatabaseService.getInstance();
        sessionManager = SessionManager.getInstance();
    }
    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    /**
     * Handles the signup process, including input validation, password hashing,
     * and storing the new user in Firestore.
     *
     * If the username already exists, the user is notified. If successful, the user is
     * logged in and redirected to the main activity.
     *
     * @param view The parent view containing input fields.
     * @throws NoSuchAlgorithmException If the password hashing algorithm is not found.
     * @throws InvalidKeySpecException If the password hashing fails due to an invalid key specification.
     */
    public void signUp(View view) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EditText usernameInput = view.findViewById(R.id.signup_username);
        EditText nameInput = view.findViewById(R.id.signup_name);
        EditText passwordInput = view.findViewById(R.id.signup_password);
        String username = usernameInput.getText().toString();
        String name = nameInput.getText().toString();
        String password = passwordInput.getText().toString();


        if (username.isEmpty()){
            usernameInput.setError(getString(R.string.empty_username_error));
            return;
        }
        if (password.isEmpty()){
            passwordInput.setError(getString(R.string.empty_password_error));
            return;
        }

        byte[] salt = userDatabaseService.generateSalt();
        String hashed = userDatabaseService.hashPassword(password, salt);

        userDatabaseService.userExists(username).addOnCompleteListener(task -> {
            if (!task.getResult()){
                usernameInput.setError(null);
                AppUser newUser = new AppUser(username, name, hashed, Base64.encodeToString(salt, Base64.NO_WRAP));
                userDatabaseService.addUser(newUser);
                sessionManager.setCurrentUser(username);
                Intent myIntent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(myIntent);
            }
            else {
                usernameInput.setError(getString(R.string.username_exists_error));
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (LoginSignupFragment.onButtonClickListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup, container, false);

        Button signupButton = view.findViewById(R.id.button_signin);
        signupButton.setOnClickListener(v ->{
            try {
                signUp(view);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });

        ImageButton backButton = view.findViewById(R.id.back_signup);
        backButton.setOnClickListener(v ->{
            if (listener != null){
                listener.onButtonClicked(R.id.back_signup);
            }
        });
        return view;
    }
}