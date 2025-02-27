package com.example.cmput301_team_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    public void login(View view) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EditText usernameInput = (EditText) view.findViewById(R.id.login_username);
        EditText passwordInput = (EditText) view.findViewById(R.id.login_password);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        TextInputLayout usernameLayout = view.findViewById(R.id.login_user_layout);
        TextInputLayout passwordLayout = view.findViewById(R.id.login_password_layout);


        if (username.isEmpty()){
            usernameLayout.setError("Username cannot be empty");
        }else if (password.isEmpty()){
            passwordLayout.setError("Password cannot be empty");
        }else{
            userDatabaseService.userExists(username).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.getResult().booleanValue()){
                        usernameLayout.setError("");
                        userDatabaseService.getSalt(username).addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                byte[] salt = Base64.decode(task.getResult(), Base64.DEFAULT);
                                try {
                                    String hashed = userDatabaseService.hashPassword(password, salt);
                                    userDatabaseService.getPassword(username).addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (hashed.equals(task.getResult())){
                                                sessionManager.setCurrentUser(username);
                                                Intent myIntent = new Intent(getContext(), MainActivity.class);
                                                getContext().startActivity(myIntent);
                                            } else{
                                                passwordLayout.setError("Incorrect password");
                                            }
                                        }
                                    });
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                } catch (InvalidKeySpecException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }else{
                        usernameLayout.setError("Username does not exist");
                    }
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