package com.example.cmput301_team_project;

import android.util.Log;
import android.view.View;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.Mood;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Base UI test class. All other intent testing classes must extend this class.
 * Performs firebase emulator setup, seeding, and teardown.
 */
public class BaseActivityTest {

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int firestorePortNumber = 8080;
        int authPortNumber = 9099;

        try {
            FirebaseFirestore.getInstance().useEmulator(androidLocalhost, firestorePortNumber);
            FirebaseAuth.getInstance().useEmulator(androidLocalhost, authPortNumber);
        } catch (IllegalStateException e) {
            // emulators have already been initialized
        }
    }

    @Before
    public void seedDatabase() {
        MoodDatabaseService moodDatabaseService = MoodDatabaseService.getInstance();
        UserDatabaseService userDatabaseService = UserDatabaseService.getInstance();

        Mood[] moods = {
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "fassdfa", true, "Urkel", null, null, null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.CROWD, "fassdfa", true, "Vance", null, null, null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.SEVERAL, "initial trigger",true, "Henrietta", null, null, null),
        };
        for (Mood mood : moods) {
            moodDatabaseService.addMood(mood);
        }

        AppUser[] users = {
                new AppUser("Henrietta", "", "some_password")
        };

        for (AppUser user: users) {
            userDatabaseService.addUser(user).addOnSuccessListener(v -> FirebaseAuthenticationService.getInstance().logoutUser());
        }
    }

    private void clearData(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
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

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
        String projectId = "cmput301-project-d122a";

        clearData("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        clearData("http://10.0.2.2:9099/emulator/v1/projects/" + projectId + "/accounts");
    }

    protected static Matcher<View> getElementFromMatchAtPosition(final Matcher<View> matcher, final int position) {
        return new BaseMatcher<>() {
            int counter = 0;

            @Override
            public boolean matches(final Object item) {
                if (matcher.matches(item)) {
                    if (counter == position) {
                        counter++;
                        return true;
                    }
                    counter++;
                }
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Element at hierarchy position " + position);
            }
        };
    }
}