package com.example.cmput301_team_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginSignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginSignupFragment extends Fragment {
    private onButtonClickListener listener;

    public interface onButtonClickListener{
        void onButtonClicked(int buttonId);
    }
    public LoginSignupFragment() {
        // Required empty public constructor
    }

    public static LoginSignupFragment newInstance() {
        return new LoginSignupFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (onButtonClickListener) getActivity();
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_signup, container, false);
        Button signupButton = view.findViewById(R.id.signup_button);
        Button loginButton = view.findViewById(R.id.login_button);

        signupButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onButtonClicked(R.id.signup_button);
            }
        });

        loginButton.setOnClickListener(v ->{
            if (listener != null){
                listener.onButtonClicked(R.id.login_button);
            }
        });



        return view;
    }
}