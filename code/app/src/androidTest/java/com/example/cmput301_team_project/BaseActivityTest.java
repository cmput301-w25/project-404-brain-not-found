package com.example.cmput301_team_project;

import android.util.Log;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.Mood;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


/**
 * Base UI test class. All other intent testing classes must extend this class.
 * Performs firebase emulator setup, seeding, and teardown.
 */
public class BaseActivityTest {
    @BeforeClass
    public static void setup() throws InterruptedException {
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int firestorePortNumber = 8080;
        int authPortNumber = 9099;
        if(!FirebaseFirestore.getInstance().getFirestoreSettings().getHost().equals(androidLocalhost)) {
            FirebaseFirestore.getInstance().useEmulator(androidLocalhost, firestorePortNumber);
            FirebaseAuth.getInstance().useEmulator(androidLocalhost, authPortNumber);
        }

        UserDatabaseService.setInstanceForTesting(FirebaseFirestore.getInstance(), Runnable::run);
        FirebaseAuthenticationService.setInstanceForTesting(FirebaseAuth.getInstance(), Runnable::run, null);
    }

    @Before
    public void seedDatabase() throws InterruptedException {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");
        Mood[] moods = {
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "fassdfa", true, "Urkel", null, null, null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.CROWD, "fassdfa", true, "Vance", null, null, null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.SEVERAL, "fassdfa",true, "Henrietta", null, null, null),
        };
        for (Mood mood : moods) {
            moodsRef.document().set(mood);
        }
        AppUser[] users = {
                new AppUser("Henrietta", "", "some_password")
        };

        for (AppUser user: users) {
            UserDatabaseService.getInstance().addUser(user);
            Thread.sleep(200);
            FirebaseAuth.getInstance().signOut();
        }

    }

    private static void clearData(String urlStr) {
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
        String projectId = "cmput301-project-d122a";

        clearData("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        clearData("http://10.0.2.2:9099/emulator/v1/projects/" + projectId + "/accounts");
    }
}