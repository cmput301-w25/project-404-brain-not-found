package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.ui.LoginSignupActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI tests for all login and signup related actions
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginSignupActivityTest extends BaseActivityTest {

    @Rule
    public ActivityScenarioRule<LoginSignupActivity> scenario = new
            ActivityScenarioRule<>(LoginSignupActivity.class);

    /**
     * Checks that the proper error is displayed if the password text input field is left empty
     * when logging in.
     * @throws InterruptedException if the Thread.sleep() method is interrupted.
     */
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

    /**
     * Checks that the proper error is displayed if the username text input field is left empty
     * when logging in.
     * @throws InterruptedException if the Thread.sleep() method is interrupted.
     */
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

    /**
     * Checks that the proper error is displayed if the password input field is too short when
     * signing up a new user.
     * @throws InterruptedException if the Thread.sleep() method is interrupted.
     */
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

    /**
     * Checks that the proper error is displayed if the username text input field is left empty
     * when signing up a new user.
     * @throws InterruptedException if the Thread.sleep() method is interrupted.
     */
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

    /**
     * Checks that the proper error is displayed when a user signing in types in a
     * username that already exists
     * @throws InterruptedException if the Thread.sleep() method is interrupted.
     */
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

    /**
     * Checks that the proper error is displayed when a user logging in inputs the incorrect
     * password for their account.
     * @throws InterruptedException if the Thread.sleep() method is interrupted.
     */
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