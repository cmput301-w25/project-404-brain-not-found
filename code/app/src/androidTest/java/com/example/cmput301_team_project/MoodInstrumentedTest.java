package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.ui.LoginSignupActivity;
import com.example.cmput301_team_project.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodInstrumentedTest extends BaseActivityTest {
    @Rule
    public ActivityScenarioRule<LoginSignupActivity> scenario = new
            ActivityScenarioRule<>(LoginSignupActivity.class);

    @BeforeClass
    public static void authSetup() {
        FirebaseAuthenticationService.getInstance().setUserForTesting("Henrietta");
    }

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(500);
        Log.i("TEST_LOG", "Login started");
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_username)).perform(typeText("Henrietta"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("some_password"), closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        Log.i("TEST_LOG", "Login started");

        Thread.sleep(15000);


        onView(withId(R.id.mood_history_icon)).perform(click());
    }
}
