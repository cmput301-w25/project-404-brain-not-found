package com.example.cmput301_team_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public void login(View view){
        EditText usernameInput = (EditText) view.findViewById(R.id.login_username);
        EditText passwordInput = (EditText) view.findViewById(R.id.login_password);
        String username = usernameInput.getText().toString();
        String password = usernameInput.getText().toString();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginButton = view.findViewById(R.id.button_login);
        loginButton.setOnClickListener(v->{
            login(view);
        });
        return view;
    }
}