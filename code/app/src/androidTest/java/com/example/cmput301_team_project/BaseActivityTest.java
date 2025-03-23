package com.example.cmput301_team_project;

import android.util.Log;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.Mood;
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
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");
        CollectionReference usersRef = db.collection("users");
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
            usersRef.document(user.getUsername()).set(user);
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
}