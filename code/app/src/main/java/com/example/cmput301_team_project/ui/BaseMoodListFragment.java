package com.example.cmput301_team_project.ui;

import static com.example.cmput301_team_project.utils.MapsUtils.getEmojiMarker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.model.Mood;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseMoodListFragment extends Fragment implements OnMapReadyCallback, MoodListAdapter.CommentButtonListener {
    protected abstract void loadMoodData();
    protected abstract boolean isMoodOwned();

    protected MoodListAdapter moodListAdapter;

    private final HashMap<Integer, Marker> markerMap = new HashMap<>();
    private int highlightedMarker = -1;
    private MapView mapView;
    private ListView moodListView;
    private FrameLayout mapContainer;
    private SwitchMaterial mapToggleButton;
    private boolean isMapView = false;
    private Bundle mapSavedInstanceState; // Store map state

    @Override
    public void onResume() {
        super.onResume();
        // Refresh mood data when returning to the fragment
        loadMoodData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mapSavedInstanceState = savedInstanceState; // Store for later use

        View view = inflater.inflate(R.layout.base_mood_list, container, false);

        // Initialize views
        moodListView = view.findViewById(R.id.mood_List);
        mapContainer = view.findViewById(R.id.mood_map_container);
        mapToggleButton = view.findViewById(R.id.mood_map_toggle_button);

        // Setup list adapter
        moodListAdapter = new MoodListAdapter(getContext(), new ArrayList<>(), this, isMoodOwned());
        moodListView.setAdapter(moodListAdapter);

        // Setup list item click listener for highlighting
        setupListItemClickListener();

        // Setup map toggle button
        mapToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                toggleMapView(mapSavedInstanceState);
            } else {
                toggleMapView(null);
            }
        });

        setupUI(view);
        loadMoodData();
        return view;
    }

    private void setupListItemClickListener() {
        moodListView.setOnItemClickListener((parent, view1, position, id) -> {
            // Existing marker highlighting logic
            if (highlightedMarker != -1) {
                Marker oldMarker = markerMap.get(highlightedMarker);
                if (oldMarker != null) {
                    Mood oldMood = moodListAdapter.getItem(highlightedMarker);
                    oldMarker.setIcon(getEmojiMarker(getString(oldMood.getEmoji()), getResources().getColor(oldMood.getColour(), requireContext().getTheme()), 100));
                }
            }

            if (position != highlightedMarker) {
                Marker marker = markerMap.get(position);
                if (marker != null) {
                    Mood mood = moodListAdapter.getItem(position);
                    highlightedMarker = position;
                    marker.setIcon(getEmojiMarker(getString(mood.getEmoji()), getResources().getColor(mood.getColour(), requireContext().getTheme()), 150));
                }
            } else {
                highlightedMarker = -1;
            }
        });
    }

    private void toggleMapView(@Nullable Bundle savedInstanceState) {
        isMapView = !isMapView;

        if (isMapView) {
            // Show map
            if (mapView == null) {
                mapView = new MapView(getContext());
                mapView.onCreate(savedInstanceState != null ? savedInstanceState : new Bundle()); // Ensure it's never null
                mapView.getMapAsync(this);
                mapContainer.addView(mapView);
            }

            moodListView.setVisibility(View.GONE);
            mapContainer.setVisibility(View.VISIBLE);

        } else {
            // Hide map, show list
            if (mapView != null) {
                mapContainer.removeView(mapView);
                mapView.onPause();
                mapView.onDestroy();

                // Save state before destroying the map
                mapSavedInstanceState = new Bundle();
                mapView.onSaveInstanceState(mapSavedInstanceState);

                mapView = null;
                markerMap.clear();
                highlightedMarker = -1;
            }

            moodListView.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCommentButtonClicked(int position) {
        Mood selectedMood = moodListAdapter.getItem(position);
        String moodId = selectedMood.getId();
        CommentListFragment.newInstance(moodId).show(requireActivity().getSupportFragmentManager(), "CommentListFragment");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < moodListAdapter.getCount(); i++) {
            Mood mood = moodListAdapter.getItem(i);
            if (mood != null && mood.getLocation() != null) {
                LatLng location = new LatLng(mood.getLocation().getLatitude(), mood.getLocation().getLongitude());
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(getEmojiMarker(getString(mood.getEmoji()), getResources().getColor(mood.getColour(), requireContext().getTheme()), 100))
                );
                marker.setTag(i);
                markerMap.put(i, marker);
                builder.include(location);
            }
        }

        map.setOnMarkerClickListener(marker -> {
            Integer position = (Integer) marker.getTag();
            if (position != null) {
                moodListView.smoothScrollToPosition(position);
            }
            return false;
        });

        int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.1);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
    }

    // Override this method in a subclass to do some subclass-specific UI initialization
    protected void setupUI(View view) { }
}
