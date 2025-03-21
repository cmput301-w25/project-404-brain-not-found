package com.example.cmput301_team_project;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

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

        MoodDatabaseService.setInstanceForTesting(mockFirestore);
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    @Test
    public void testAddMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null);

        moodDatabaseService.addMood(mood);
        verify(mockMoodCollection).add(mood);
    }

    @Test
    public void testDeleteMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.DISGUST, MoodSocialSituationEnum.SEVERAL, "test", true, "me", new Date(), null);
        mood.setId("mockId");

        moodDatabaseService.deleteMood(mood);
        verify(mockDocRef).delete();
    }

    @Test
    public void testUpdateMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null);
        mood.setId("mockId");

        moodDatabaseService.updateMood(mood);
        verify(mockDocRef).set(mood);
    }
}
