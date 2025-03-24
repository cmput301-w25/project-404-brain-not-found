package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.SessionManager;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.model.Mood;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass for user mood history screen.
 * Responsible for user's mood history list.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends BaseMoodListFragment implements MoodFormFragment.MoodFormDialogListener,
                                                             MoodFilterFragment.MoodFilterDialogListener {
    private final MoodDatabaseService moodDatabaseService;
    private MoodListAdapter moodListAdapter;
    private ArrayList<Mood> moodList;


    public MoodHistoryFragment() {
        moodDatabaseService = MoodDatabaseService.getInstance();
        moodList = new ArrayList<>();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoodHistoryFragment.
     */
    public static MoodHistoryFragment newInstance() {
        return new MoodHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mood_history, container, false);
        ListView moodListView = view.findViewById(R.id.mood_List);
        moodListAdapter = new MoodListAdapter(getContext(), moodList, this, true);
        moodListView.setAdapter(moodListAdapter);

        ImageButton addMoodButton = view.findViewById(R.id.add_mood_button);
        addMoodButton.setOnClickListener(v -> {
            MoodFormFragment.newInstance(null).show(getChildFragmentManager(), "Add Mood Event");
        });

        ImageButton filterMoodButton = view.findViewById(R.id.filter_button);
        filterMoodButton.setOnClickListener( v -> {
            MoodFilterFragment.newInstance().show(getChildFragmentManager(), "Filter Moods");
        });
        loadMoodData();
        return view;
    }

    @Override
    public void addMood(Mood mood) {
        moodDatabaseService.addMood(mood);
        loadMoodData();
    }

    @Override
    public void replaceMood(Mood newMood) {
        moodDatabaseService.updateMood(newMood);
        loadMoodData();
    }

    /**
     * Loads mood data from Firestore database
     */
    protected void loadMoodData() {
        moodDatabaseService.getMoodList(SessionManager.getInstance().getCurrentUser())
                .addOnSuccessListener(moods -> {
                    // Clear existing list and add new data
                    moodList.clear();
                    moodList.addAll(moods);

                    // Notify adapter that data has changed
                    moodListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle loading errors
                    Toast.makeText(getContext(), "Failed to load mood data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * calls {@link MoodDatabaseService} filterByEmotion() to make the database query
     * to filter based on {@param emotion}. It then uses updateFilters to update
     * the listview with the new filtered list
     */
    public void filterByEmotion(String emotion) {
        moodDatabaseService.filterByEmotion(SessionManager.getInstance().getCurrentUser(), emotion, this::updateFilters);// calls updateFilters after
    }

    /**
     * calls {@link MoodDatabaseService} filterByTime() to make the database query
     * to filter based on {@param time}. It then uses updateFilters to update
     * the listview with the new filtered list
     */
    public void filterByTime(int time) {
        moodDatabaseService.filterByTime(SessionManager.getInstance().getCurrentUser(), time, this::updateFilters);
    }

    /**
     * calls {@link MoodDatabaseService} filterByText() to make the database query
     * to filter based on moods that include {@param text}, via splitting into an array
     * based on spaces and commas. It then uses updateFilters to update the listview with
     * the new filtered list
     */
    public void filterByText(String text) {
        System.out.println("Filtering by text:" + text);
        String[] textArray = text.split("[,\\s]+");
        moodDatabaseService.filterByText(SessionManager.getInstance().getCurrentUser(), textArray, this::updateFilters);
    }

    /**
     * Clears the ListView and replaces with all of the users inputted moods
     */
    public void resetFilters() {
        moodListAdapter.clear();
        loadMoodData();
    }

    /**
     *  Clears the ListView and replaces with the {@param filteredMoods} based
     *  on what the inputted filter was
     */
    public void updateFilters(ArrayList<Mood> filteredMoods) {
        Log.d("updateFilters", "Received " + filteredMoods.size() + " moods.");
        moodListAdapter.clear();
        moodListAdapter.addAll(filteredMoods);
        moodListAdapter.notifyDataSetChanged();
    }
}