package com.example.cmput301_team_project;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * Class for testing {@link MoodDatabaseService}.
 *
 * Tests to validate all methods of adding, deleting, and editing Moods are being updated to the
 * database.
 */
public class MoodDatabaseServiceUnitTest {
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockMoodCollection;
    @Mock
    private DocumentReference mockDocRef;

    private MoodDatabaseService moodDatabaseService;

    /**
     * Sets up the mocks needed for the tests to run properly.
     *
     * Makes a mock Mood collection, and a mock MoodDatabaseService instance.
     */
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockFirestore.collection("moods")).thenReturn(mockMoodCollection);
        when(mockMoodCollection.document()).thenReturn(mockDocRef);
        when(mockMoodCollection.document(anyString())).thenReturn(mockDocRef);

        MoodDatabaseService.setInstanceForTesting(mockFirestore, Runnable::run);
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    /**
     * Verifies that the newly added Mood is actually being added to the database.
     */
    @Test
    public void testAddMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null, null);

        moodDatabaseService.addMood(mood);
        verify(mockMoodCollection).add(mood);
    }

    /**
     * verifies that the deleted Mood and all other related objects (such as the Moods respective
     * comments) are actually being removed from the database.
     */
    @Test
    public void testDeleteMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.DISGUST, MoodSocialSituationEnum.SEVERAL, "test", true, "me", new Date(), null, null);
        mood.setId("mockId");
        CollectionReference mockCommentsCollection = mock();
        Task<QuerySnapshot> mockQuerySnapshot = mock();
        when(mockDocRef.collection("comments")).thenReturn(mockCommentsCollection);
        when(mockCommentsCollection.get()).thenReturn(mockQuerySnapshot);

        moodDatabaseService.deleteMood(mood);
        verify(mockDocRef).delete();
    }

    /**
     * Verifies that the edited Mood is actually being updated in the database.
     */
    @Test
    public void testUpdateMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null, null);
        mood.setId("mockId");

        moodDatabaseService.updateMood(mood);
        verify(mockDocRef).set(mood);
    }
}
