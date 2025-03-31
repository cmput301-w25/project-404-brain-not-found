package com.example.cmput301_team_project.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter class for managing fragments in the ViewPager2 component.
 * This adapter handles four fragments: Requests, People, Followers, and Following.
 */
public class UserPagerAdapter extends FragmentStateAdapter {
    public UserPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Creates and returns the fragment corresponding to the given position.
     *
     * @param position The position of the fragment in the ViewPager.
     * @return The corresponding fragment instance.
     */
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

    /**
     * Returns the total number of fragments managed by this adapter.
     *
     * @return The number of fragments (4 in this case).
     */
    @Override
    public int getItemCount() {
        return 4;
    }
}
