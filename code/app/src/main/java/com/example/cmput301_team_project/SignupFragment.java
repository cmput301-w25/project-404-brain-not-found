package com.example.cmput301_team_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private final UserDatabaseService userDatabaseService;

    public SignupFragment() {
        userDatabaseService = UserDatabaseService.getInstance();
    }
    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    public void signUp(View view){
        EditText usernameInput = view.findViewById(R.id.signup_username);
        EditText passwordInput = view.findViewById(R.id.signup_password);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        AppUser newUser = new AppUser(username, password);
        userDatabaseService.addUser(newUser);
        Toast.makeText(getContext(), "User signed up successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup, container, false);

        Button signupButton = view.findViewById(R.id.button_signin);
        signupButton.setOnClickListener(v ->{

            signUp(view);
        });
        return view;
    }
}