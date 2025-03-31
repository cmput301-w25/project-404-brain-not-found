package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Comment;
import com.example.cmput301_team_project.model.Mood;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
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
        when(mockMoodCollection.document()).thenReturn(mockDocRef);
        when(mockMoodCollection.document(anyString())).thenReturn(mockDocRef);

        MoodDatabaseService.setInstanceForTesting(mockFirestore, Runnable::run);
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    @Test
    public void testAddMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null, null);

        moodDatabaseService.addMood(mood);
        verify(mockMoodCollection).add(mood);
    }

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

    @Test
    public void testUpdateMood() {
        Mood mood = Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null, null);
        mood.setId("mockId");

        moodDatabaseService.updateMood(mood);
        verify(mockDocRef).set(mood);
    }

    @Test
    public void testGetMostRecentMood() {
        Query mockRes = mock();

        when(mockMoodCollection.whereEqualTo("author", "me")).thenReturn(mockQuery);
        when(mockQuery.orderBy("date", Query.Direction.DESCENDING)).thenReturn(mockQuery);
        when(mockQuery.limit(1)).thenReturn(mockRes);
        when(mockRes.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.isEmpty()).thenReturn(false);
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("emotion")).thenReturn("ANGER");

        Task<String> result = moodDatabaseService.getMostRecentMood("me");
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "ANGER");
    }

    @Test
    public void testAddComment() {
        CollectionReference mockCommentsCollection = mock();
        Timestamp frozenTimestamp = Timestamp.now();
        try (MockedStatic<Timestamp> mockedTimestamp = mockStatic(Timestamp.class)) {
            mockedTimestamp.when(Timestamp::now).thenReturn(frozenTimestamp);
            when(mockDocRef.collection("comments")).thenReturn(mockCommentsCollection);

            Comment comment = new Comment("mockUser", "comment text");
            comment.setTimestamp(frozenTimestamp);
            moodDatabaseService.addComment("mockId", comment);
            verify(mockCommentsCollection).add(comment);
        }
    }

    @Test
    public void testGetComments() {
        CollectionReference mockCommentsCollection = mock();
        when(mockDocRef.collection("comments")).thenReturn(mockCommentsCollection);
        when(mockCommentsCollection.orderBy("date", Query.Direction.DESCENDING)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("username")).thenReturn("mockUser");
        when(mockDocumentSnapshot.getString("text")).thenReturn("mockText");

        Task<List<Comment>> result = moodDatabaseService.getComments("mockId");
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult().size(), 1);
        assertEquals(result.getResult().get(0).getUsername(), "mockUser");
        assertEquals(result.getResult().get(0).getText(), "mockText");
    }

    @Test
    public void testIsPublic() {
        when(mockDocRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getBoolean("public")).thenReturn(false);

        Task<Boolean> result = moodDatabaseService.isPublic("mockId");
        assertTrue(result.isSuccessful());
        assertFalse(result.getResult());


        when(mockDocumentSnapshot.getBoolean("public")).thenReturn(true);

        result = moodDatabaseService.isPublic("mockId");
        assertTrue(result.isSuccessful());
        assertTrue(result.getResult());
    }
}
