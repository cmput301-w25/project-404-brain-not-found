package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.util.Date;

public class MoodUnitTest {
//    @Test
//    public void testCreateMood() {
//        MoodSocialSituationEnum socialSituation = MoodSocialSituationEnum.ALONE;
//        String trigger = "";
//        String author = "";
//        Date date = null;
//        class TestHelper {
//            Mood createMoodUtil(MoodEmotionEnum emotion) {
//
//            }
//        }
//        assertEquals(MoodAnger.class, Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "", "", null, null).getClass());
//    }

    @Test
    public void testIllegalCreateMood() {
        assertThrows(IllegalArgumentException.class, () -> {
            Mood.createMood(MoodEmotionEnum.NONE, MoodSocialSituationEnum.ALONE, "", "", null, null);
        });
    }
}
