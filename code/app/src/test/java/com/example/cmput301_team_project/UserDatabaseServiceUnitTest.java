package com.example.cmput301_team_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.model.AppUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

/**
 * Class for testing {@link UserDatabaseService}
 */
public class UserDatabaseServiceUnitTest {
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private CollectionReference mockUserCollection;
    @Mock
    private DocumentReference mockDocRef;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;
    @Mock
    private Task<DocumentSnapshot> mockTask;
    @Mock
    private QuerySnapshot mockQuerySnapshot;

    private UserDatabaseService userDatabaseService;
    private FirebaseAuthenticationService firebaseAuthenticationService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockFirestore.collection("users")).thenReturn(mockUserCollection);
        when(mockUserCollection.document()).thenReturn(mockDocRef);
        when(mockUserCollection.document(anyString())).thenReturn(mockDocRef);

        UserDatabaseService.setInstanceForTesting(mockFirestore, Runnable::run);
        userDatabaseService = UserDatabaseService.getInstance();

        FirebaseAuthenticationService.setInstanceForTesting(mockAuth);
        firebaseAuthenticationService = FirebaseAuthenticationService.getInstance();
    }

    @Test
    public void testAddUser() {
        AppUser mockUser = new AppUser("mockUsername", "mockName", "mockPassword");

        Task<AuthResult> mockAuthResultTask = mock();

        when(mockUserCollection.document(mockUser.getUsername())).thenReturn(mockDocRef);
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthResultTask);
        when(mockAuthResultTask.continueWith(any())).thenReturn(Tasks.forResult("mockEmail"));

        userDatabaseService.addUser(mockUser);
        verify(mockDocRef).set(mockUser);
    }

    @Test
    public void testUserExists() {
        String username = "mockUsername";
        when(mockUserCollection.document(username)).thenReturn(mockDocRef);
        when(mockDocRef.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockTask.continueWith(any(Continuation.class))).thenAnswer(invocation -> {
            Continuation<DocumentSnapshot, Boolean> continuation = invocation.getArgument(0);
            TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

            boolean result = continuation.then(mockTask);
            taskCompletionSource.setResult(result);

            return taskCompletionSource.getTask();
        });

        when(mockDocumentSnapshot.exists()).thenReturn(true);

        Task<Boolean> userExists = userDatabaseService.userExists(username);

        assertTrue(userExists.getResult());

        when(mockDocumentSnapshot.exists()).thenReturn(false);

        userExists = userDatabaseService.userExists(username);
        verify(mockDocRef, times(2)).get();
        assertFalse(userExists.getResult());
    }

    @Test
    public void testGetDisplayName() {
        when(mockDocRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("name")).thenReturn("testName");

        Task<String> result = userDatabaseService.getDisplayName("mockUsername");

        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "testName");
    }

    @Test
    public void testFollowerCount() {
        CollectionReference followersCollection = mock();
        AggregateQuery mockQuery = mock();
        AggregateQuerySnapshot mockQuerySnapshot = mock();

        when(mockDocRef.collection("followers")).thenReturn(followersCollection);
        when(followersCollection.count()).thenReturn(mockQuery);
        when(mockQuery.get(AggregateSource.SERVER)).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getCount()).thenReturn(99L);

        Task<Long> result = userDatabaseService.followerCount("mockUsername");

        assertTrue(result.isSuccessful());
        assertEquals((long) result.getResult(), 99L);
    }

    @Test
    public void testFollowingCount() {
        CollectionReference followingCollection = mock();
        AggregateQuery mockQuery = mock();
        AggregateQuerySnapshot mockQuerySnapshot = mock();

        when(mockDocRef.collection("following")).thenReturn(followingCollection);
        when(followingCollection.count()).thenReturn(mockQuery);
        when(mockQuery.get(AggregateSource.SERVER)).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getCount()).thenReturn(123L);

        Task<Long> result = userDatabaseService.followingCount("mockUsername");

        assertTrue(result.isSuccessful());
        assertEquals((long) result.getResult(), 123L);
    }

    @Test
    public void testRemoveRequest() {
        try (MockedStatic<Tasks> mockedTasks = mockStatic(Tasks.class)) {
            CollectionReference requestsReceivedCollection = mock();
            CollectionReference requestsSentCollection = mock();
            DocumentReference followerReference = mock();
            DocumentReference targetReference = mock();

            Task<Void> mockTask = Tasks.forResult(null);
            mockedTasks.when(() -> Tasks.whenAll(anyList())).thenReturn(mockTask);

            when(mockDocRef.collection("requestsReceived")).thenReturn(requestsReceivedCollection);
            when(requestsReceivedCollection.document("mockFollower")).thenReturn(followerReference);
            when(followerReference.delete()).thenReturn(mockTask);

            when(mockDocRef.collection("requestsSent")).thenReturn(requestsSentCollection);
            when(requestsSentCollection.document("mockTarget")).thenReturn(targetReference);
            when(targetReference.delete()).thenReturn(mockTask);

            userDatabaseService.removeRequest("mockFollower", "mockTarget");
            verify(followerReference).delete();
            verify(targetReference).delete();
        }
    }

    @Test
    public void testGetRequests() {
        CollectionReference mockRequestsCollection = mock();

        when(mockDocRef.collection("requestsReceived")).thenReturn(mockRequestsCollection);
        when(mockRequestsCollection.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getId()).thenReturn("testUsername");

        Task<List<String>> result = userDatabaseService.getRequests("mockUsername");

        assertTrue(result.isSuccessful());
        assertEquals(result.getResult().size(), 1);
        assertEquals(result.getResult().get(0), "testUsername");
    }
}
