package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginSignupTest {

    @Rule
    public ActivityScenarioRule<LoginSignupActivity> scenario = new
            ActivityScenarioRule<LoginSignupActivity>(LoginSignupActivity.class);

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moviesRef = db.collection("movies");
        Mood[] moods = {
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "fassdfa", "Urkel", null, null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.CROWD, "fassdfa", "Vance", null, null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.SEVERAL, "fassdfa", "Henrietta", null, null),
        };
        for (Mood mood : moods) {
            moviesRef.document().set(mood);
        }
    }



    @After
    public void tearDown() {
        String projectId = "cmput301-project-d122a";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

    }

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
