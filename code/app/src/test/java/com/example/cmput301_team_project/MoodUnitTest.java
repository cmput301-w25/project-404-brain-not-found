package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.model.MoodAnger;
import com.example.cmput301_team_project.model.MoodConfusion;
import com.example.cmput301_team_project.model.MoodDisgust;
import com.example.cmput301_team_project.model.MoodFear;
import com.example.cmput301_team_project.model.MoodHappiness;
import com.example.cmput301_team_project.model.MoodSadness;
import com.example.cmput301_team_project.model.MoodShame;
import com.example.cmput301_team_project.model.MoodSurprise;

import org.junit.Test;


/**
 * Class for testing {@link Mood} class and its subclasses.
 *
 * Ensures Moods are being created as intended.
 */
public class MoodUnitTest {
    /**
     * Tests that each Mood is properly being created based on its respective emotion.
     *
     * Uses a testHelper class to help with creating new Moods to verify.
     */
    @Test
    public void testCreateMood() {
        class TestHelper {
            Mood createMoodUtil(MoodEmotionEnum emotion) {
                return Mood.createMood(emotion, MoodSocialSituationEnum.ALONE, "", true,"", null, null, null);
            }
        }
        TestHelper testHelper = new TestHelper();
        assertEquals(MoodAnger.class,testHelper.createMoodUtil(MoodEmotionEnum.ANGER).getClass());
        assertEquals(MoodConfusion.class,testHelper.createMoodUtil(MoodEmotionEnum.CONFUSION).getClass());
        assertEquals(MoodDisgust.class,testHelper.createMoodUtil(MoodEmotionEnum.DISGUST).getClass());
        assertEquals(MoodFear.class,testHelper.createMoodUtil(MoodEmotionEnum.FEAR).getClass());
        assertEquals(MoodHappiness.class,testHelper.createMoodUtil(MoodEmotionEnum.HAPPINESS).getClass());
        assertEquals(MoodSadness.class,testHelper.createMoodUtil(MoodEmotionEnum.SADNESS).getClass());
        assertEquals(MoodShame.class,testHelper.createMoodUtil(MoodEmotionEnum.SHAME).getClass());
        assertEquals(MoodSurprise.class,testHelper.createMoodUtil(MoodEmotionEnum.SURPRISE).getClass());
    }

    /**
     *  Verifies that the {@link IllegalArgumentException} is being thrown if a Mood is being created
     *  incorrectly.
     */
    @Test
    public void testIllegalCreateMood() {
        assertThrows(IllegalArgumentException.class, () -> {
            Mood.createMood(MoodEmotionEnum.NONE, MoodSocialSituationEnum.ALONE, "", true,"", null, null, null);
        });
    }
}
