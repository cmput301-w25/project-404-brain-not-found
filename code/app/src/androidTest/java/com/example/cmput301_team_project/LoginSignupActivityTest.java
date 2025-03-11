package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginSignupActivityTest extends BaseActivityTest {

    @Rule
    public ActivityScenarioRule<LoginSignupActivity> scenario = new
            ActivityScenarioRule<>(LoginSignupActivity.class);

    @Test
    public void logInPasswordErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_username)).perform(ViewActions.typeText("hello"));
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(R.id.login_password)).check(matches(hasErrorText("Password cannot be empty")));

    }

    @Test
    public void logInUserErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_password)).perform(ViewActions.typeText("hello"));
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(R.id.login_username)).check(matches(hasErrorText("Username cannot be empty")));
    }

    @Test
    public void SignUpPasswordErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_username)).perform(ViewActions.typeText("hello"));
        onView(withId(R.id.button_signin)).perform(click());
        onView(withId(R.id.signup_password)).check(matches(hasErrorText("Password cannot be empty")));

    }

    @Test
    public void signUpUserEmptyErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_password)).perform(ViewActions.typeText("hello"));
        onView(withId(R.id.button_signin)).perform(click());
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("Username cannot be empty")));
    }


    @Test
    public void signUpUserErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_username)).perform(ViewActions.typeText("Henrietta"));
        onView(withId(R.id.signup_password)).perform(ViewActions.typeText("henrietta"));
        onView(withId(R.id.button_signin)).perform(click());
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("Username already taken")));
    }

    @Test
    public void incorrectPasswordErrorShown() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_username)).perform(ViewActions.typeText("Henrietta"));
        onView(withId(R.id.login_password)).perform(ViewActions.typeText("Henrietta"));
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(R.id.login_password)).check(matches(hasErrorText("Incorrect password")));
    }


}