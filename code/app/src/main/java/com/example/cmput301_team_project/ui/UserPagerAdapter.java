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

        };
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
