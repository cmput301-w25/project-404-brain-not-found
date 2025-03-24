package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.ui.MoodHistoryFragment;
import com.example.cmput301_team_project.ui.MoodListAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * class for testing {@link com.example.cmput301_team_project.ui.MoodHistoryFragment} and
 * the filtering functions
 */
public class MoodFilterUnitTest {
    private MoodHistoryFragment moodHistoryFragment;

    @Mock
    private ArrayList<Mood> mockMoodList;
    @Mock
    private MoodDatabaseService mockDatabaseService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        moodHistoryFragment = new MoodHistoryFragment();
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date twoDaysAgo = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        Date weekAgo = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -365);
        Date yearAgo = calendar.getTime();


        when(mockDatabaseService.getMoodList(any())).thenReturn(
            Task.forResult(new ArrayList<>(Arrays.asList(
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", true, "hi", today, null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.ALONE, "test2", true, "hi", twoDaysAgo, null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test3", true, "hi", weekAgo, null),
                Mood.createMood(MoodEmotionEnum.CONFUSION, MoodSocialSituationEnum.ALONE, "test4", true, "hi", yearAgo, null)
            ))));

        moodHistoryFragment.moodList = new ArrayList<>(mockMoodList);
        moodHistoryFragment.filteredMoodList = new ArrayList<>(mockMoodList);

    }

}
