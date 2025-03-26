package com.example.cmput301_team_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.example.cmput301_team_project.ui.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodFormFragmentTest {

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
        onData(allOf(is(instanceOf(String.class)), is("Happiness"))).perform(click());

        // Select Social Situation
        onView(withId(R.id.form_situation)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Alone"))).perform(click());

        // Enter a trigger
        onView(withId(R.id.form_trigger)).perform(replaceText("Feeling great after a long walk!"));

        // Click the "Add" button in the dialog
        onView(withText("Add")).perform(click());

        // Verify the new mood appears in the mood list
        onView(withText("Feeling great after a long walk!"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEditMood() {
        // Add a mood entry
        onView(withId(R.id.add_mood_button)).perform(click());
        onView(withId(R.id.form_emotion)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Sadness"))).perform(click());
        onView(withId(R.id.form_trigger)).perform(replaceText("Had a tough day"));
        onView(withText("Add")).perform(click());

        // Verify the mood is added
        onView(withText("Had a tough day")).check(matches(isDisplayed()));

        // Edit the mood entry
        onView(withText("Had a tough day")).perform(click());
        onView(withId(R.id.form_emotion)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Happiness"))).perform(click());
        onView(withId(R.id.form_trigger)).perform(clearText(), replaceText("Feeling better now"));
        onView(withText("Save")).perform(click());

        // Verify the edited mood
        onView(withText("Feeling better now")).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteMood() {
        // Ensure the mood exists before attempting deletion
        onView(withText("Feeling better now")).check(matches(isDisplayed()));
        onView(withText("Feeling better now")).perform(click());
        onView(withText("Delete")).perform(click());

        // Verify mood is deleted
        onView(withText("Feeling better now")).check(doesNotExist());
    }
}
