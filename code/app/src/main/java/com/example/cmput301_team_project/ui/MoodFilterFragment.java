package com.example.cmput301_team_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.model.MoodFilterState;
import com.example.cmput301_team_project.utils.LocationPermissionManager;
import com.example.cmput301_team_project.utils.PlacesUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class MoodFilterFragment extends DialogFragment {

    // filters for time (-x days)
    private final int FILTER_BY_DAY = -1;
    private final int FILTER_BY_WEEK = -7;
    private final int FILTER_BY_MONTH = -30;
    private LocationPermissionManager locationPermissionManager;
    private Integer time;
    private MoodEmotionEnum emotion;
    private String text;

    public MoodFilterFragment() {

    }

    interface MoodFilterDialogListener {
        void updateFilter(MoodFilterState moodFilterState);
    }
    private MoodFilterDialogListener listener;

    public static MoodFilterFragment newInstance(MoodFilterState moodFilterState) {
        Bundle args = new Bundle();
        MoodFilterFragment fragment = new MoodFilterFragment();
        args.putSerializable("moodFilterState", moodFilterState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof MoodFilterFragment.MoodFilterDialogListener) {
            listener = (MoodFilterFragment.MoodFilterDialogListener) parentFragment;
        }
        else {
            throw new RuntimeException(parentFragment + " must implement MoodFilterDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_mood_filter, null);

        Spinner emotionFilter = view.findViewById(R.id.filter_by_mood_spinner);
        RadioButton lastDayFilter = view.findViewById(R.id.filter_by_day);
        RadioButton lastWeekFilter = view.findViewById(R.id.filter_by_week);
        RadioButton lastMonthFilter = view.findViewById(R.id.filter_by_month);
        EditText triggerFilter = view.findViewById(R.id.filter_by_text);
        MaterialSwitch locationFilter = view.findViewById(R.id.location_filter);

        locationPermissionManager = new LocationPermissionManager(this, () -> locationFilter.setChecked(true));

        locationFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && !locationPermissionManager.isPermissionGranted()) {
                locationFilter.setChecked(false);
                locationPermissionManager.requestPermission();
            }
        });

        HintDropdownAdapter emotionAdapter = new HintDropdownAdapter(getContext(), new ArrayList<>(Arrays.asList(MoodEmotionEnum.values())));

        emotionFilter.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        emotionFilter.setAdapter(emotionAdapter);

        MoodFilterState moodFilterState = (MoodFilterState) getArguments().getSerializable("moodFilterState");

        if(moodFilterState != null) {
            if (moodFilterState.emotion() != null) {
                emotionFilter.setSelection(emotionAdapter.getPosition(moodFilterState.emotion().getDropdownDisplayName(requireContext())));
            }
            if(moodFilterState.time() != null) {
                Integer time = moodFilterState.time();
                if(time == FILTER_BY_DAY) {
                    lastDayFilter.setChecked(true);
                }
                else if(time == FILTER_BY_WEEK) {
                    lastWeekFilter.setChecked(true);
                }
                else if(time == FILTER_BY_MONTH){
                    lastMonthFilter.setChecked(true);
                }
            }
            if(moodFilterState.text() != null) {
                triggerFilter.setText(moodFilterState.text());
            }
            if(moodFilterState.location() != null) {
                locationFilter.setChecked(true);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setTitle("Filter Moods")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Set Filter", null)
                .setNeutralButton("Reset Filters", null)
                .create();

        dialog.setOnShowListener(dialog1 -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                time = null;
                emotion = null;
                text = null;

                if (emotionFilter.getSelectedItemPosition() != 0) {
                    emotion = MoodEmotionEnum.values()[emotionFilter.getSelectedItemPosition()];
                }

                if (lastDayFilter.isChecked()) {
                    time = FILTER_BY_DAY;
                }
                else if (lastWeekFilter.isChecked()) {
                    time = FILTER_BY_WEEK;
                }
                else if (lastMonthFilter.isChecked()) {
                    time = FILTER_BY_MONTH;
                }

                String triggerFilterInput = triggerFilter.getText().toString();
                if (!triggerFilterInput.isEmpty()) {
                    text = triggerFilterInput;
                }

                if(locationFilter.isChecked()) {
                    PlacesUtils.getLastLocation(getContext())
                            .addOnSuccessListener(location -> {
                                listener.updateFilter(new MoodFilterState(time, emotion, text, new GeoPoint(location.getLatitude(), location.getLongitude())));
                                dialog.dismiss();
                            });
                }
                else {
                    listener.updateFilter(new MoodFilterState(time, emotion, text, null));
                    dialog.dismiss();
                }

            });
            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(v -> {
                listener.updateFilter(MoodFilterState.getEmptyFilterState());
                dialog.dismiss();
            });
        });
        return dialog;
    }
}
