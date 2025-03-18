package com.example.cmput301_team_project.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserPagerAdapter extends FragmentStateAdapter {
    public UserPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> RequestsFragment.newInstance();
            case 2 -> FollowersFragment.newInstance();
            case 3 -> FollowingFragment.newInstance();
            default -> PeopleFragment.newInstance();
        };
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
