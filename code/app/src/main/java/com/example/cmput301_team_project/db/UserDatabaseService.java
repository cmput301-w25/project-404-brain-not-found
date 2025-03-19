package com.example.cmput301_team_project.db;


import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import android.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import kotlin.NotImplementedError;

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
        instance = new UserDatabaseService(db);
    }

    public void addUser(AppUser user) {
        DocumentReference uref = usersRef.document(user.getUsername());
        uref.set(user);

        Map<String, Object> updates = new HashMap<>();
        updates.put("usernameLower", user.getUsername().toLowerCase());
        updates.put("nameLower", user.getName().toLowerCase());

        uref.update(updates);
    }

    public Task<String> getDisplayName(String username){
        DocumentReference uref = usersRef.document(username);
        return uref.get().continueWith(task -> {
            if(!task.isSuccessful()) {
                throw task.getException();
            }
            return task.getResult().getString("name");
        });
    }

    /**
     * Validates a user's credentials by checking the hashed password.
     * This method retrieves the stored password hash and salt from Firestore,
     * hashes the input password with the same salt, and compares the results.
     *
     * @param username The username of the user.
     * @param inputPassword The password entered by the user.
     * @return A {@link Task} that resolves to {@code true} if the password matches, otherwise {@code false}.
     */
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

    /**
     * Checks if a user exists in the Firestore database.
     *
     * @param username The username to check.
     * @return A {@link Task} that resolves to {@code true} if the user exists, otherwise {@code false}.
     */
    public Task<Boolean> userExists(String username){
        DocumentReference docRef = usersRef.document(username);

        return docRef.get().continueWith(task ->{
                return task.isSuccessful() && task.getResult().exists();
                });
    }

    private Task<QuerySnapshot> getBaseUserSearchQuery(String field, String query) {
        return usersRef.orderBy(field)
                .startAt(query.toLowerCase())
                .endAt(query.toLowerCase() + "\uf8ff")
                .get();
    }

    /**
     * Searches for users in the database
     *
     * @param query search query
     * @return A {@link Task} that resolves to a list of users matching the query
     */
    public Task<List<PublicUser>> userSearch(String query) {
        return Tasks.whenAllComplete(getBaseUserSearchQuery("usernameLower", query), getBaseUserSearchQuery("nameLower", query)).continueWith(results -> {
                if(results.isSuccessful()) {
                    Set<DocumentSnapshot> documentSnapshotSet = new HashSet<>();

                    for(Task<?> task : results.getResult()) {
                        if(task.isSuccessful() && task.getResult() instanceof QuerySnapshot documents) {
                            documentSnapshotSet.addAll(documents.getDocuments());
                        }
                    }

                    return documentSnapshotSet.stream()
                            .map(d -> new PublicUser(d.getString("username"), d.getString("name")))
                            .collect(Collectors.toCollection(ArrayList::new));
                }
                else {
                    return new ArrayList<>();
                }
        });
    }

    /**
     * Searches for followers of a given user in the database
     *
     * @param username username of the user whose followers the query searches in
     * @param query search query
     * @return A {@link Task} that resolves to a list of followers of the user matching the query
     */
    public Task<List<PublicUser>> userFollowersSearch(String username, String query) {
        // TODO: implement search among followers, getBaseUserSearchQuery can be reused here
        throw new NotImplementedError();
    }

    /**
     * Searches for users followed by a given user in the database
     *
     * @param username username of the user whose following the query searches in
     * @param query search query
     * @return A {@link Task} that resolves to a list of users followed by the user matching the query
     */
    public Task<List<PublicUser>> userFollowingSearch(String username, String query) {
        // TODO: implement search among following, getBaseUserSearchQuery can be reused here
        throw new NotImplementedError();
    }

    /**
     * One user requests permission from another user to follow
     *
     * @param follower username of the user who initiates the follow action
     * @param target username of the user who is being followed
     */
    public void followUser(String follower, String target) {
        // TODO: implement follow db query
    }

    /**
     * One user unfollows another user
     *
     * @param follower username of the user who initiates the unfollow action
     * @param target username of the user who is being unfollowed
     */
    public void unfollowUser(String follower, String target) {
        // TODO: implement unfollow db query
    }

    /**
     * One user revokes permission to follow them from another user
     *
     * @param following username of the user who is being followed by the other user
     * @param target username of the user whose follow permission should be revoked
     */
    public void revokeFollow(String following, String target) {
        // TODO: implement revoke permission db query
    }


    /**
     * Hashes a password using the PBKDF2 algorithm with HMAC-SHA1.
     *
     * @param password The password to hash.
     * @param salt The salt used for hashing.
     * @return A Base64-encoded string representing the hashed password.
     * @throws NoSuchAlgorithmException If the algorithm is not available.
     * @throws InvalidKeySpecException If the key specification is invalid.
     */
    public String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 256;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.encodeToString(hash, Base64.NO_WRAP);

    }

    /**
     * Generates a random salt for password hashing.
     *
     * @return A byte array representing the generated salt.
     */
    public byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}