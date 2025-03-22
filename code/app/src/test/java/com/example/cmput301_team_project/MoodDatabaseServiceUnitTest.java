package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

        Task<QuerySnapshot> mockFirestoreTask = mock(Task.class);
        when(mockQuery.get()).thenReturn(mockFirestoreTask);
        when(mockFirestoreTask.isSuccessful()).thenReturn(true);

        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        List<DocumentSnapshot> mockDocuments = new ArrayList<>();
        DocumentSnapshot mockDocument = mock(DocumentSnapshot.class);

        when(mockDocument.toObject(Mood.class)).thenReturn(
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test", true, "me", new Date(), null)
        );
        mockDocuments.add(mockDocument);

        when(mockQuerySnapshot.getDocuments()).thenReturn(mockDocuments);
        when(mockFirestoreTask.getResult()).thenReturn(mockQuerySnapshot);

        Task<List<Mood>> mockMoodListTask = mock(Task.class);
        when(mockMoodListTask.isSuccessful()).thenReturn(true);

        MoodDatabaseService.setInstanceForTesting(mockFirestore);
        moodDatabaseService = spy(MoodDatabaseService.getInstance());
        doReturn(mockMoodListTask).when(moodDatabaseService).getMoodList(anyString());
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

    @Test
    public void filterByEmotionShouldShowProperMoods() {
        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", true, "me", new Date(), null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test1",true, "me", new Date(), null),
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1",true, "me", new Date(), null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByEmotion("me", "ANGER", filteredMoods ->{
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(2, capturedMoods[0].size());
            assertEquals(MoodEmotionEnum.ANGER, capturedMoods[0].get(0).getEmotion());
            assertEquals(MoodEmotionEnum.ANGER, capturedMoods[0].get(1).getEmotion());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    //test for if an emotion is filtered, but no mood matches
    @Test
    public void filterByEmotionShouldShowShowEmptyOnNoMatch() {
        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1", true, "me", new Date(), null),
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test1",true, "me", new Date(), null),
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1",true, "me", new Date(), null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByEmotion("me", "SHAME", filteredMoods ->{
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(0, capturedMoods[0].size());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByDayShouldShowProperMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "filtered", true, "me", today, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", yesterday, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByTime("me",-1, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(1, capturedMoods[0].size());
            assertEquals(MoodEmotionEnum.HAPPINESS, capturedMoods[0].get(0).getEmotion());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByDayShouldShowEmptyWhenNoMatch() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", yesterday, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", yesterday, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByTime("me",-1, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(0, capturedMoods[0].size());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByWeekShouldShowProperMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "filtered", true, "me", today, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", lastWeek, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByTime("me",-7, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(1, capturedMoods[0].size());
            assertEquals(MoodEmotionEnum.HAPPINESS, capturedMoods[0].get(0).getEmotion());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByWeekShouldShowEmptyOnNoMatch() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", lastWeek, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", lastWeek, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByTime("me",-7, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(0, capturedMoods[0].size());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByMonthShouldShowProperMoods() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date lastMonth = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "filtered", true, "me", today, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", lastMonth, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByTime("me",-30, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(1, capturedMoods[0].size());
            assertEquals(MoodEmotionEnum.HAPPINESS, capturedMoods[0].get(0).getEmotion());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByMonthShouldShowEmptyOnNoMatch() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date lastMonth = calendar.getTime();

        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "filtered", true, "me", lastMonth, null),
                Mood.createMood(MoodEmotionEnum.SHAME, MoodSocialSituationEnum.ALONE, "not filtered", true, "me", lastMonth, null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("me")).thenReturn(mockTask);

        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByTime("me",-30, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(0, capturedMoods[0].size());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }

    @Test
    public void filterByTextShouldShowProperMoods() {
        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test1", true,"user1", new Date(), null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.SEVERAL, "test1 test2", true, "user1", new Date(), null),
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1 test2 test3", true,"user1", new Date(), null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("user1")).thenReturn(mockTask);
        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByText("user1",new String[]{"test1", "test2"}, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(3, capturedMoods[0].size());
            assertEquals("test1 test2", capturedMoods[0].get(0).getTrigger());
            assertEquals("test1 test2 test3", capturedMoods[0].get(1).getTrigger());
            assertEquals("test1", capturedMoods[0].get(2).getTrigger());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }

    }

    @Test
    public void filterByTextShouldShowEmptyOnNoMatch() {
        List<Mood> mockMoods = Arrays.asList(
                Mood.createMood(MoodEmotionEnum.HAPPINESS, MoodSocialSituationEnum.ALONE, "test1", true,"user1", new Date(), null),
                Mood.createMood(MoodEmotionEnum.SADNESS, MoodSocialSituationEnum.SEVERAL, "test1 test2", true, "user1", new Date(), null),
                Mood.createMood(MoodEmotionEnum.ANGER, MoodSocialSituationEnum.ALONE, "test1 test2 test3", true,"user1", new Date(), null)
        );

        Task<List<Mood>> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockMoods);
        when(moodDatabaseService.getMoodList("user1")).thenReturn(mockTask);
        ArgumentCaptor<OnCompleteListener<List<Mood>>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        when(mockTask.addOnCompleteListener(listenerCaptor.capture())).thenReturn(mockTask);

        final List<Mood>[] capturedMoods = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        moodDatabaseService.filterByText("user1",new String[]{"Hello"}, filteredMoods -> {
            capturedMoods[0] = filteredMoods;
            latch.countDown();
        });

        listenerCaptor.getValue().onComplete(mockTask);

        try {
            boolean callbackCompleted = latch.await(1, TimeUnit.SECONDS);
            assertTrue("Callback was not executed within timeout", callbackCompleted);

            assertEquals(0, capturedMoods[0].size());
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }

    }
}
