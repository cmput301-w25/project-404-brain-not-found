package com.example.cmput301_team_project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.model.AppUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Class for testing {@link UserDatabaseService}
 */
public class UserDatabaseServiceUnitTest {
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockUserCollection;
    @Mock
    private DocumentReference mockDocRef;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;
    @Mock
    private Task<DocumentSnapshot> mockTask;

    private UserDatabaseService userDatabaseService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockFirestore.collection("users")).thenReturn(mockUserCollection);
        when(mockUserCollection.document()).thenReturn(mockDocRef);
        when(mockUserCollection.document(anyString())).thenReturn(mockDocRef);

        UserDatabaseService.setInstanceForTesting(mockFirestore);
        userDatabaseService = UserDatabaseService.getInstance();
    }

    @Test
    public void testAddUser() {
        String username = "mockUsername";

        AppUser mockUser = new AppUser(username, "mockPassword", "mockSalt");

        when(mockUserCollection.document(username)).thenReturn(mockDocRef);

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
}
