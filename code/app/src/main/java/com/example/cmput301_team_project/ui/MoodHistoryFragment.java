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
import com.example.cmput301_team_project.model.Comment;
import com.example.cmput301_team_project.model.Mood;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass for user mood history screen.
 * Responsible for user's mood history list.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends BaseMoodListFragment implements MoodFormFragment.MoodFormDialogListener {
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

        moodListView.setOnItemClickListener((parent, view1, position, id) -> {
            Mood selectedMood = (Mood) parent.getItemAtPosition(position);
            String moodId = selectedMood.getId();
            CommentListFragment.newInstance(moodId).show(requireActivity().getSupportFragmentManager(), "CommentListFragment");

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


}