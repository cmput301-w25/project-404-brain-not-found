package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.ui.LoginSignupActivity;
import com.example.cmput301_team_project.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FilterActivityTest extends BaseActivityTest {
    @Rule
    public ActivityScenarioRule<LoginSignupActivity> scenario = new
            ActivityScenarioRule<>(LoginSignupActivity.class);

    @BeforeClass
    public static void loginUser() {
        FirebaseAuthenticationService.setInstanceForTesting(FirebaseAuth.getInstance(), Runnable::run, "Henrietta");
    }

    @Before
    public void AddExtraMoodsAndViewHistory() throws InterruptedException {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moodsRef = db.collection("moods");
        // user has 4 moods each with own respective emotion, trigger, and Date
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -5);
        Date lastWeek = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -23);
        Date lastMonth = calendar.getTime();

        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -365);
        Date lastYear = calendar.getTime();

                Mood[] moods = {
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", true, "Henrietta", today, null, null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.CROWD, "test2", true, "Henrietta", lastWeek, null, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.SEVERAL, "test3",true, "Henrietta", lastMonth, null, null),
                Mood.createMood(MoodEmotionEnum.DISGUST, MoodSocialSituationEnum.SEVERAL, "test3",true, "Henrietta", lastYear, null, null),

        };

        for (Mood mood : moods) {
            moodsRef.document().set(mood);
        }

        Thread.sleep(2000);

        scenario.getScenario().onActivity(activity -> {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });

        // Give time for MainActivity to load before test interactions
        Thread.sleep(2000);

        onView(withId(R.id.mood_history_icon)).perform(click());
    }


    /* NOTE: moods are:
        Happiness | text: happy     | date: today     | trigger: fassdfa
        Sadness   | text: Sad       | date: lastWeek  | trigger: test3
        Shame     | text: Ashamed   | date: lastMonth | trigger: test2
        Anger     | text: Angry     | date: yesterday | trigger: test1
        Disgust   | text: Disgusted | date: lastYear  | trigger: test3
     */

    /**
     * Filters by emotion and checks to see all matches are showing
     */
    @Test
    public void filterByEmotionDoesShowsValidMoods() {
        Espresso.onIdle(); //idle to wait for the Moods to Load
        onView(withId(R.id.filter_button)).perform(click());

        // set the 'Shame' selection from the spinner
        onView(withId(R.id.filter_by_mood_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Shame")))
                .inRoot(RootMatchers.isPlatformPopup()) // Ensure it's a popup with focus
                .perform(click());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        // check that the Shame mood is appearing
        onView(withText("Ashamed")).check(matches(isDisplayed()));
        // make sure not filtered moods arent appearing
        onView(withText("Angry")).check(doesNotExist());
        onView(withText("Sad")).check(doesNotExist());
    }


    /** tests that the filters are being displayed after they select
     *  'RESET FILTERS' in the dialog
     */
    @Test
    public void testResetFilters() {
        Espresso.onIdle();
        // set the 'Shame' selection from the spinner (remove some posts from the view)
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.filter_by_mood_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Shame")))
                .inRoot(RootMatchers.isPlatformPopup()) // Ensure it's a popup with focus
                .perform(click());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        Espresso.onIdle();
        // reset the filters
        onView(withId(R.id.filter_button)).perform(click());
        onView(withText("RESET FILTERS")).inRoot(isDialog()).perform(click());

        Espresso.onIdle();
        // check that all moods are appearing
        onView(withText("Happy")).check(matches(isDisplayed()));
        onView(withText("Angry")).check(matches(isDisplayed()));
        onData(anything()).atPosition(0).perform(swipeUp());
        onView(withText("Sad")).check(matches(isDisplayed()));

    }

    @Test
    public void filterByDayShowsValidMoods() {
        Espresso.onIdle();
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.filter_by_day)).perform(click());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        Espresso.onIdle();

        // only these moods should be visible
        onView(withText("Happy")).check(matches(isDisplayed()));
        onView(withText("Angry")).check(matches(isDisplayed()));
        // these moods shouldn't be visible
        onView(withText("Sad")).check(doesNotExist());
        onView(withText("Ashamed")).check(doesNotExist());
        onView(withText("Disgusted")).check(doesNotExist());
    }


    @Test
    public void filterByWeekShowsValidMoods() {
        Espresso.onIdle();
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.filter_by_week)).perform(click());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        Espresso.onIdle();

        onView(withText("Happy")).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withText("Angry")).perform(scrollTo()).check(matches(isDisplayed()));
        onData(anything()).atPosition(0).perform(swipeUp());
        onView(withText("Sad")).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withText("Ashamed")).check(doesNotExist());
        onView(withText("Disgusted")).check(doesNotExist());
    }

    @Test
    public void filterByMonthShowsValidMoods() {
        Espresso.onIdle();
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.filter_by_month)).perform(click());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        Espresso.onIdle();

        onView(withText("Happy")).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withText("Angry")).perform(scrollTo()).check(matches(isDisplayed()));
        onData(anything()).atPosition(0).perform(swipeUp());
        onView(withText("Sad")).perform(scrollTo()).check(matches(isDisplayed()));
        onData(anything()).atPosition(1).perform(swipeUp());
        onView(withText("Ashamed")).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withText("Disgusted")).check(doesNotExist());
    }

    @Test
    public void filterByTextShowsValidMoods() {
        Espresso.onIdle();
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.filter_by_text)).perform(typeText("test1")).perform(closeSoftKeyboard());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        Espresso.onIdle();

        onView(withText("test1")).check(matches(isDisplayed()));

        onView(withText("test2")).check(doesNotExist());
        onView(withText("test3")).check(doesNotExist());
        onView(withText("fassdfa")).check(doesNotExist());
    }

    @Test
    public void cancelFiltersDoesntUpdateList() {
        Espresso.onIdle();
        onView(withId(R.id.filter_button)).perform(click());


        onView(withId(R.id.filter_by_mood_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Shame")))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
        onView(withText("SET FILTER")).inRoot(isDialog()).perform(click());

        onView(withId(R.id.filter_button)).perform(click());
        onView(withText("CANCEL")).inRoot(isDialog()).perform(click());

        // check that the Shame mood is still only one appearing
        onView(withText("Ashamed")).check(matches(isDisplayed()));
        // make sure not filtered moods arent appearing
        onView(withText("Angry")).check(doesNotExist());
        onView(withText("Sad")).check(doesNotExist());

    }
}
