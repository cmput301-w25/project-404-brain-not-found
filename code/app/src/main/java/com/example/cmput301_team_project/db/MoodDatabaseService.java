package com.example.cmput301_team_project.db;

import android.util.Log;

import com.example.cmput301_team_project.model.Comment;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;

/**
 * Singleton class to manage mood-related operations with the firestore database
 * Handles all mood-related queries
 */
public class MoodDatabaseService extends BaseDatabaseService {
    private static MoodDatabaseService instance = null;
    private final CollectionReference moodsRef;

    private UserDatabaseService userDatabaseService = UserDatabaseService.getInstance();

    private MoodDatabaseService() {
        super();

        moodsRef = db.collection("moods");
    }

    private MoodDatabaseService(FirebaseFirestore db, Executor executor) {
        super(db, executor);

        moodsRef = db.collection("moods");
    }

    public static MoodDatabaseService getInstance() {
        if (instance == null) {
            instance = new MoodDatabaseService();
        }
        return instance;
    }

    public static void setInstanceForTesting(FirebaseFirestore db, Executor executor) {
        instance = new MoodDatabaseService(db, executor);
    }

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

        userDatabaseService.getAllUsers().addOnSuccessListener(userDocs -> {
            List<Task<Void>> deleteTasks = new ArrayList<>();

            for (DocumentSnapshot userDoc : userDocs.getDocuments()) {
                CollectionReference mentionsRef = userDoc.getReference().collection("mentions");
                Task<QuerySnapshot> mentionQuery = mentionsRef.whereEqualTo("moodId", mood.getId()).get();

                mentionQuery.addOnSuccessListener(mentionsSnapshot -> {
                    for (DocumentSnapshot mentionDoc : mentionsSnapshot.getDocuments()) {
                        deleteTasks.add(mentionDoc.getReference().delete());
                    }
                });
            }

            Tasks.whenAll(deleteTasks).addOnSuccessListener(aVoid -> {
                moodDocRef.delete().addOnSuccessListener(aVoid2 -> {
                    System.out.println("Mood and all related mentions deleted successfully.");
                }).addOnFailureListener(e -> {
                    System.err.println("Failed to delete mood: " + e.getMessage());
                });
            }).addOnFailureListener(e -> {
                System.err.println("Failed to delete mentions: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            System.err.println("Failed to retrieve user documents: " + e.getMessage());
        });
    }

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

    /**this is the method to get all the documents in the moods collection*/
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


    public Task<DocumentReference> addComment(String moodId, Comment comment){
        comment.setTimestamp(Timestamp.now());
        return moodsRef.document(moodId).collection("comments")
                .add(comment);
    }

    public Task<List<Comment>> getComments(String moodId){
        return moodsRef.document(moodId)
                .collection("comments")
                .orderBy("date", Query.Direction.DESCENDING)
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

    public Task<List<Mood>> getMentionedMoods(List<String> moodIds) {
        List<Task<DocumentSnapshot>> fetchTasks = new ArrayList<>();
        for(String moodId : moodIds) {
            fetchTasks.add(moodsRef.document(moodId).get());
        }

        return Tasks.whenAllSuccess(fetchTasks)
                .continueWith(result -> {
                    if(result.isSuccessful()) {
                        Set<Mood> moodList = new HashSet<>();
                        for(Object res : result.getResult()) {
                            if(res instanceof DocumentSnapshot documentSnapshot) {
                                moodList.add(moodFromDoc(documentSnapshot));

                            }
                        }
                        return new ArrayList<>(moodList);
                    }
                    return new ArrayList<>();
                });
    }

    public void updateMood(Mood mood) {
        moodsRef.document(mood.getId()).set(mood);
    }

    public Task<Boolean> isPublic(String moodId){
        return moodsRef.document(moodId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        return documentSnapshot.getBoolean("public");
                    }
                    return false;
                });
    }

}