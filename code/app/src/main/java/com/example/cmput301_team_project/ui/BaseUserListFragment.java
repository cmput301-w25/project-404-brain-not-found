package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseUserListFragment extends Fragment {
    protected abstract int getUserButtonTextId();
    protected abstract UserButtonActionEnum getUserButtonAction();

    private UserListAdapter userAdapter;
    private UserDatabaseService userDatabaseService;

    protected BaseUserListFragment() {
        // empty protected constructor to be called by sub-classes
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDatabaseService = UserDatabaseService.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_user_list, container, false);

        ListView userList = view.findViewById(R.id.user_list);
        userAdapter = new UserListAdapter(requireContext(), new ArrayList<>(), getString(getUserButtonTextId()), getUserButtonAction());
        userList.setAdapter(userAdapter);

        SearchView searchView = view.findViewById(R.id.user_search);
        searchView.setOnCloseListener(() -> {
            userAdapter.clear();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String currentUser = FirebaseAuthenticationService.getInstance().getCurrentUser();
                Task<List<PublicUser>> searchTask;

                switch (getUserButtonAction()) {
                    case FOLLOW -> searchTask = userDatabaseService.userSearch(currentUser, query);
                    case UNFOLLOW -> searchTask = userDatabaseService.userFollowingSearch(currentUser, query);
                    case REMOVE -> searchTask = userDatabaseService.userFollowersSearch(currentUser, query);
                    default -> throw new IllegalStateException("Unexpected action: " + getUserButtonAction());
                }

                searchTask.addOnSuccessListener(users -> userAdapter.replaceItems(users))
                        .addOnFailureListener(users -> userAdapter.clear());

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        userList.setOnItemClickListener((parent, view1, position, id) -> {
            PublicUser user = userAdapter.getItem(position);
            ViewProfileFragment.newInstance(user.getUsername(), user.getName()).show(requireActivity().getSupportFragmentManager(), "Profile");
        });
        return view;
    }

}