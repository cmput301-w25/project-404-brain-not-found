package com.example.cmput301_team_project;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.model.MoodFilterState;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * class for testing {@link com.example.cmput301_team_project.ui.MoodHistoryFragment} and
 * the filtering functions
 */
@RunWith(MockitoJUnitRunner.class)
public class MoodFilterUnitTest {
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockMoodCollection;
    @Mock
    private Query mockQuery;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    private MoodDatabaseService moodDatabaseService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockFirestore.collection("moods")).thenReturn(mockMoodCollection);

        when(mockMoodCollection.whereEqualTo("author", "testUser")).thenReturn(mockQuery);
        when(mockQuery.orderBy("date", Query.Direction.DESCENDING)).thenReturn(mockQuery);

        MoodDatabaseService.setInstanceForTesting(mockFirestore, Runnable::run);
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    @Test
    public void filterByEmotionShouldShowValidMoods() {
        MoodFilterState moodFilterState = new MoodFilterState(null, MoodEmotionEnum.ANGER, null, null);

        Query resultQuery = mock();
        when(mockQuery.whereEqualTo("emotion", moodFilterState.emotion().toString())).thenReturn(resultQuery);
        when(resultQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("emotion")).thenReturn(moodFilterState.emotion().toString());
        when(mockDocumentSnapshot.getString("socialSituation")).thenReturn("NONE");

        Task<List<Mood>> result = moodDatabaseService.getMoodList("testUser", moodFilterState);

        verify(mockQuery).whereEqualTo("emotion", moodFilterState.emotion().toString());
        verify(resultQuery).get();

        assertTrue(result.isSuccessful());
        List<Mood> mood = result.getResult();
        assertNotNull(mood);
        assertFalse(mood.isEmpty());
        assertSame(mood.get(0).getEmotion(), MoodEmotionEnum.ANGER);
    }

    @Test
    public void filterByEmotionShouldShowEmptyOnNoMatch() {
        MoodFilterState moodFilterState = new MoodFilterState(null, MoodEmotionEnum.ANGER, null, null);

        Query resultQuery = mock();
        when(mockQuery.whereEqualTo("emotion", moodFilterState.emotion().toString())).thenReturn(resultQuery);
        when(resultQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        Task<List<Mood>> result = moodDatabaseService.getMoodList("testUser", moodFilterState);

        verify(mockQuery).whereEqualTo("emotion", moodFilterState.emotion().toString());
        verify(resultQuery).get();

        assertTrue(result.isSuccessful());
        List<Mood> mood = result.getResult();
        assertNotNull(mood);
        assertTrue(mood.isEmpty());
    }

    @Test
    public void filterByDayShouldShowValidMoods() {
        MoodFilterState moodFilterState = new MoodFilterState(-1, null, null, null);

        Query resultQuery = mock();
        when(mockQuery.whereGreaterThan(eq("date"), any())).thenReturn(resultQuery);
        when(resultQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("emotion")).thenReturn("ANGER");
        when(mockDocumentSnapshot.getString("socialSituation")).thenReturn("NONE");
        when(mockDocumentSnapshot.getDate("date")).thenReturn(new Date());

        Task<List<Mood>> result = moodDatabaseService.getMoodList("testUser", moodFilterState);

        verify(mockQuery).whereGreaterThan(eq("date"), any());
        verify(resultQuery).get();

        assertTrue(result.isSuccessful());
        List<Mood> mood = result.getResult();
        assertNotNull(mood);
        assertFalse(mood.isEmpty());
    }

    @Test
    public void filterByWeekShouldShowValidMoods() {
        MoodFilterState moodFilterState = new MoodFilterState(-7, null, null, null);

        Query resultQuery = mock();
        when(mockQuery.whereGreaterThan(eq("date"), any())).thenReturn(resultQuery);
        when(resultQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("emotion")).thenReturn("ANGER");
        when(mockDocumentSnapshot.getString("socialSituation")).thenReturn("NONE");
        when(mockDocumentSnapshot.getDate("date")).thenReturn(new Date());

        Task<List<Mood>> result = moodDatabaseService.getMoodList("testUser", moodFilterState);

        verify(mockQuery).whereGreaterThan(eq("date"), any());
        verify(resultQuery).get();

        assertTrue(result.isSuccessful());
        List<Mood> mood = result.getResult();
        assertNotNull(mood);
        assertFalse(mood.isEmpty());
    }

    @Test
    public void filterByMonthShouldShowValidMoods() {
        MoodFilterState moodFilterState = new MoodFilterState(-30, null, null, null);

        Query resultQuery = mock();
        when(mockQuery.whereGreaterThan(eq("date"), any())).thenReturn(resultQuery);
        when(resultQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("emotion")).thenReturn("ANGER");
        when(mockDocumentSnapshot.getString("socialSituation")).thenReturn("NONE");
        when(mockDocumentSnapshot.getDate("date")).thenReturn(new Date());

        Task<List<Mood>> result = moodDatabaseService.getMoodList("testUser", moodFilterState);

        verify(mockQuery).whereGreaterThan(eq("date"), any());
        verify(resultQuery).get();

        assertTrue(result.isSuccessful());
        List<Mood> mood = result.getResult();
        assertNotNull(mood);
        assertFalse(mood.isEmpty());
    }

    @Test
    public void filterByTextShouldShowValidMoods() {
        // ensure case-insensitive comparison
        MoodFilterState moodFilterState = new MoodFilterState(null, null, "testText", null);

        when(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("emotion")).thenReturn("ANGER");
        when(mockDocumentSnapshot.getString("socialSituation")).thenReturn("NONE");
        when(mockDocumentSnapshot.getString("trigger")).thenReturn("hello testtext123");

        Task<List<Mood>> result = moodDatabaseService.getMoodList("testUser", moodFilterState);

        verify(mockQuery).get();

        assertTrue(result.isSuccessful());
        List<Mood> mood = result.getResult();
        assertNotNull(mood);
        assertFalse(mood.isEmpty());
    }
}
