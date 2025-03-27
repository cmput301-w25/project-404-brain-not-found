package com.example.cmput301_team_project.db;





import com.example.cmput301_team_project.enums.FollowRelationshipEnum;
import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Singleton class to manage user-related operations with the firestore database
 * Handles all user-related queries
 */
public class UserDatabaseService extends BaseDatabaseService {
    private static UserDatabaseService instance = null;
    private final CollectionReference usersRef;

    {
        usersRef = db.collection("users");
    }

    private UserDatabaseService() {
        super();
    }

    private UserDatabaseService(FirebaseFirestore db, Executor executor) {
        super(db, executor);
    }

    public static UserDatabaseService getInstance() {
        if (instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }

    public static void setInstanceForTesting(FirebaseFirestore db, Executor executor) {
        instance = new UserDatabaseService(db, executor);
    }

    public Task<Void> addUser(AppUser user) {
        return FirebaseAuthenticationService.getInstance().registerUser(user.getUsername(), user.getPassword())
                .continueWithTask(taskExecutor, task -> {
                    if(task.isSuccessful()) {
                        DocumentReference uref = usersRef.document(user.getUsername());
                        uref.set(user);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("usernameLower", user.getUsername().toLowerCase());
                        updates.put("nameLower", user.getName().toLowerCase());
                        updates.put("email", task.getResult());
                        return uref.update(updates);
                    }
                    Tasks.forException(task.getException());
                    return Tasks.forResult(Void.TYPE.newInstance());
                });
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
        return usersRef.document(username).get()
                .continueWithTask(task -> {
                    String email = task.getResult().getString("email");
                    return FirebaseAuthenticationService.getInstance().loginUser(email, inputPassword);
                })
                .continueWith(task -> {
                    if(task.isSuccessful())
                    {
                        return task.getResult().getUser() != null;
                    }
                    return false;
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

        return docRef.get().continueWith(task -> task.isSuccessful() && task.getResult().exists());
    }

    /**
     * Base query that searches field for query in the given collection. The search is case-insensitive
     *
     * @param collectionRef collection reference to search in
     * @param field field to search in
     * @param query query to search for
     * @return IF query is empty, returns all documents in the collection, otherwise returns all documents that start with query (case-insensitive)
     */
    private Task<QuerySnapshot> getBaseUserSearchQuery(CollectionReference collectionRef, String field, String query) {
        String searchQuery = query.toLowerCase();
        return searchQuery.isEmpty()
                ? collectionRef.orderBy(field).get()
                : collectionRef.orderBy(field)
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")
                .get();
    }

    /**
     * Get follow relationship of one user with respect another user
     *
     * @param user1 username of the first user
     * @param user2 username of the second user
     * @return A {@link Task} that resolves to the follow relationship of the first user with respect to the second user
     */
    private Task<FollowRelationshipEnum> getFollowRelationship(String user1, String user2) {
        DocumentReference userRef = usersRef.document(user1);
        Task<DocumentSnapshot> followingCheck = userRef.collection("following")
                .document(user2).get();
        Task<DocumentSnapshot> requestedCheck = userRef.collection("requestsSent")
                .document(user2).get();

        return Tasks.whenAllComplete(followingCheck, requestedCheck)
                .continueWith(task -> {
                    if(followingCheck.isSuccessful() && followingCheck.getResult().exists()) {
                        return FollowRelationshipEnum.FOLLOWED;
                    }
                    if(requestedCheck.isSuccessful() && followingCheck.getResult().exists()) {
                        return FollowRelationshipEnum.REQUESTED;
                    }
                    return FollowRelationshipEnum.NONE;
                });
    }

    public Task<Long> followerCount(String username){
        CollectionReference userRef = usersRef.document(username).collection("followers");
        return userRef.count().get(AggregateSource.SERVER).continueWith(task -> {
            if (!task.isSuccessful()){
                throw task.getException();
            }
            return task.getResult().getCount();
        });
    }

    public Task<Long> followingCount(String username){
        CollectionReference userRef = usersRef.document(username).collection("following");
        return userRef.count().get(AggregateSource.SERVER).continueWith(task -> {
            if (!task.isSuccessful()){
                throw task.getException();
            }
            return task.getResult().getCount();
        });
    }
    /**
     * Searches for users in the database
     *
     * @param currentUser username of the user making the search
     * @param query search query
     * @return A {@link Task} that resolves to a list of users matching the query
     */
    public Task<List<PublicUser>> userSearch(String currentUser, String query) {
        List<Task<QuerySnapshot>> searchTasks = Arrays.asList(
                getBaseUserSearchQuery(usersRef,"usernameLower", query),
                getBaseUserSearchQuery(usersRef,"nameLower", query)
        );

        return Tasks.whenAllComplete(searchTasks).continueWithTask(results -> {
            if(results.isSuccessful()) {
                Set<DocumentSnapshot> documentSnapshotSet = new HashSet<>();

                for(Task<?> task : results.getResult()) {
                    if(task.isSuccessful() && task.getResult() instanceof QuerySnapshot documents) {
                        documentSnapshotSet.addAll(documents.getDocuments());
                    }
                }

                List<Task<PublicUser>> userMappingTasks = new ArrayList<>();
                for (DocumentSnapshot d : documentSnapshotSet) {
                    String username = d.getString("username");
                    String name = d.getString("name");

                    if (!Objects.equals(username, currentUser)) {
                        userMappingTasks.add(getFollowRelationship(currentUser, username)
                                .continueWith(task -> new PublicUser(username, name, task.getResult())));
                    }
                }

                return Tasks.whenAllSuccess(userMappingTasks);
            }
            else {
                return Tasks.forResult(new ArrayList<>());
            }
        });
    }

    /**
     * Searches for users in a nested collection of a given user
     *
     * @param username username of the user
     * @param collection nested collection name
     * @param query search query
     * @return A {@link Task} that resolves to a list of users matching the query in the nested collection
     */
    private Task<List<PublicUser>> userSearchNestedCollection(String username, String collection, String query) {
        CollectionReference collectionReference = usersRef.document(username).collection(collection);
        List<Task<QuerySnapshot>> searchTasks = Arrays.asList(
                getBaseUserSearchQuery(collectionReference,"usernameLower", query),
                getBaseUserSearchQuery(collectionReference,"nameLower", query)
        );

        return Tasks.whenAllComplete(searchTasks).continueWith(results -> {
            if(results.isSuccessful()) {
                Set<DocumentSnapshot> documentSnapshotSet = new HashSet<>();

                for (Task<?> task : results.getResult()) {
                    if (task.isSuccessful() && task.getResult() instanceof QuerySnapshot documents) {
                        documentSnapshotSet.addAll(documents.getDocuments());
                    }
                }

                return documentSnapshotSet.stream()
                        .map(d -> new PublicUser(d.getId(), d.getString("name"))).collect(Collectors.toList());
            }

            return new ArrayList<>();
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
        return userSearchNestedCollection(username, "followers", query);
    }

    /**
     * Searches for users followed by a given user in the database
     *
     * @param username username of the user whose following the query searches in
     * @param query search query
     * @return A {@link Task} that resolves to a list of users followed by the user matching the query
     */
    public Task<List<PublicUser>> userFollowingSearch(String username, String query) {
        return userSearchNestedCollection(username, "following", query);
    }

    /**
     * Gets all follow requests to a given user
     *
     * @param username username of the user whose requests the method gets
     * @return A {@link Task} that resolves to a list of follow requests to the user
     */
    public Task<List<String>> getRequests(String username) {
        return usersRef.document(username)
                .collection("requestsReceived")
                .get()
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        return task.getResult().getDocuments()
                                .stream()
                                .map(DocumentSnapshot::getId)
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                });
    }

    /**
     * Request one user to follow another
     *
     * @param follower username of the user making the follow request
     * @param target username of the user being requested to follow
     * @return A {@link Task} that resolves when request is processed
     */
    public Task<Void> requestFollow(String follower, String target) {
        Task<Void> receivedTask = getDisplayName(follower).continueWithTask(task -> usersRef.document(target)
                .collection("requestsReceived")
                .document(follower)
                .set(new PublicUser(follower, task.getResult())));

        Task<Void> sentTask = getDisplayName(target).continueWithTask(task -> usersRef.document(follower)
                .collection("requestsSent")
                .document(target)
                .set(new PublicUser(target, task.getResult())));

        return Tasks.whenAll(receivedTask, sentTask);
    }

    /**
     * Accepts a follow request
     *
     * @param follower username of the user making the follow request
     * @param target username of the user accepting the follow request
     * @return A {@link Task} that resolves when request is accepted
     */
    public Task<Void> acceptRequest(String follower, String target) {
        Task<Void> followingTask = getDisplayName(target).continueWithTask(task -> usersRef.document(follower)
            .collection("following")
            .document(target)
            .set(new PublicUser(target, task.getResult())));

        Task<Void> followerTask = getDisplayName(follower).continueWithTask(task -> usersRef.document(target)
                .collection("followers")
                .document(follower)
                .set(new PublicUser(follower, task.getResult())));

        return Tasks.whenAll(followingTask, followerTask)
                .continueWithTask(voidTask -> removeRequest(follower, target));
    }

    /**
     * Removes a follow request
     * @param follower username of the user making the follow request
     * @param target username of the user being requested to follow
     * @return A {@link Task} that resolves when removal is processed
     */
    public Task<Void> removeRequest(String follower, String target) {
        Task<Void> receivedTask = usersRef.document(target)
                .collection("requestsReceived")
                .document(follower)
                .delete();
        Task<Void> sentTask = usersRef.document(follower)
                .collection("requestsSent")
                .document(target)
                .delete();

        return Tasks.whenAll(receivedTask, sentTask);
    }

    /**
     * Removes a follow relationship
     * @param follower username of the user following
     * @param target username of the user being followed
     * @return A {@link Task} that resolves when removal is processed
     */
    public Task<Void> removeFollow(String follower, String target) {
        Task<Void> followingTask = usersRef.document(follower)
                .collection("following")
                .document(target)
                .delete();

        Task<Void> followersTask = usersRef.document(target)
                .collection("followers")
                .document(follower)
                .delete();

        return Tasks.whenAll(followingTask, followersTask);
    }

    public Task<List<String>> getFollowing(String username) {
        return usersRef.document(username)
                .collection("following")
                .get()
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        return task.getResult()
                                .getDocuments()
                                .stream()
                                .map(DocumentSnapshot::getId)
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                });
    }
    public Task<DocumentReference> addMention(String moodId, String mentionedUser){
        Map<String, Object> mentionData = new HashMap<>();
        mentionData.put("moodId", moodId);
        mentionData.put("date", FieldValue.serverTimestamp());

        return usersRef.document(mentionedUser).collection("mentions")
                .add(mentionData);
    }

    public Task<List<String>> getMentions(String username) {
        return usersRef.document(username)
                .collection("mentions")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult()
                                .getDocuments()
                                .stream()
                                .map(DocumentSnapshot -> DocumentSnapshot.getString("moodId"))
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                });
    }

    public Task<QuerySnapshot> getAllUsers() {
        return usersRef.get();
    }
    public Task<Void> deleteMentions(String moodId, String username){
        return usersRef.document(username)
                .collection("mentions")
                .whereEqualTo("moodId", moodId)
                .get()
                .continueWithTask(query -> {
                    List<Task<Void>> deleteList = new ArrayList<>();
                    for (DocumentSnapshot document: query.getResult().getDocuments()){
                        deleteList.add(usersRef.document(username)
                                .collection("mentions")
                                .document(document.getId())
                                .delete());
                    }
                    return Tasks.whenAll(deleteList);
                });
    }
    public Task<Long> getMentionCount(String username){
        CollectionReference userRef = usersRef.document(username).collection("mentions");
        return userRef.count().get(AggregateSource.SERVER).continueWith(task -> {
            if (!task.isSuccessful()){
                throw task.getException();
            }
            return task.getResult().getCount();
        });
    }
}