package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.ui.MainActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodFormFragmentTest extends BaseActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addMoodShouldAddValidMoodToMoodList() {
        Context context = ApplicationProvider.getApplicationContext();

        // Open the add mood dialog
        onView(withId(R.id.add_mood_button)).perform(click());

        // Select Emotion
        onView(withId(R.id.form_emotion)).perform(click());
        onData(allOf(is(instanceOf(MoodEmotionEnum.class)), is(MoodEmotionEnum.HAPPINESS)))
                .perform(click());

        // Select Social Situation
        onView(withId(R.id.form_situation)).perform(click());
        onData(allOf(is(instanceOf(MoodSocialSituationEnum.class)), is(MoodSocialSituationEnum.ALONE)))
                .perform(click());

        // Enter a trigger
        onView(withId(R.id.form_trigger)).perform(replaceText("Feeling great after a long walk!"));

        // Click the "Add" button in the dialog
        onView(withText("Add"))
                .inRoot(isDialog())
                .perform(click());

        // Verify the new mood appears in the mood list
        onView(withText("Feeling great after a long walk!")).check(matches(isDisplayed()));
    }
}
