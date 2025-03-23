package com.example.cmput301_team_project.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.model.Mood;

import java.util.ArrayList;

public abstract class BaseMoodListFragment extends Fragment {
    protected abstract void loadMoodData();
    protected abstract boolean isMoodOwned();

    protected MoodListAdapter moodListAdapter;

    @Override
    public void onResume() {
        super.onResume();
        // Refresh mood data when returning to the fragment
        loadMoodData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_mood_list, container, false);

        ListView moodListView = view.findViewById(R.id.mood_List);
        moodListAdapter = new MoodListAdapter(getContext(), new ArrayList<>(), this, isMoodOwned());
        moodListView.setAdapter(moodListAdapter);

        moodListView.setOnItemClickListener((parent, view1, position, id) -> {
            Mood selectedMood = (Mood) parent.getItemAtPosition(position);
            String moodId = selectedMood.getId();
            CommentListFragment.newInstance(moodId).show(requireActivity().getSupportFragmentManager(), "CommentListFragment");
        });

        setupUI(view);

        loadMoodData();
        return view;
    }

    // Override this method in a subclass to do some subclass-specific UI initialization
    protected void setupUI(View view) { }
}
