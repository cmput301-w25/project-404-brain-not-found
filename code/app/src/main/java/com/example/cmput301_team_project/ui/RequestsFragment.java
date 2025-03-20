package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmput301_team_project.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    private RequestListAdapter requestListAdapter;
    private TextView noRequestsText;

    public RequestsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestsFragment.
     */
    public static RequestsFragment newInstance() {
        return new RequestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        noRequestsText = view.findViewById(R.id.no_requests);

        requestListAdapter = new RequestListAdapter(requireContext(), new ArrayList<>());
        ListView requestList = view.findViewById(R.id.request_list);
        requestList.setAdapter(requestListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestListAdapter.refreshRequests()
                .addOnSuccessListener(n -> {
                    if(n > 0) {
                        noRequestsText.setVisibility(View.GONE);
                    }
                    else {
                        noRequestsText.setVisibility(View.VISIBLE);
                    }
                });
    }
}