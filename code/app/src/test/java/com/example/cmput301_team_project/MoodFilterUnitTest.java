package com.example.cmput301_team_project;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.ui.MoodHistoryFragment;
import com.example.cmput301_team_project.ui.MoodListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * class for testing {@link com.example.cmput301_team_project.ui.MoodHistoryFragment} and
 * the filtering functions
 */
@RunWith(MockitoJUnitRunner.class)
public class MoodFilterUnitTest {
    @Mock
    private MoodDatabaseService mockDatabaseService;
    @Mock
    private SessionManager mockSessionManager;
    @Mock
    private MoodListAdapter mockMoodListAdapter;

    private MoodHistoryFragment moodHistoryFragment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        try (MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class);
             MockedStatic<MoodDatabaseService> mockedDatabaseService = mockStatic(MoodDatabaseService.class)) {

            mockedSessionManager.when(SessionManager::getInstance).thenReturn(mockSessionManager);

            mockedDatabaseService.when(MoodDatabaseService::getInstance).thenReturn(mockDatabaseService);

            moodHistoryFragment = spy(new MoodHistoryFragment());
            moodHistoryFragment.moodListAdapter = mockMoodListAdapter;

        }
    }

    private List<Mood> createSampleMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date twoDaysAgo = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        Date weekAgo = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -365);
        Date yearAgo = calendar.getTime();

        List<Mood> sampleMoods = new ArrayList<>();
        sampleMoods.add(Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", true, "testUser", today, null));
        sampleMoods.add(Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.ALONE, "test2", true, "testUser", twoDaysAgo, null));
        sampleMoods.add(Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test3", true, "testUser", weekAgo, null));
        sampleMoods.add(Mood.createMood(MoodEmotionEnum.CONFUSION, MoodSocialSituationEnum.ALONE, "test4", true, "testUser", yearAgo, null));

        return sampleMoods;
    }

    @Test
    public void filterByEmotionShouldShowValidMoods() {

        List<Mood> sampleMoods = createSampleMoods();
        moodHistoryFragment.moodList = new ArrayList<>(sampleMoods);

        moodHistoryFragment.filterByEmotion("ANGER");

        ArgumentCaptor<ArrayList<Mood>> filteredMoodsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(moodHistoryFragment).updateFilters(filteredMoodsCaptor.capture());

        List<Mood> filteredMoods = filteredMoodsCaptor.getValue();
        assertEquals(1, filteredMoods.size());
        assertEquals(filteredMoods.get(0).getEmotion(), MoodEmotionEnum.ANGER);
    }

    @Test
    public void filterByEmotionShouldShowEmptyOnNoMatch() {
        List<Mood> sampleMoods = createSampleMoods();
        moodHistoryFragment.moodList = new ArrayList<>(sampleMoods);

        moodHistoryFragment.filterByEmotion("SHAME");

        ArgumentCaptor<ArrayList<Mood>> filteredMoodsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(moodHistoryFragment).updateFilters(filteredMoodsCaptor.capture());

        List<Mood> filteredMoods = filteredMoodsCaptor.getValue();
        assertEquals(0, filteredMoods.size());

    }

    @Test
    public void filterByDayShouldShowValidMoods() {
        // Prepare
        List<Mood> sampleMoods = createSampleMoods();
        moodHistoryFragment.moodList = new ArrayList<>(sampleMoods);

        moodHistoryFragment.filterByTime(-1);

        ArgumentCaptor<ArrayList<Mood>> filteredMoodsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(moodHistoryFragment).updateFilters(filteredMoodsCaptor.capture());

        List<Mood> filteredMoods = filteredMoodsCaptor.getValue();
        assertEquals(1, filteredMoods.size());
        assertEquals(MoodEmotionEnum.ANGER, filteredMoods.get(0).getEmotion());
    }

    @Test
    public void filterByWeekShouldShowValidMoods() {
        // Prepare
        List<Mood> sampleMoods = createSampleMoods();
        moodHistoryFragment.moodList = new ArrayList<>(sampleMoods);

        moodHistoryFragment.filterByTime(-7);

        ArgumentCaptor<ArrayList<Mood>> filteredMoodsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(moodHistoryFragment).updateFilters(filteredMoodsCaptor.capture());

        List<Mood> filteredMoods = filteredMoodsCaptor.getValue();
        assertEquals(2, filteredMoods.size());
        assertEquals(MoodEmotionEnum.ANGER, filteredMoods.get(0).getEmotion());
        assertEquals(MoodEmotionEnum.SADNESS, filteredMoods.get(1).getEmotion());
    }

    @Test
    public void filterByMonthShouldShowValidMoods() {
        // Prepare
        List<Mood> sampleMoods = createSampleMoods();
        moodHistoryFragment.moodList = new ArrayList<>(sampleMoods);

        moodHistoryFragment.filterByTime(-30);

        ArgumentCaptor<ArrayList<Mood>> filteredMoodsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(moodHistoryFragment).updateFilters(filteredMoodsCaptor.capture());

        List<Mood> filteredMoods = filteredMoodsCaptor.getValue();
        assertEquals(3, filteredMoods.size());
        assertEquals(MoodEmotionEnum.ANGER, filteredMoods.get(0).getEmotion());
        assertEquals(MoodEmotionEnum.SADNESS, filteredMoods.get(1).getEmotion());
        assertEquals(MoodEmotionEnum.HAPPINESS, filteredMoods.get(2).getEmotion());
    }

    @Test
    public void filterByTextShouldShowValidMoods() {
        List<Mood> sampleMoods = createSampleMoods();
        moodHistoryFragment.moodList = new ArrayList<>(sampleMoods);

        moodHistoryFragment.filterByText("test1");

        ArgumentCaptor<ArrayList<Mood>> filteredMoodsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(moodHistoryFragment).updateFilters(filteredMoodsCaptor.capture());

        List<Mood> filteredMoods = filteredMoodsCaptor.getValue();
        assertEquals(1, filteredMoods.size());
        assertEquals("test1", filteredMoods.get(0).getTrigger());
    }
}
