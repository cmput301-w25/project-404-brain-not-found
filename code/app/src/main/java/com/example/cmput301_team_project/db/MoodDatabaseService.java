package com.example.cmput301_team_project.db;

import android.util.Log;

import com.example.cmput301_team_project.SessionManager;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Singleton class to manage mood-related operations with the firestore database
 * Handles all mood-related queries
 */
public class MoodDatabaseService extends BaseDatabaseService {
    private static MoodDatabaseService instance = null;
    public CollectionReference moodsRef;

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

    public Mood moodFromDoc(DocumentSnapshot document) {
        Mood mood = Mood.createMood(MoodEmotionEnum.valueOf(document.getString("emotion")),
                MoodSocialSituationEnum.valueOf(document.getString("socialSituation")),
                document.getString("trigger"),
                Boolean.TRUE.equals(document.getBoolean("public")),
                document.getString("author"),
                document.getDate("date"),
                document.getString("imageBase64"));
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


    public void updateMood(Mood mood) {
        moodsRef.document(mood.getId()).set(mood);
    }

    /**
     *  Gets the users posted moods and then filters by the requested {@param emotion}. It returns
     *  the filtered ArrayList via the {@param listener} to then use it to display the new
     *  filtered moods.
     */
    public void filterByEmotion(String username, String emotion, OnMoodFetchedListener listener) {

        if (username == null || username.isEmpty()) {
            Log.e("filterByEmotion", "Username is null or empty. Cannot filter.");
            return;
        }

        TaskCompletionSource<List<Mood>> taskCompletionSource = new TaskCompletionSource<>();

        moodsRef.whereEqualTo("author", username)
                .whereEqualTo("emotion", emotion)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Mood> filteredMoods = new ArrayList<>();
                        System.out.println("Query Successful");
                        for (DocumentSnapshot doc : task.getResult()) {
                            Mood mood = moodFromDoc(doc);
                            filteredMoods.add(mood);
                        }
                        taskCompletionSource.setResult(filteredMoods);
                        listener.onMoodsFetched(new ArrayList<>(filteredMoods));
                    } else {
                        Log.e("MoodDatabaseService", "Error filtering by mood: " + emotion, task.getException());
                        listener.onMoodsFetched(new ArrayList<>());
                    }
                });
    }

    /**
     * Gets the users posted moods and then filters by the requested {@param time}. It returns the
     * filtered ArrayList via the {@param listener} to then use it to display the new filtered moods.
     */
    public void filterByTime(String username, int time, OnMoodFetchedListener listener) {

        if (username == null || username.isEmpty()) {
            Log.e("filterByEmotion", "Username is null or empty. Cannot filter.");
            return;
        }

        TaskCompletionSource<List<Mood>> taskCompletionSource = new TaskCompletionSource<>();

        Date currentDate = new Date();
        // get instance of calendar to find requested filtered Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, time); // time is a negative number here
        Date requestedDate = calendar.getTime();

        moodsRef.whereEqualTo("author", username)
                .whereGreaterThan("date", requestedDate )
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Mood> filteredMoods = new ArrayList<>();
                        System.out.println("Query Successful");
                        for (DocumentSnapshot doc : task.getResult()) {
                            Mood mood = moodFromDoc(doc);
                            filteredMoods.add(mood);
                        }
                        taskCompletionSource.setResult(filteredMoods);
                        listener.onMoodsFetched(new ArrayList<>(filteredMoods));
                    } else {
                        Log.e("MoodDatabaseService", "Error filtering by Date " + time, task.getException());
                        listener.onMoodsFetched(new ArrayList<>());
                    }
                });
    }

    /**
     * Gets the users posted moods and then filters by the requested {@param text}. It then sorts
     * the filtered moods based on text matches and accuracy. It returns the filtered ArrayList via
     * the {@param listener} to then use it to display the new filtered moods.
     */
    public void filterByText(String username, String[] text, OnMoodFetchedListener listener) {
        getMoodList(username).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Mood> allMoods = task.getResult();
                List<Mood> filteredMoods;

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