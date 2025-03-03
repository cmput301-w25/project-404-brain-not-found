package com.example.cmput301_team_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Responsible for displaying the user's mood history list.
 */
public class MoodHistoryFragment extends Fragment implements MoodFormFragment.MoodFormDialogListener {
    private MoodDatabaseService moodDatabaseService;
    private ListView moodListView;
    private MoodListAdapter moodListAdapter;
    private ArrayList<Mood> moodList;
    private String currentUsername;

    // Constructor should not call newInstance
    public MoodHistoryFragment(String username) {
        this.currentUsername = username;
        moodDatabaseService = MoodDatabaseService.getInstance(username);
        moodList = new ArrayList<>();
    }

    /**
     * Factory method to create a new instance of MoodHistoryFragment.
     *
     * @param username The username of the current user.
     * @return A new instance of MoodHistoryFragment.
     */
    public static MoodHistoryFragment newInstance(String username) {
        MoodHistoryFragment fragment = new MoodHistoryFragment(username);
        Bundle args = new Bundle();
        args.putString("username", username); // Pass username as an argument
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUsername = getArguments().getString("username"); // Retrieve username from arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mood_history, container, false);
        moodListView = view.findViewById(R.id.Mood_List);
        moodListAdapter = new MoodListAdapter(getContext(), moodList);
        moodListView.setAdapter(moodListAdapter);

        ImageButton addMoodButton = view.findViewById(R.id.add_mood_button);
        addMoodButton.setOnClickListener(v -> {
            MoodFormFragment.newInstance().show(getChildFragmentManager(), "Add Mood Event");
        });
        // Handle movie list item click to edit a movie
        moodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new MoodDetailFragment(moodList.get(i)).show(getParentFragmentManager(), "Mood Event details");;
            }
        });

        loadMoodData();

        return view;
    }

    @Override
    public void addMood(Mood mood) {
        moodDatabaseService.addMood(mood);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh mood data when returning to the fragment
        loadMoodData();
    }

    /**
     * Loads mood data from Firestore database for the current user.
     */
    private void loadMoodData() {
        moodDatabaseService.getMoodList()
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

}