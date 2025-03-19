package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cmput301_team_project.R;

/**
 * Handles user authentication flow by managing the login and signup fragments.
 * This activity allows users to navigate between the login, signup, and selection screens.
 */
public class LoginSignupActivity extends BaseActivity implements LoginSignupFragment.onButtonClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        replaceFragment(new LoginSignupFragment());
    }

    /**
     * Handles button clicks to navigate between different authentication fragments.
     *
     * @param buttonId The ID of the button that was clicked.
     */
    public void onButtonClicked(int buttonId){
        if (buttonId == R.id.signup_button) {
            replaceFragment(new SignupFragment());
        } else if (buttonId == R.id.login_button){
            replaceFragment(new LoginFragment());
        } else if (buttonId == R.id.back_login || buttonId == R.id.back_signup){
            replaceFragment(new LoginSignupFragment());
        }
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main, fragment);
        fragmentTransaction.commit();
    }
}