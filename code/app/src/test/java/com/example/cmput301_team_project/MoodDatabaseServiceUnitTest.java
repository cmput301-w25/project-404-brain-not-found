package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
 * Class for testing {@link MoodDatabaseService}
 */
public class MoodDatabaseServiceUnitTest {
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockMoodCollection;
    @Mock
    private DocumentReference mockDocRef;

    private MoodDatabaseService moodDatabaseService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockFirestore.collection("moods")).thenReturn(mockMoodCollection);
        when(mockMoodCollection.document()).thenReturn(mockDocRef);
        when(mockMoodCollection.document(anyString())).thenReturn(mockDocRef);

        // mock to return valid queries (for filtering tests)
        Query mockQuery = mock(Query.class);
        when(mockMoodCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery);

        when(mockQuery.orderBy(anyString(), any())).thenReturn(mockQuery);
        Task<QuerySnapshot> mockTask = mock(Task.class);
        when(mockQuery.get()).thenReturn(mockTask);

        when(mockTask.isSuccessful()).thenReturn(true);

        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        List<DocumentSnapshot> mockDocuments = new ArrayList<>();

        DocumentSnapshot mockDocument = mock(DocumentSnapshot.class);
        when(mockDocument.toObject(Mood.class)).thenReturn(Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test", "me", new Date(), null));
        mockDocuments.add(mockDocument);

        when(mockQuerySnapshot.getDocuments()).thenReturn(mockDocuments);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);

        MoodDatabaseService.setInstanceForTesting(mockFirestore);
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    @Test
    public void testAddMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test", "me", new Date(), null);

        moodDatabaseService.addMood(mood);
        verify(mockMoodCollection).add(mood);
    }

    @Test
    public void testDeleteMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.DISGUST, MoodSocialSituationEnum.SEVERAL, "test", "me", new Date(), null);
        mood.setId("mockId");

        moodDatabaseService.deleteMood(mood);
        verify(mockDocRef).delete();
    }

    @Test
    public void testUpdateMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.ALONE, "test", "me", new Date(), null);
        mood.setId("mockId");

        moodDatabaseService.updateMood(mood);
        verify(mockDocRef).set(mood);
    }

    @Test
    public void filterByEmotionShouldShowProperMoods() {
        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", "me", new Date(), null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test1", "me", new Date(), null),
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", "me", new Date(), null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList()).thenReturn(mockTask);

        moodDatabaseService.filterByEmotion("ANGER", filteredMoods -> {
            assertEquals(2, filteredMoods.size());
            assertEquals(MoodEmotionEnum.ANGER, filteredMoods.get(0).getEmotion());
            assertEquals(MoodEmotionEnum.ANGER, filteredMoods.get(1).getEmotion());
        });

    }

    @Test
    public void filterByDayShouldShowProperMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "not filtered", "me", today, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "filtered", "me", yesterday, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList()).thenReturn(mockTask);

        moodDatabaseService.filterByTime(-1, filteredMoods -> {
            assertEquals(1, filteredMoods.size());
            assertEquals("filtered", filteredMoods.get(0).getTrigger());
        });
    }

    @Test
    public void filterByWeekShouldShowProperMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "not filtered", "me", today, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "filtered", "me", lastWeek, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList()).thenReturn(mockTask);

        moodDatabaseService.filterByTime(-7, filteredMoods -> {
            assertEquals(1, filteredMoods.size());
            assertEquals("filtered", filteredMoods.get(0).getTrigger());
        });
    }

    @Test
    public void filterByMonthShouldShowProperMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date lastMonth = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "not filtered", "me", today, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "filtered", "me", lastMonth, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList()).thenReturn(mockTask);

        moodDatabaseService.filterByTime(-30, filteredMoods -> {
            assertEquals(1, filteredMoods.size());
            assertEquals("filtered", filteredMoods.get(0).getTrigger());
        });
    }

    @Test
    public void filterByTextShouldShowProperMoods() {
        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test1", "user1", new Date(), null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.SEVERAL, "test1 test2", "user2", new Date(), null),
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1 test2 test3", "user3", new Date(), null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList()).thenReturn(mockTask);

        moodDatabaseService.filterByText(new String[]{"test1", "test2"}, filteredMoods -> {
            assertEquals(3, filteredMoods.size());
            assertEquals("test1 test2", filteredMoods.get(0).getTrigger());
            assertEquals("test1 test2 test3", filteredMoods.get(1).getTrigger());
            assertEquals("test1", filteredMoods.get(2).getTrigger());
        });
    }
}
