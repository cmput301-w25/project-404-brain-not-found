package com.example.cmput301_team_project.db;

import android.util.Log;

import com.example.cmput301_team_project.model.Comment;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;

/**
 * the MoodDatabaseService singleton class includes methods for making queries on the "moods"
 * collection in the firestore database.
 *
 * Provides methods on posting, editing, deleting, and getting Moods, as well as getting, and
 * posting comments on a users Mood post.
 */
public class MoodDatabaseService extends BaseDatabaseService {
    private static MoodDatabaseService instance = null;
    private final CollectionReference moodsRef;

    private MoodDatabaseService() {
        super();

        moodsRef = db.collection("moods");
    }

    private MoodDatabaseService(FirebaseFirestore db, Executor executor) {
        super(db, executor);

        moodsRef = db.collection("moods");
    }

    /**
     * returns the instance and/or creates a new instance if needed of the MoodDatabaseService.
     *
     * @return the instance of the MoodDatabaseService.
     */
    public static MoodDatabaseService getInstance() {
        if (instance == null) {
            instance = new MoodDatabaseService();
        }
        return instance;
    }

    /**
     * Sets up a custom Firebase db and executor for testing purposes. Overrides the singleton
     * instance of MoodDatabaseService.
     *
     * @param db The Firestore instance to be used.
     * @param executor The Executor to handle the Firestore operations during the tests.
     */
    public static void setInstanceForTesting(FirebaseFirestore db, Executor executor) {
        instance = new MoodDatabaseService(db, executor);
    }

    /**
     * Adds a Mood to the "moods" CollectionReference
     *
     * @param mood The Mood to be added.
     */
    public void addMood(Mood mood) {
        moodsRef.add(mood);
    }


    /**Find the mood event in database with doc ID and delete it
     * @param mood The mood event in history to be deleted.*/
    public void deleteMood(Mood mood){
        DocumentReference moodDocRef = moodsRef.document(mood.getId());
        moodDocRef.collection("comments").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                document.getReference().delete();
            }});
        moodsRef.document(mood.getId()).delete();

    }

    /**
     * Returns the users most recently posted moods from the database.
     * @param username The username of the user whose mood is being retrieved.
     * @return A Task containing a String representing the emotion of the
     * users most recently posted mood. If no mood is found, the Task returns null.
     * @throws Exception if the db query fails.
     */
    public Task<String> getMostRecentMood(String username){
        return moodsRef
                .whereEqualTo("author", username)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get().continueWith(task ->{
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    QuerySnapshot snap =  task.getResult();
                    if (snap == null || snap.isEmpty()){
                        return null;
                    }
                    DocumentSnapshot document = snap.getDocuments().get(0);
                    return document.getString("emotion");
                });
    }

    /**
     * Gets the users mood from the Firestore db document.
     * @param document The document representing the mood.
     * @return A Mood object based on the one from the document.
     */
    private Mood moodFromDoc(DocumentSnapshot document) {
        Mood mood = Mood.createMood(MoodEmotionEnum.valueOf(document.getString("emotion")),
                MoodSocialSituationEnum.valueOf(document.getString("socialSituation")),
                document.getString("trigger"),
                Boolean.TRUE.equals(document.getBoolean("public")),
                document.getString("author"),
                document.getDate("date"),
                document.getString("imageBase64"),
                document.getGeoPoint("location"));
        mood.setId(document.getId());
        return mood;
    }

    /**
     * Gets all of the user's moods they have posted in the db.
     * @param username The username of the user whose moods are being retrieved.
     * @return A Task containing a List of Moods that the user has posted.
     * If the query fails then a null List is returned and the error is logged.
     */
    public Task<List<Mood>> getMoodList(String username) {
        return moodsRef
                .whereEqualTo("author", username)
                .orderBy("date", Query.Direction.DESCENDING)
                .get().continueWith(task -> {
            List<Mood> moodList = new ArrayList<>();

            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    moodList.add(moodFromDoc(doc));
                }
            } else if (task.getException() != null) {
                // Log the task exception
                Log.e("MoodDatabaseService", "Error getting documents: ", task.getException());
            }

            return moodList;
        });
    }

    /**
     * Gets the three(3) most recent public Moods from users that the current user is following
     * @param following A List of Strings representing usernames that the current user is following
     * @return A Task containing a List of Moods from the followed users, sorted by recent date posted.
     */
    public Task<List<Mood>> getFollowingMoods(List<String> following) {

        List<Task<QuerySnapshot>> fetchTasks = new ArrayList<>();
        for(String username : following) {
            fetchTasks.add(moodsRef.orderBy("date", Query.Direction.DESCENDING)
                            .whereEqualTo("author", username)
                            .whereEqualTo("public", true)
                            .limit(3)
                            .get());
        }

        return Tasks.whenAllSuccess(fetchTasks)
                .continueWith(result -> {
                    if(result.isSuccessful()) {
                        List<Mood> moodList = new ArrayList<>();
                        for(Object res : result.getResult()) {
                            if(res instanceof QuerySnapshot querySnapshot) {
                                for(DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    moodList.add(moodFromDoc(document));
                                }
                            }
                        }

                        moodList.sort((m1, m2) -> m2.getDate().compareTo(m1.getDate()));
                        return moodList;
                    }
                    return new ArrayList<>();
                });
    }

    /**
     * Adds a comment to the moods 'comments' collection.
     * @param moodId The id of the Mood being commented on.
     * @param comment The comment object being added to the Mood's collection
     * @return A Task containing the DocumentReference to the newly added Comment. If the operation
     * fails, an exception is added to the Task.
     */
    public Task<DocumentReference> addComment(String moodId, Comment comment){
        return moodsRef.document(moodId).collection("comments")
                .add(comment);
    }

    /**
     * Gets the comments of the Mood post.
     * @param moodId The id of the Mood with the comments.
     * @return A Task containing a List of the Comments that the Moods has in its
     * "comments" collection. If the query fails, then a null List is returned.
     */
    public Task<List<Comment>> getComments(String moodId){
        Log.d("Firestore", "getComments() called for moodId: " + moodId);
        return moodsRef.document(moodId)
                .collection("comments")
                .get()
                .continueWith(task ->{
                    if(task.isSuccessful()){
                        return task.getResult().getDocuments()
                                .stream()
                                .map(document -> new Comment(document.getString("username"), document.getString("text")))
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                });
    }

    /**
     * Updates the Mood document with new information Using its MoodId
     * @param mood The Mood with the new data
     */
    public void updateMood(Mood mood) {
        moodsRef.document(mood.getId()).set(mood);
    }
}