package com.example.cmput301_team_project.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.model.Mood;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MentionedMoodsFragment extends BaseMoodListFragment {
    private final MoodDatabaseService moodDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final Set<Mood> seenItems = new HashSet<>();
    private MentionsListener mentionsListener;
    interface MentionsListener{
        void mentionsDeleted();
    }


    public MentionedMoodsFragment(MentionsListener mentionsListener) {
        moodDatabaseService = MoodDatabaseService.getInstance();
        userDatabaseService = UserDatabaseService.getInstance();
        this.mentionsListener = mentionsListener;
    }

    public static MentionedMoodsFragment newInstance(MentionsListener mentionsListener) {
        return new MentionedMoodsFragment(mentionsListener);
    }


    @Override
    protected void loadMoodData() {
        userDatabaseService.getMentions(FirebaseAuthenticationService.getInstance().getCurrentUser())
                .addOnSuccessListener(mentions -> moodDatabaseService.getMentionedMoods(mentions, moodFilterState)
                        .addOnSuccessListener(moods -> {
                            moodListAdapter.clear();
                            moodListAdapter.addAll(moods);
                            moodListAdapter.notifyDataSetChanged();
                        }));
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ListView mentionList = view.findViewById(R.id.mood_List);

        mentionList.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
                for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++){
                    seenItems.add(moodListAdapter.getItem(i));
                }
            }
        });
    }

    public Task<Void> deleteViewedMention(){
        List<Task<Void>> deleteList = new ArrayList<>();
        for (Mood mood: seenItems){
            String moodId = mood.getId();
            deleteList.add(userDatabaseService.deleteMentions(moodId, FirebaseAuthenticationService.getInstance().getCurrentUser()));
        }
        return Tasks.whenAll(deleteList);
    }
    @Override
    public void onPause(){
        super.onPause();
        deleteViewedMention().addOnSuccessListener(result ->{
            mentionsListener.mentionsDeleted();
        });

    }
    @Override
    protected boolean isMoodOwned() {
        return false;
    }
}
