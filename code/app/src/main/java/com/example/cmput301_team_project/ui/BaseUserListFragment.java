package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    protected abstract Task<List<PublicUser>> loadDefaultData(int low, int high);

    protected UserDatabaseService userDatabaseService;

    @FunctionalInterface
    interface LoadDataWrapper {
        Task<List<PublicUser>> load(String currentUser, String query, int low, int high);
    }
    private final int BATCH_SIZE = 10;
    private UserListAdapter userAdapter;
    private LoadDataWrapper loadDataWrapper;
    private int low, high;

    protected BaseUserListFragment() {
        // empty protected constructor to be called by sub-classes
        low = 0;
        high = BATCH_SIZE;
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

        loadDataWrapper = (user, q, l, h) -> loadDefaultData(l, h);
        loadDataWrapper.load(FirebaseAuthenticationService.getInstance().getCurrentUser(), "", low, high)
                .addOnSuccessListener(res -> userAdapter.addAll(res));

        SearchView searchView = view.findViewById(R.id.user_search);
        searchView.setOnCloseListener(() -> {
            userAdapter.clear();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String currentUser = FirebaseAuthenticationService.getInstance().getCurrentUser();

                switch (getUserButtonAction()) {
                    case FOLLOW -> loadDataWrapper = (user, q, l, h) -> userDatabaseService.userSearch(user, q);
                    case UNFOLLOW -> loadDataWrapper = (user, q, l, h) -> userDatabaseService.userFollowingSearch(user, q);
                    case REMOVE -> loadDataWrapper = (user, q, l, h) -> userDatabaseService.userFollowersSearch(user, q);
                    default -> throw new IllegalStateException("Unexpected action: " + getUserButtonAction());
                }

                loadDataWrapper.load(currentUser, query, low, high).addOnSuccessListener(users -> userAdapter.replaceItems(users))
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

        Button showMoreButton = (Button) inflater.inflate(R.layout.user_search_footer, userList, false);
        showMoreButton.setOnClickListener(v -> {
            low += BATCH_SIZE;
            high += BATCH_SIZE;
            loadDataWrapper.load(FirebaseAuthenticationService.getInstance().getCurrentUser(), searchView.getQuery().toString(), low, high)
                    .addOnSuccessListener(users -> userAdapter.addAll(users));
        });
        userList.addFooterView(showMoreButton);

        return view;
    }

}