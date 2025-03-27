package com.example.cmput301_team_project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Class for testing {@link UserDatabaseService}.
 *
 * Verifies that all methods regarding adding users to the database are working as intended.
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

    private UserDatabaseService userDatabaseService;
    private FirebaseAuthenticationService firebaseAuthenticationService;

    /**
     * sets up the mocks needed before testing the UserDatabaseService class.
     *
     * Mocks a user collection, UserDatabaseService instance, and FirebaseAuthenticationService
     * instance.
     */
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

    /**
     * Verifies that a newly created user is being added to the database and is visible in it.
     */
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

    /**
     * Verifies that a user actually exists in the database, ensuring that the userExists() method
     * is properly interacting with the Firestore database and correctly identifies the user's
     * document.
     */
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
