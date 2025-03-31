package com.example.cmput301_team_project.db;

import android.util.Log;

import com.example.cmput301_team_project.enums.FollowRelationshipEnum;
import com.example.cmput301_team_project.model.AppUser;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

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
 * the UserDatabaseService singleton class includes methods for making queries on the "users"
 * collection in the firestore database.
 *
 * Provides methods on logging in, signing out, and registering users, as well as relational
 * data from one user to another such as following, searching, etc.
 */
public class UserDatabaseService extends BaseDatabaseService {
    private static UserDatabaseService instance = null;
    private final CollectionReference usersRef;
    private final CollectionReference mentionsRef;

    {
        usersRef = db.collection("users");
        mentionsRef = db.collection("mentions");
    }

    private UserDatabaseService() {
        super();
    }

    private UserDatabaseService(FirebaseFirestore db, Executor executor) {
        super(db, executor);
    }

    /**
     * Gets/creates an instance of the UserDatabaseService.
     *
     * @return The instances of the UserDatabaseService.
     */
    public static UserDatabaseService getInstance() {
        if (instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }

    /**
     * Sets up a custom Firebase db and executor for testing purposes. Overrides the singleton
     * instance of UserDatabaseService.
     *
     * @param db The Firestore instance to be used.
     * @param executor The Executor to handle the Firestore operations during the tests.
     */
    public static void setInstanceForTesting(FirebaseFirestore db, Executor executor) {
        instance = new UserDatabaseService(db, executor);
    }

    /**
     * Adds a user to the firebase db.
     *
     * @param user The AppUser to be added to the db.
     * @return A void Task if the addition to the db is successful, or an exception if unsuccessful.
     */
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
                        updates.put("followerCount", 0);
                        return uref.update(updates);
                    }
                    Tasks.forException(task.getException());
                    return Tasks.forResult(Void.TYPE.newInstance());
                });
    }

    /**
     * Gets the users display name.
     *
     * @param username The username of the user.
     * @return A Task containing a String with the user's display name if successful, otherwise an
     * exception is thrown.
     */
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
     * @param username      The username of the user.
     * @param inputPassword The password entered by the user.
     * @return A {@link Task} that resolves to {@code true} if the password matches, otherwise {@code false}.
     */
    public Task<Boolean> validateCredentials(String username, String inputPassword)
    {
        return usersRef.document(username).get()
                .continueWithTask(taskExecutor, task -> {
                    String email = task.getResult().getString("email");
                    return FirebaseAuthenticationService.getInstance().loginUser(email, inputPassword);
                })
                .continueWith(taskExecutor, task -> {
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
     * @param field         field to search in
     * @param query         query to search for
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

    /**
     * Gets the count of all accounts following the user.
     *
     * @param username The username of the user.
     * @return A Task containing the count of the user's followers if successful, otherwise an
     * exception is thrown.
     */
    public Task<Long> followerCount(String username){
        CollectionReference userRef = usersRef.document(username).collection("followers");
        return userRef.count().get(AggregateSource.SERVER).continueWith(task -> {
            if (!task.isSuccessful()){
                throw task.getException();
            }
            return task.getResult().getCount();
        });
    }

    /**
     * Gets the count of accounts the user is following.
     *
     * @param username The username of the user.
     * @return A task containing the count of accounts the user is following, otherwise an
     * exception is thrown.
     */
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
     * @param currentUser   username of the user making the search
     * @param query         search query
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
     * @param username      username of the user
     * @param collection    nested collection name
     * @param query         search query
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
                .continueWithTask(voidTask -> {
                    updateFollowerCount(target, true);
                    return removeRequest(follower, target);
                });
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


        return Tasks.whenAll(followingTask, followersTask).continueWithTask(voidTask -> {
            updateFollowerCount(target, false);
            return voidTask;
        });
    }

    public void updateFollowerCount(String username, boolean increment) {
        DocumentReference userRef = usersRef.document(username);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            Long currentCount = snapshot.getLong("followerCount");

            if (currentCount == null) {
                currentCount = 0L;
            }

            long newCount = increment ? currentCount + 1 : Math.max(currentCount - 1, 0);

            transaction.update(userRef, "followerCount", newCount);
            return null;
        });
    }

    public Task<List<PublicUser>> getFollowing(String username, BatchLoader batchLoader) {
        CollectionReference followingRef = usersRef.document(username).collection("following");
        Task<QuerySnapshot> followingTask;

        if(batchLoader != null) {
            followingTask = batchLoader.getNextBatchQuery(followingRef).get();
        }
        else {
            followingTask = followingRef.get();
        }
        return followingTask.continueWith(task -> {
            if(task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                if(batchLoader != null) {
                    batchLoader.nextBatch(documents);
                }
                return documents.stream()
                        .map(document -> new PublicUser(document.getId(), document.getString("name")))
                        .collect(Collectors.toList());
            }

            return new ArrayList<>();
        });
    }

    public Task<List<PublicUser>> getFollowers(String username, BatchLoader batchLoader) {
        CollectionReference followersRef = usersRef.document(username).collection("followers");
        Task<QuerySnapshot> followersTask;
        if(batchLoader != null) {
            followersTask = batchLoader.getNextBatchQuery(followersRef).get();
        }
        else {
            followersTask = followersRef.get();
        }
        return followersTask.continueWith(task -> {
           if(task.isSuccessful()) {
               List<DocumentSnapshot> documents = task.getResult().getDocuments();

               if(batchLoader != null) {
                   batchLoader.nextBatch(documents);
               }

               return documents.stream()
                       .map(document -> new PublicUser(document.getId(), document.getString("name")))
                       .collect(Collectors.toList());
           }

           return new ArrayList<>();
        });
    }
    /**
     * Gets the list of accounts the users is following.
     *
     * @return A Task containing a list of users that the user is following, otherwise an empty
     * ArrayList is returned.
     */
    public Task<List<PublicUser>> getMostFollowedUsers(String currentUser, BatchLoader batchLoader) {
        Query query = usersRef.orderBy("followerCount", Query.Direction.DESCENDING).whereNotEqualTo(FieldPath.documentId(), currentUser);

        if(batchLoader != null) {
            query = batchLoader.getNextBatchQuery(query);
        }

        return query.get()
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        if(batchLoader != null) {
                            batchLoader.nextBatch(documents);
                        }

                        return documents.stream()
                                .map(document -> new PublicUser(document.getId(), document.getString("name")))
                                .collect(Collectors.toList());
                    }

                    return new ArrayList<>();
                });
    }

    public Task<DocumentReference> addMention(String moodId, String mentionedUser){
        Map<String, Object> mentionData = new HashMap<>();
        mentionData.put("moodId", moodId);
        mentionData.put("date", FieldValue.serverTimestamp());
        mentionData.put("mentionedUser", mentionedUser);

        return mentionsRef.add(mentionData);
    }

    public Task<List<String>> getMentions(String username) {
        return mentionsRef
                .whereEqualTo("mentionedUser", username)
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
                    else {
                        Log.d("Mentions", "Error getting mentions: " + task.getException());
                    }
                    return new ArrayList<>();
                });
    }

    public Task<Void> deleteMentions(String moodId, String username){
        Query query = mentionsRef
                .whereEqualTo("moodId", moodId);

        if(username != null) {
            query = query.whereEqualTo("mentionedUser", username);
        }

        return query.get()
                .continueWithTask(task -> {
                    List<Task<Void>> deleteList = new ArrayList<>();
                    for (DocumentSnapshot document: task.getResult().getDocuments()){
                        deleteList.add(document.getReference().delete());
                    }
                    return Tasks.whenAll(deleteList);
                });
    }
    public Task<Long> getMentionCount(String username) {
        return mentionsRef.whereEqualTo("mentionedUser", username).count().get(AggregateSource.SERVER).continueWith(task -> {
            if (!task.isSuccessful()){
                throw task.getException();
            }
            return task.getResult().getCount();
        });
    }
}