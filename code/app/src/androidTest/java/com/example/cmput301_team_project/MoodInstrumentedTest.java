package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.ui.LoginSignupActivity;
import com.example.cmput301_team_project.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodInstrumentedTest extends BaseActivityTest {
    @Rule
    public ActivityScenarioRule<LoginSignupActivity> scenario = new
            ActivityScenarioRule<>(LoginSignupActivity.class);

    @Before
    public void login() throws InterruptedException {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(500);
        System.out.println("Login started");
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_username)).perform(typeText("Henrietta"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("some_password"), closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        System.out.println("Login pressed");

        Thread.sleep(500);
        intended(hasComponent(MainActivity.class.getName()));
        System.out.println("Intended passed");

        onView(withId(R.id.mood_history_icon)).perform(click());
    }
}
