package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.ui.LoginSignupActivity;
import com.example.cmput301_team_project.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class MoodInstrumentedTest extends BaseActivityTest {
    private ActivityScenario<MainActivity> scenario;

    @BeforeClass
    public static void setTestUser() {
        FirebaseAuthenticationService.getInstance().setUserForTesting("Henrietta");
    }

    @Before
    public void login() throws InterruptedException {
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void release() {
        scenario.close();
    }

    @Test
    public void aaa_my_testcase() throws InterruptedException {
        Thread.sleep(500);

        onView(withId(R.id.mood_history_icon)).perform(click());
    }
}
