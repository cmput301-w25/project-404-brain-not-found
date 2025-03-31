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
import com.example.cmput301_team_project.db.BatchLoader;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


/**
 * A base fragment for displaying a list of users with search functionality.
 * Subclasses must specify the button text and action for user interactions.
 *
 * This fragment initializes a search view and a list view to display users.
 * Depending on the action type (FOLLOW, UNFOLLOW, REMOVE), it fetches relevant user data from the database.
 */
public abstract class BaseUserListFragment extends Fragment {
    protected abstract int getUserButtonTextId();
    protected abstract UserButtonActionEnum getUserButtonAction();
    protected abstract Task<List<PublicUser>> loadDefaultData(BatchLoader batchLoader);

    protected UserDatabaseService userDatabaseService;
    protected FirebaseAuthenticationService authService;
    private final int BATCH_SIZE = 10;
    private final BatchLoader batchLoader;
    private UserListAdapter userAdapter;
    private SearchView searchView;

    protected BaseUserListFragment() {
        // empty protected constructor to be called by sub-classes
        batchLoader = new BatchLoader(BATCH_SIZE);
    }

    /**
     * Initializes the UserDatabaseService instance when the fragment is created.
     *
     * @param savedInstanceState The saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDatabaseService = UserDatabaseService.getInstance();
        authService = FirebaseAuthenticationService.getInstance();
    }

    /**
     * Initializes the fragment's view, including the mood list and map button.
     * @param inflater  LayoutInflater to inflate the fragment layout
     * @param container ViewGroup container
     * @param savedInstanceState Saved instance state bundle
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_user_list, container, false);

        ListView userList = view.findViewById(R.id.user_list);
        userAdapter = new UserListAdapter(requireContext(), new ArrayList<>(), getString(getUserButtonTextId()), getUserButtonAction());
        userList.setAdapter(userAdapter);

        loadDefaultData(batchLoader).addOnSuccessListener(res -> userAdapter.addAll(res));


        userList.setOnItemClickListener((parent, view1, position, id) -> {
            PublicUser user = userAdapter.getItem(position);
            ViewProfileFragment.newInstance(user.getUsername(), user.getName()).show(requireActivity().getSupportFragmentManager(), "Profile");
        });

        Button showMoreButton = (Button) inflater.inflate(R.layout.user_search_footer, userList, false);
        showMoreButton.setOnClickListener(v -> {
            loadDefaultData(batchLoader)
                    .addOnSuccessListener(users -> {
                        userAdapter.addAll(users);
                        if(batchLoader.isAllLoaded()) {
                            showMoreButton.setVisibility(View.GONE);
                        }
                    });
        });
        userList.addFooterView(showMoreButton);

        searchView = view.findViewById(R.id.user_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String currentUser = authService.getCurrentUser();
                Task<List<PublicUser>> searchTask;

                switch (getUserButtonAction()) {
                    case FOLLOW -> searchTask = userDatabaseService.userSearch(currentUser, query);
                    case UNFOLLOW -> searchTask = userDatabaseService.userFollowingSearch(currentUser, query);
                    case REMOVE -> searchTask = userDatabaseService.userFollowersSearch(currentUser, query);
                    default -> throw new IllegalStateException("Unexpected action: " + getUserButtonAction());
                }

                showMoreButton.setVisibility(View.GONE);
                batchLoader.reset();

                searchTask.addOnSuccessListener(users -> userAdapter.replaceItems(users))
                        .addOnFailureListener(users -> userAdapter.clear());

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    batchLoader.reset();
                    loadDefaultData(batchLoader).addOnSuccessListener(res -> userAdapter.replaceItems(res));
                    showMoreButton.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(searchView.getQuery().toString().isEmpty()) {
            batchLoader.reset();
            loadDefaultData(batchLoader).addOnSuccessListener(res -> userAdapter.replaceItems(res));
        }
    }
}