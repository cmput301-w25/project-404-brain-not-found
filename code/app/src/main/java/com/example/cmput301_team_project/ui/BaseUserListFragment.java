package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.enums.UserButtonAction;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseUserListFragment extends Fragment {
    protected abstract int getUserButtonTextId();
    protected abstract UserButtonAction getUserButtonAction();

    protected BaseUserListFragment() {
        // empty protected constructor to be called by sub-classes
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_user_list, container, false);

        ListView userList = view.findViewById(R.id.user_list);
        // TODO: Fetch users according to current search
        userList.setAdapter(new UserListAdapter(requireContext(), new ArrayList<>(), getString(getUserButtonTextId()), getUserButtonAction()));
        // Inflate the layout for this fragment
        return view;
    }
}