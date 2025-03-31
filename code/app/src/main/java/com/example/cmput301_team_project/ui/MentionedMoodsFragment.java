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

/**
 * The MentionedMoodsFragment class extends the {@link BaseMoodListFragment} but shows the list of
 * Moods where the user is mentioned in. It updates the list with new mentions and removes already
 * seen Moods.
 */
public class MentionedMoodsFragment extends BaseMoodListFragment {
    private final MoodDatabaseService moodDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final Set<Mood> seenItems = new HashSet<>();
    private MentionsListener mentionsListener;

    /**
     * Listener interface to notify when a mentioned Mood is viewed.
     */
    interface MentionsListener{
        void mentionsDeleted();
    }

    /**
     * Constructor for a new {@code MentionedMoodsFragment}
     *
     * @param mentionsListener A listener to notify when a mentioned mood is deleted.
     */
    public MentionedMoodsFragment(MentionsListener mentionsListener) {
        moodDatabaseService = MoodDatabaseService.getInstance();
        userDatabaseService = UserDatabaseService.getInstance();
        this.mentionsListener = mentionsListener;
    }

    /**
     * Creates a new instance of {@code MentionedMoodsFragment}
     *
     * @param mentionsListener A listener to notify when a mentioned mood is deleted.
     * @return the newly created fragment.
     */
    public static MentionedMoodsFragment newInstance(MentionsListener mentionsListener) {
        return new MentionedMoodsFragment(mentionsListener);
    }


    /**
     * Loads the Moods where the user is mentioned.
     * It fetches the mentions from the {@link UserDatabaseService} and retrieves the respective
     * Moods with the mentions from the {@link MoodDatabaseService}.
     */
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

    /**
     * Sets up the scroll listener for the moodList when the fragment is created.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState The saved state of the fragment.
     */
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

    /**
     * Deletes the mentioned Mood from the view after the user has viewed it.
     * Removes the mentions associated with the seen moods from the db.
     *
     * @return A Task representing the completion of the deletion process.
     */
    public Task<Void> deleteViewedMention(){
        List<Task<Void>> deleteList = new ArrayList<>();
        for (Mood mood: seenItems){
            String moodId = mood.getId();
            deleteList.add(userDatabaseService.deleteMentions(moodId, FirebaseAuthenticationService.getInstance().getCurrentUser()));
        }
        return Tasks.whenAll(deleteList);
    }

    /**
     * called when the fragment is paused and deletes the viewed mentions, notifying the listener.
     */
    @Override
    public void onPause(){
        super.onPause();
        deleteViewedMention().addOnSuccessListener(result ->{
            mentionsListener.mentionsDeleted();
        });

    }

    /**
     * Shows if the displayed Moods are created/owned by the user.
     *
     * @return {@code false} since the mentioned Moods are not owned by the user.
     */
    @Override
    protected boolean isMoodOwned() {
        return false;
    }
}
