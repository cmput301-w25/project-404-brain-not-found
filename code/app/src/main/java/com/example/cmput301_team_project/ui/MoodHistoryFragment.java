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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass for user mood history screen.
 * Responsible for user's mood history list.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends BaseMoodListFragment implements MoodFormFragment.MoodFormDialogListener,
                                                             MoodFilterFragment.MoodFilterDialogListener {
    public final MoodDatabaseService moodDatabaseService;
    public MoodListAdapter moodListAdapter;
    public ArrayList<Mood> moodList;

    // used for filtering, takes copies of the moodList to lower db reads and writes
    public ArrayList<Mood> filteredMoodList = new ArrayList<>();

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
        filteredMoodList = new ArrayList<>(moodList);
        moodListAdapter = new MoodListAdapter(getContext(), filteredMoodList, this, true);
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

                    filteredMoodList = new ArrayList<>(moodList);
                    // Notify adapter that data has changed
                    updateFilters(filteredMoodList);
                    moodListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle loading errors
                    Toast.makeText(getContext(), "Failed to load mood data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * uses the users moodList to create a new filteredList based on the users {@param emotion}
     * It then uses updateFilters to update the listview with the new filtered list
     */
    public void filterByEmotion(String emotion) {
        List<Mood> filteredMoodslist = moodList.stream()
                .filter(mood -> mood.getEmotion().toString().equalsIgnoreCase(emotion))
                .collect(Collectors.toList());
        ArrayList<Mood> filteredMoods = new ArrayList<>(filteredMoodslist);
        updateFilters(filteredMoods);

    }

    /**
     * uses the users moodList to create a new filteredList based on the users {@param time}
     * It then uses updateFilters to update the listview with the new filtered list
     */
    public void filterByTime(int time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, time);
        Date filterDate = calendar.getTime();

        List<Mood> filteredMoodslist = moodList.stream()
                .filter(mood -> mood.getDate().after(filterDate))
                .collect(Collectors.toList());
        ArrayList<Mood> filteredMoods = new ArrayList<>(filteredMoodslist);
        updateFilters(filteredMoods);

    }

    /**
     * uses the users moodList to create a new filteredList based on the users {@param text}
     * It then uses updateFilters to update the listview with the new filtered list
     */
    public void filterByText(String text) {
        List<Mood> filteredMoodslist = moodList.stream()
                .filter(mood -> mood.getTrigger().contains(text))
                .collect(Collectors.toList());
        ArrayList<Mood> filteredMoods = new ArrayList<>(filteredMoodslist);
        updateFilters(filteredMoods);

    }

    /**
     * Clears the ListView and replaces with all of the users inputted moods
     */
    public void resetFilters() {
        moodListAdapter.clear();
        filteredMoodList = new ArrayList<>(moodList);
        updateFilters(new ArrayList<>(filteredMoodList));
    }

    /**
     *  Clears the ListView and replaces with the {@param filteredMoods} based
     *  on what the inputted filter was
     */
    public void updateFilters(ArrayList<Mood> filteredMoods) {
        moodListAdapter.clear();
        moodListAdapter.addAll(filteredMoods);
        moodListAdapter.notifyDataSetChanged();
    }
}