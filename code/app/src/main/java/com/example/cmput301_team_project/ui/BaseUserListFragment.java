package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.UserButtonAction;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseUserListFragment extends Fragment {
    protected abstract int getUserButtonTextId();
    protected abstract UserButtonAction getUserButtonAction();

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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: this should be different for each case depending on button action after we implement following
                userDatabaseService.userSearch(query).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        userAdapter.replaceItems(task.getResult());
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return view;
    }
}