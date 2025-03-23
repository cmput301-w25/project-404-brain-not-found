package com.example.cmput301_team_project.ui;

import static com.example.cmput301_team_project.utils.MapsUtils.getEmojiMarker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

    @Override
    public void onResume() {
        super.onResume();
        // Refresh mood data when returning to the fragment
        loadMoodData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_mood_list, container, false);

        moodListView = view.findViewById(R.id.mood_List);
        moodListAdapter = new MoodListAdapter(getContext(), new ArrayList<>(), this, isMoodOwned());
        moodListView.setAdapter(moodListAdapter);

        moodListView.setOnItemClickListener((parent, view1, position, id) -> {
            Marker marker = markerMap.get(position);
            if(marker != null) {
                Mood mood = moodListAdapter.getItem(position);
                int size;
                if(position == highlightedMarker) {
                    size = 100;
                    highlightedMarker = -1;
                }
                else {
                    size = 150;
                    highlightedMarker = position;
                }

                marker.setIcon(getEmojiMarker(getString(mood.getEmoji()), getResources().getColor(mood.getColour(), requireContext().getTheme()), size));
            }
        });

        ImageButton mapButton = view.findViewById(R.id.mood_map_button);
        FrameLayout mapContainer = view.findViewById(R.id.mood_map_container);
        mapButton.setOnClickListener(v -> {
            if(mapView == null) {
                mapView = new MapView(getContext());
                mapView.onCreate(savedInstanceState);
                mapView.getMapAsync(this);

                mapContainer.addView(mapView);
                mapContainer.setVisibility(View.VISIBLE);
            }
            else {
                mapContainer.setVisibility(View.GONE);
                mapContainer.removeView(mapView);

                markerMap.clear();

                mapView.onPause();
                mapView.onDestroy();
                mapView = null;
            }

        });

        setupUI(view);

        loadMoodData();
        return view;
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

        for(int i = 0; i < moodListAdapter.getCount(); i++) {
            Mood mood = moodListAdapter.getItem(i);
            if(mood != null && mood.getLocation() != null) {
                LatLng location = new LatLng(mood.getLocation().getLatitude(), mood.getLocation().getLongitude());
                Marker marker = map.addMarker(new MarkerOptions().position(location)
                        .icon(getEmojiMarker(getString(mood.getEmoji()), getResources().getColor(mood.getColour(), requireContext().getTheme()), 100)));
                marker.setTag(i);
                markerMap.put(i, marker);
                builder.include(location);
            }
        }

        map.setOnMarkerClickListener(marker -> {
            Integer position = (Integer) marker.getTag();

            if(position != null) {
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
