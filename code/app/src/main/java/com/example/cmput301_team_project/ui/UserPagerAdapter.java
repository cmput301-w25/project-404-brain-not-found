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
        switch (position) {
            case 1: return RequestsFragment.newInstance();
            case 2: return PeopleFragment.newInstance();
        };

        return PeopleFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
