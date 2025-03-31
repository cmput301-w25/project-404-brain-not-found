package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cmput301_team_project.R;

/**
 * A simple {@link Fragment} subclass for LoginSignup screen.
 * Use the {@link LoginSignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginSignupFragment extends Fragment {
    /**
     * Interface for handling button click events from this fragment.
     */
    private onButtonClickListener listener;

    public interface onButtonClickListener{
        void onButtonClicked(int buttonId);
    }
    public LoginSignupFragment() {
        // Required empty public constructor
    }
    /**
     * Makes a new instance of LoginSignupFragment.
     * @return the new fragment
     */

    public static LoginSignupFragment newInstance() {
        return new LoginSignupFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (onButtonClickListener) getActivity();
        }

    /**
     * Creates and returns the view with login and signup buttons.
     *
     * @param inflater Used to create the view from the layout file
     * @param container The parent view that this fragment's view will be attached to
     * @param savedInstanceState If the fragment was previously saved, this contains the saved data (may be null)
     * @return The created view with both login and signup buttons
     */
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