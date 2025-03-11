package com.example.cmput301_team_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private final UserDatabaseService userDatabaseService;
    private final SessionManager sessionManager;
    private LoginSignupFragment.onButtonClickListener listener;

    public LoginFragment() {
        userDatabaseService = UserDatabaseService.getInstance();
        sessionManager = SessionManager.getInstance();
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    /**
     * Attempts to log in the user by verifying credentials.
     * If the username or password fields are empty, it displays an error.
     * If the credentials are valid, the user is authenticated and redirected to the main activity.
     *
     * @param view The view containing the login input fields.
     * @throws NoSuchAlgorithmException If an encryption algorithm is missing.
     * @throws InvalidKeySpecException If an invalid key specification is encountered.
     */
    public void login(View view) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EditText usernameInput = (EditText) view.findViewById(R.id.login_username);
        EditText passwordInput = (EditText) view.findViewById(R.id.login_password);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();


        if (username.isEmpty()){
            usernameInput.setError(getString(R.string.empty_username_error));
        }else if (password.isEmpty()){
            passwordInput.setError(getString(R.string.empty_password_error));
        }else{
            userDatabaseService.userExists(username).addOnCompleteListener(task -> {
                if (task.getResult()) {
                    usernameInput.setError(null);

                    userDatabaseService.validateCredentials(username, password).addOnCompleteListener(validationTask -> {
                        if(validationTask.isSuccessful() && validationTask.getResult()) {
                            passwordInput.setError(null);
                            sessionManager.setCurrentUser(username);
                            Intent myIntent = new Intent(getContext(), MainActivity.class);
                            getContext().startActivity(myIntent);
                        }
                        else {
                            passwordInput.setError("Incorrect password");
                        }
                    });
                }
                else {
                    usernameInput.setError("Username does not exist");
                }
            });
        }
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginButton = view.findViewById(R.id.button_login);
        loginButton.setOnClickListener(v->{
            try {
                login(view);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });

        ImageButton backButton = view.findViewById(R.id.back_login);
        backButton.setOnClickListener(v ->{
            if (listener != null){
                listener.onButtonClicked(R.id.back_login);
            }
        });
        return view;
    }
}