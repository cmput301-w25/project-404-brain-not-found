package com.example.cmput301_team_project;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import android.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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

    private UserDatabaseService(FirebaseFirestore db) {
        super(db);

        usersRef = db.collection("users");
    }

    public static UserDatabaseService getInstance() {
        if (instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }

    public static void setInstanceForTesting(FirebaseFirestore db) {
        if(instance == null) {
            instance = new UserDatabaseService(db);
        }
    }

    public void addUser(AppUser user) {
        DocumentReference uref = usersRef.document(user.username);
        uref.set(user);
    }

    public Task<Boolean> validateCredentials(String username, String inputPassword)
    {
        DocumentReference uref = usersRef.document(username);

        return uref.get().continueWith(task -> {
            if(!task.isSuccessful()) {
                return false;
            }

            String password = task.getResult().getString("password");
            byte[] salt = Base64.decode(task.getResult().getString("salt"), Base64.DEFAULT);
            String hashed = hashPassword(inputPassword, salt);
            return hashed.equals(password);
        });
    }

    public Task<Boolean> userExists(String username){
        DocumentReference docRef = usersRef.document(username);

        Task<Boolean> document = docRef.get().continueWith(task ->{
                return task.isSuccessful() && task.getResult().exists();
                });
        return document;
    }

    public String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 256;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.encodeToString(hash, Base64.NO_WRAP);

    }

    public byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}