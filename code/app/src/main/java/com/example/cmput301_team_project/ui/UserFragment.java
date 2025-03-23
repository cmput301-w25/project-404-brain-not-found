package com.example.cmput301_team_project.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.model.Mood;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    private final UserDatabaseService userDatabaseService;
    private final MoodDatabaseService moodDatabaseService;

    public UserFragment() {
        userDatabaseService = UserDatabaseService.getInstance();
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static UserFragment newInstance() {

        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView userEmoji = view.findViewById(R.id.appUserEmoji);
        userEmoji.setVisibility(View.INVISIBLE);
        TextView displayName = view.findViewById(R.id.displayName);
        displayName.setVisibility(View.INVISIBLE);
        TextView username = view.findViewById(R.id.usernameDisplay);
        String currUser = FirebaseAuthenticationService.getInstance().getCurrentUser();
        username.setText(currUser);
        userDatabaseService.getDisplayName(currUser).addOnSuccessListener(name->{
            displayName.setText(name);
            displayName.setVisibility(View.VISIBLE);
        });

        moodDatabaseService.getMostRecentMood(currUser).addOnSuccessListener(emotion ->{
            if (emotion != null){
                Mood tempMood = Mood.createMood(MoodEmotionEnum.valueOf(emotion), null, null, false,null, null, null, null);
                userEmoji.setText(tempMood.getEmoji());
                userEmoji.setVisibility(View.VISIBLE);
            }else{
                userEmoji.setVisibility(View.VISIBLE);
            }
        });

        ImageButton logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuthenticationService.getInstance().logoutUser();
            Intent intent = new Intent(getContext(), LoginSignupActivity.class);
            requireContext().startActivity(intent);
            requireActivity().finish();
        });

        UserPagerAdapter userPagerAdapter = new UserPagerAdapter(this);
        TabLayout tabLayout = view.findViewById(R.id.user_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(userPagerAdapter.getItemCount());
        viewPager.setAdapter(userPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Requests"); break;
                case 1: tab.setText("People"); break;
                case 2: tab.setText("Followers"); break;
                case 3: tab.setText("Following"); break;
            }
        }).attach();
    }
}