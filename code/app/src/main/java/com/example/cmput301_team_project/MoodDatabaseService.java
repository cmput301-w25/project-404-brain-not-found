package com.example.cmput301_team_project;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Calendar;

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

    public interface OnMoodFetchedListener {
        void onMoodsFetched(ArrayList<Mood> moods);
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


    /**
     *  Gets the users posted moods and then filters by the requested {@param emotion}. It returns
     *  the filtered ArrayList via the {@param listener} to then use it to display the new
     *  filtered moods.
     */
    public void filterByEmotion(String emotion, OnMoodFetchedListener listener) {
        getMoodList().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
              List<Mood> allMoods = task.getResult(); // get all users moods
              List<Mood> filteredMoods = allMoods.stream() // filter based on emotion
                      .filter(mood -> mood.getEmotion().toString().equalsIgnoreCase(emotion))
                      .collect(Collectors.toList());

              listener.onMoodsFetched(new ArrayList<>(filteredMoods)); // return to the original call with new list
            } else {
                Log.e("MoodDatabaseService", "error filtering moods by emotion: ", task.getException());
                listener.onMoodsFetched(new ArrayList<>()); // empty list returned on failure
            }
        });
    }

    /**
     * Gets the users posted moods and then filters by the requested {@param time}. It returns the
     * filtered ArrayList via the {@param listener} to then use it to display the new filtered moods.
     */
    public void filterByTime(int time, OnMoodFetchedListener listener) {
        getMoodList().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Date currentDate = new Date();
                // get instance of calendar to find requested filtered Date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_MONTH, time); // time is a negative number here
                Date requestedDate = calendar.getTime();

                List<Mood> allMoods = task.getResult();
                List<Mood> filteredMoods = allMoods.stream()
                        .filter(mood -> mood.getDate().compareTo(requestedDate) >= 0) // not sure about this
                        .collect(Collectors.toList());
                listener.onMoodsFetched(new ArrayList<>(filteredMoods));
            } else {
                Log.e("MoodDatabaseService", "Error filtering moods by time " + time, task.getException());
                listener.onMoodsFetched(new ArrayList<>());
            }
        });
    }

    /**
     * Gets the users posted moods and then filters by the requested {@param text}. It then sorts
     * the filtered moods based on text matches and accuracy. It returns the filtered ArrayList via
     * the {@param listener} to then use it to display the new filtered moods.
     */
    public void filterByText(String[] text, OnMoodFetchedListener listener) {
        getMoodList().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Mood> allMoods = task.getResult();
                List<Mood> filteredMoods = new ArrayList<>();

                Map<Mood, Integer> textMatchCount = new HashMap<>();
                Set<String> searchText = new HashSet<>(Arrays.asList(text));

                // loop thru all words searched and all user's mood triggers
                for (Mood mood: allMoods) {
                    int matches = 0;
                    // make set of the triggers words for faster lookup (O(1)) instead of linear search (O(n))
                    Set<String> moodWords = new HashSet<>(Arrays.asList(mood.getTrigger().toLowerCase().split("[,\\s]+")));

                    for (String word : searchText) {
                        if (moodWords.contains(word)) {
                            matches++;
                        }
                    }
                    if (matches > 0) {
                        textMatchCount.put(mood, matches);
                    }
                }
                // sort the filtered moods based on highest/most accurate match count
                filteredMoods = textMatchCount.entrySet().stream()
                        .sorted((e1, e2) -> {
                            // compare number of matches in the array
                            int compareMatches = Integer.compare(e2.getValue(), e1.getValue());

                            if (compareMatches != 0) {
                                return compareMatches;
                            }
                            // secondary sort, sorts to find more close/exact matches
                            int totalWords1 = e1.getKey().getTrigger().split("[,\\s]+").length;
                            int totalWords2 = e2.getKey().getTrigger().split("[,\\s]+").length;

                            return Integer.compare(totalWords1, totalWords2);
                        })
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                listener.onMoodsFetched(new ArrayList<>(filteredMoods));
            } else {
                Log.e("MoodDatabaseService", "Error filtering moods by text: " + Arrays.toString(text), task.getException());
                listener.onMoodsFetched(new ArrayList<>());
            }
        });
    }
}