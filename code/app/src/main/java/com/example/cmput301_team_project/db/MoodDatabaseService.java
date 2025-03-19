package com.example.cmput301_team_project.db;

import android.util.Log;

import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Singleton class to manage mood-related operations with the firestore database
 * Handles all mood-related queries
 */
public class MoodDatabaseService extends BaseDatabaseService {
    private static MoodDatabaseService instance = null;
    private final CollectionReference moodsRef;

    private MoodDatabaseService() {
        super();

        moodsRef = db.collection("moods");
    }

    private MoodDatabaseService(FirebaseFirestore db) {
        super(db);

        moodsRef = db.collection("moods");
    }

    public static MoodDatabaseService getInstance() {
        if (instance == null) {
            instance = new MoodDatabaseService();
        }
        return instance;
    }

    public static void setInstanceForTesting(FirebaseFirestore db) {
        instance = new MoodDatabaseService(db);
    }

    public void addMood(Mood mood) {
        moodsRef.add(mood);
    }


    /**Find the mood event in database with doc ID and delete it
     * @param mood The mood event in history to be deleted.*/
    public void deleteMood(Mood mood){
        moodsRef.document(mood.getId()).delete();
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
    /**this is the method to get all the documents in the moods collection*/
    public Task<List<Mood>> getMoodList() {
        //Log.d("username", SessionManager.getInstance().getCurrentUser());
        return moodsRef
                .whereEqualTo("author", SessionManager.getInstance().getCurrentUser())
                .orderBy("date", Query.Direction.DESCENDING)
                .get().continueWith(task -> {
            List<Mood> moodList = new ArrayList<>();

            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    try {
                        // Safely extract fields with null checks
                        Object authorObj = doc.get("author");
                        String author = (authorObj != null) ? authorObj.toString() : "Guest";

                        Object imageObj = doc.get("imageBase64");
                        String imageBase64 = (imageObj != null) ? imageObj.toString() : "";

                        Object situationObj = doc.get("socialSituation");
                        String socialSituationString = (situationObj != null) ? situationObj.toString() : "";

                        MoodSocialSituationEnum socialSituation = MoodSocialSituationEnum.NONE;
                        // Safely converting the socialSituation string to the enum
                        try {
                            if (socialSituationString != null && !socialSituationString.isEmpty()) {
                                socialSituation = MoodSocialSituationEnum.valueOf(socialSituationString.toUpperCase());
                            }
                        } catch (IllegalArgumentException e) {
                            // Keep the default NONE value
                        }

                        Object triggerObj = doc.get("trigger");
                        String trigger = (triggerObj != null) ? triggerObj.toString() : "";

                        // Safely get date with fallback to current date
                        Date date = new Date();
                        Timestamp timestamp = doc.getTimestamp("date");
                        if (timestamp != null) {
                            date = timestamp.toDate();
                        }

                        // Safely get emotion with default to prevent crashes
                        Object emotionObj = doc.get("emotion");
                        String emotionString = (emotionObj != null) ? emotionObj.toString().toLowerCase() : "";

                        MoodEmotionEnum emotion = MoodEmotionEnum.NONE;
                        // Safely converting the socialSituation string to the enum
                        try {
                            if (emotionString != null && !emotionString.isEmpty()) {
                                emotion = MoodEmotionEnum.valueOf(emotionString.toUpperCase());
                            }
                        } catch (IllegalArgumentException e) {
                            // Keep the default NONE value
                        }
                        // Creating mood objects based on the emotion
                        Mood mood = Mood.createMood(emotion, socialSituation, trigger, author, date, imageBase64);
                        if (mood != null) {
                            mood.setId(doc.getId()); // Set the Firestore document ID
                            moodList.add(mood);
                        }
                    } catch (Exception e) {
                        // Log the exception but continue processing other documents
                        Log.e("MoodDatabaseService", "Error processing document: " + doc.getId(), e);
                    }
                }
            } else if (task.getException() != null) {
                // Log the task exception
                Log.e("MoodDatabaseService", "Error getting documents: ", task.getException());
            }

            return moodList;
        });
    }


    public void updateMood(Mood mood) {
        moodsRef.document(mood.getId()).set(mood);
    }
}