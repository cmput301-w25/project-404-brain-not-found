package com.example.cmput301_team_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;
import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private final UserDatabaseService userDatabaseService;
    private final SessionManager sessionManager;

    public SignupFragment() {
        userDatabaseService = UserDatabaseService.getInstance();
        sessionManager = SessionManager.getInstance();
    }
    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    public void signUp(View view){
        EditText usernameInput = view.findViewById(R.id.signup_username);
        EditText passwordInput = view.findViewById(R.id.signup_password);
        String username = usernameInput.getText().toString();
        //eventually this password should be hashed
        String password = passwordInput.getText().toString();

        TextInputLayout usernameLayout = view.findViewById(R.id.signup_username_layout);
        TextInputLayout passwordLayout = view.findViewById(R.id.signup_password_layout);


        if (username.isEmpty()){
            usernameLayout.setError("Cannot have an empty username");
            return;
        }
        if (password.isEmpty()){
            passwordLayout.setError("Cannot have an empty password");
            return;
        }
        userDatabaseService.userExists(username).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (!task.getResult().booleanValue()){
                    AppUser newUser = new AppUser(username, password);
                    userDatabaseService.addUser(newUser);
                    sessionManager.setCurrentUser(username);
                    Intent myIntent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(myIntent);
                }
            }
        });
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