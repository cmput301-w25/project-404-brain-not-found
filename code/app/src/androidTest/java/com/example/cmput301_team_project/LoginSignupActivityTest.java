package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.ui.LoginSignupActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * UI tests for all login and signup related actions
 */

public class LoginSignupActivityTest extends BaseActivityTest {

    private ActivityScenario<LoginSignupActivity> scenario;

    @Before
    public void startTest() throws InterruptedException {
        Thread.sleep(1000);
        FirebaseAuthenticationService.getInstance().setUserForTesting(null);
        FirebaseAuthenticationService.getInstance().logoutUser();
        Thread.sleep(1000);
        scenario = ActivityScenario.launch(LoginSignupActivity.class);
    }

    @After
    public void stopTest() {
        scenario.close();
    }

    @Test
    public void logInPasswordErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_username))
                .perform(ViewActions.typeText("hello"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(R.id.login_password)).check(matches(hasErrorText("Password cannot be empty")));

    }

    @Test
    public void logInUserErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_password))
                .perform(ViewActions.typeText("hello"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(R.id.login_username)).check(matches(hasErrorText("Username cannot be empty")));
    }

    @Test
    public void SignUpPasswordErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_username))
                .perform(ViewActions.typeText("hello"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_signin)).perform(click());
        onView(withId(R.id.signup_password)).check(matches(hasErrorText("Password should be at least 6 characters")));

    }

    @Test
    public void signUpUserEmptyErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_password))
                .perform(ViewActions.typeText("hello"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_signin)).perform(click());
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("Username cannot be empty")));
    }


    @Test
    public void signUpUserErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_username))
                .perform(ViewActions.typeText("Henrietta"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.signup_password))
                .perform(ViewActions.typeText("henrietta"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_signin)).perform(click());
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("Username already taken")));
    }

    @Test
    public void incorrectPasswordErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_username))
                .perform(ViewActions.typeText("Henrietta"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.login_password))
                .perform(ViewActions.typeText("Henrietta"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(R.id.login_password)).check(matches(hasErrorText("Incorrect password")));
    }
}