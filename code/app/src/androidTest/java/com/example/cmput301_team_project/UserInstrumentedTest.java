package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import android.view.KeyEvent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.ui.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserInstrumentedTest extends BaseActivityTest {
    private ActivityScenario<MainActivity> scenario;
    private UserDatabaseService userDatabaseService;

    @Before
    @Override
    public void seedDatabase() {
        MoodDatabaseService moodDatabaseService = MoodDatabaseService.getInstance();
        UserDatabaseService userDatabaseService = UserDatabaseService.getInstance();
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

        Mood[] moods = {
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "fassdfa", true, "Urkel", null, null, null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.CROWD, "fassdfa", true, "Vance", null, null, null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.SEVERAL, "initial trigger",true, "Henrietta", null, null, null),
        };
        for (Mood mood : moods) {
            moodDatabaseService.addMood(mood);
        }

        AppUser[] users = {
                new AppUser("Henrietta", "Test Display", "some_password"),
                new AppUser("Vance", "Vancey", "vance123"),
                new AppUser("Urkel", "Lekru", "urkel321"),
                new AppUser("thor123", "Chris Hemsworth", "qwerty")
        };

        for (AppUser user: users) {
            Map<String, String> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("usernameLower", user.getUsername().toLowerCase());
            data.put("name", user.getName());
            data.put("nameLower", user.getName().toLowerCase());
            usersRef.document(user.getUsername()).set(data);
        }
    }

    @Before
    public void startTest() throws InterruptedException {
        userDatabaseService = UserDatabaseService.getInstance();

        userDatabaseService.requestFollow("Vance", "Henrietta");
        userDatabaseService.requestFollow("Henrietta", "Urkel");
        userDatabaseService.acceptRequest("Henrietta", "Urkel");

        Thread.sleep(3000);
        FirebaseAuthenticationService.getInstance().setUserForTesting("Henrietta");
        Thread.sleep(1000);
        scenario = ActivityScenario.launch(MainActivity.class);
        Thread.sleep(1000);
    }

    @After
    public void stopTest() {
        scenario.close();
    }

    @Test
    public void profileTest() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("@Henrietta")).check(matches(isDisplayed()));
        onView(withText("Test Display")).check(matches(isDisplayed()));
    }

    @Test
    public void testRequests() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.request_text)).check(matches(withText(containsString("Vance"))));
    }

    @Test
    public void testAcceptRequest() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("Accept")).perform(click());
        Thread.sleep(500);
        onView(withText("Vance")).check(doesNotExist());
        onView(withText("Followers")).perform(click());
        Thread.sleep(1000);

        onView(withText("Vance")).check(matches(isDisplayed()));
    }

    @Test
    public void testRemoveFollower() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("Accept")).perform(click());
        Thread.sleep(500);
        onView(withText("Vance")).check(doesNotExist());
        onView(withText("Followers")).perform(click());
        Thread.sleep(1000);

        onView(withText("Remove")).perform(click());
        Thread.sleep(500);
        onView(withText("Vance")).check(doesNotExist());
    }

    @Test
    public void testDeclineRequest() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("Decline")).perform(click());
        Thread.sleep(500);
        onView(withText("Vance")).check(doesNotExist());
        onView(withText("Followers")).perform(click());
        Thread.sleep(1000);

        onView(withText("Vance")).check(doesNotExist());
    }

    @Test
    public void testFollowing() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(allOf(getElementFromMatchAtPosition(allOf(withText("Following")), 0))).perform(click());
        Thread.sleep(1000);
        onView(allOf(getElementFromMatchAtPosition(allOf(withText("Urkel")), 1))).check(matches(isDisplayed()));
    }

    @Test
    public void testUnfollow() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(allOf(getElementFromMatchAtPosition(allOf(withText("Following")), 0))).perform(click());
        Thread.sleep(1000);
        onView(withText("Unfollow")).perform(click());
        Thread.sleep(500);
        onView(allOf(getElementFromMatchAtPosition(allOf(withText("Urkel")), 1))).check(doesNotExist());
    }

    @Test
    public void testSearch() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("People")).perform(click());
        Thread.sleep(1000);
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(typeText("ThOr"));
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(closeSoftKeyboard());
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(500);
        onView(withText("thor123")).check(matches(isDisplayed()));
    }

    @Test
    public void testOthersProfile() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("People")).perform(click());
        Thread.sleep(1000);
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(typeText("ThOr"));
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(closeSoftKeyboard());
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(500);
        onView(withText("thor123")).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.publicUsername)).check(matches(withText("@thor123")));
        onView(withId(R.id.publicName)).check(matches(withText("Chris Hemsworth")));
    }

    @Test
    public void testFollow() throws InterruptedException {
        onView(withId(R.id.user_icon)).perform(click());
        Thread.sleep(2000);

        onView(withText("People")).perform(click());
        Thread.sleep(1000);
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(typeText("ThOr"));
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(closeSoftKeyboard());
        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.user_search)), 0))).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(500);
        onView(withText("Follow")).perform(click());
        Thread.sleep(500);
        onView(withText("Requested")).check(matches(isDisplayed()));
    }
}
