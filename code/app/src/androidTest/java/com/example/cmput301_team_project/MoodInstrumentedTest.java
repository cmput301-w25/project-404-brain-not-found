package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.ui.MainActivity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodInstrumentedTest extends BaseActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setTestUser() {
        FirebaseAuthenticationService.getInstance().setUserForTesting("Henrietta");
    }

    @Before
    public void waitLoading() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void addMoodTest() {
        Espresso.onIdle();
        onView(withId(R.id.mood_history_icon)).perform(click());
        onView(withId(R.id.add_mood_button)).perform(click());

        onView(withId(R.id.form_emotion)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Surprise")))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withText("ADD")).perform(click());

        onView(withText("Surprised")).check(matches(isDisplayed()));
    }

    @Test
    public void addMoodSocialSituationTest() {
        Espresso.onIdle();
        onView(withId(R.id.mood_history_icon)).perform(click());
        onView(withId(R.id.add_mood_button)).perform(click());

        onView(withId(R.id.form_emotion)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Surprise")))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withId(R.id.form_situation)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("With one other person")))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withText("ADD")).perform(click());

        onView(withText("Surprised")).check(matches(isDisplayed()));
        onView(withText("with one other person")).check(matches(isDisplayed()));
    }

    @Test
    public void addMoodTriggerTest() {
        Espresso.onIdle();
        onView(withId(R.id.mood_history_icon)).perform(click());
        onView(withId(R.id.add_mood_button)).perform(click());

        onView(withId(R.id.form_emotion)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Surprise")))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withId(R.id.form_situation)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("With one other person")))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withId(R.id.form_trigger)).perform(typeText("trigger test"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());

        onView(allOf(getElementFromMatchAtPosition(allOf(withId(R.id.drop_down)), 0), isDisplayed())).perform(click());

        onView(withText("Surprised")).check(matches(isDisplayed()));
        onView(withText("with one other person")).check(matches(isDisplayed()));
        onView(withText("trigger test")).check(matches(isDisplayed()));
    }

    @Test
    public void addMoodNoEmotionErrorTest() {
        Espresso.onIdle();
        onView(withId(R.id.mood_history_icon)).perform(click());
        onView(withId(R.id.add_mood_button)).perform(click());

        onView(withText("ADD")).perform(click());

        onView(withText("Emotional State is required")).check(matches(isDisplayed()));
    }
}
