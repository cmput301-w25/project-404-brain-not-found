package com.example.cmput301_team_project;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    public static MoodDatabaseService getInstance() {
        if (instance == null) {
            instance = new MoodDatabaseService();
        }
        return instance;
    }

    public void addMood(Mood mood) {
        moodsRef.add(mood);
    }

    // this is the method to get all the documents in the moods collection
    public Task<List<Mood>> getMoodList() {
        return moodsRef.get().continueWith(task -> {
            List<Mood> moodList = new ArrayList<>();

            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    try {
                        // Safely extract fields with null checks
                        Object authorObj = doc.get("author");
                        String author = (authorObj != null) ? authorObj.toString() : "Unknown";

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
                        String emotion = (emotionObj != null) ? emotionObj.toString().toLowerCase() : "";

                        // Creating mood objects based on the emotion
                        Mood mood = null;

                        switch (emotion) {
                            case "anger":
                                mood = new MoodAnger(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "confusion":
                                mood = new MoodConfusion(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "disgust":
                                mood = new MoodDisgust(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "fear":
                                mood = new MoodFear(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "happiness":
                                mood = new MoodHappiness(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "sadness":
                                mood = new MoodSadness(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "shame":
                                mood = new MoodShame(socialSituation, trigger, author, date, imageBase64);
                                break;
                            case "surprise":
                                mood = new MoodSurprise(socialSituation, trigger, author, date, imageBase64);
                                break;
                            default:
                                // Log unknown emotion and skip this entry
                                Log.w("MoodDatabaseService", "Unknown emotion type: " + emotion);
                                continue;
                        }

                        if (mood != null) {
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

}