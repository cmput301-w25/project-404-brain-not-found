package com.example.cmput301_team_project;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Singleton class to manage user-related operations with the firestore database
 * Handles all user-related queries
 */
public class UserDatabaseService extends BaseDatabaseService {
    private static UserDatabaseService instance = null;
    private final CollectionReference usersRef;

    private UserDatabaseService() {
        super();

        usersRef = db.collection("users");
    }

    public static UserDatabaseService getInstance() {
        if (instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }

    public void addUser(AppUser user) {
        DocumentReference uref = usersRef.document(user.username);
        uref.set(user);
    }

    public DocumentReference getDocRef(String username) {
        return usersRef.document(username);
    }

    private Boolean userExists = false;
    public Boolean userExists(String username){
        DocumentReference docRef = usersRef.document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        userExists = true;
                    }else{
                        userExists = false;
                    }
                }
            }
        });
        return userExists;
    }
}