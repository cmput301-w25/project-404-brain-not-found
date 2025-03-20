package com.example.cmput301_team_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class MoodFilterFragment extends DialogFragment {

    // filters for time (-x days)
    private final int FILTER_BY_DAY = -1;
    private final int FILTER_BY_WEEK = -7;
    private final int FILTER_BY_MONTH = -30;

    public MoodDatabaseService moodDatabaseService;

    public MoodFilterFragment() {
        this.moodDatabaseService = MoodDatabaseService.getInstance();
    }

    interface MoodFilterDialogListener {
        void filterByEmotion(String emotion);
        void filterByTime(int time);
        void filterByText(String text);
        void resetFilters();
    }
    private MoodFilterDialogListener listener;

    public static MoodFilterFragment newInstance() {
        return new MoodFilterFragment();
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

        Spinner emotionFilter = view.findViewById(R.id.filter_by_mood);
        RadioButton lastDayFilter = view.findViewById(R.id.filter_by_day);
        RadioButton lastWeekFilter = view.findViewById(R.id.filter_by_week);
        RadioButton lastMonthFilter = view.findViewById(R.id.filter_by_month);
        EditText triggerFilter = view.findViewById(R.id.filter_by_text);

        HintDropdownAdapter emotionAdapter = new HintDropdownAdapter(getContext(), new ArrayList<>(Arrays.asList(MoodEmotionEnum.values())));

        emotionFilter.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        emotionFilter.setAdapter(emotionAdapter);

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
                if (emotionFilter.getSelectedItemPosition() != 0) {
                    listener.filterByEmotion(emotionFilter.getSelectedItem().toString().toUpperCase()); // method to be used in classes where filtering is needed
                }
//              TODO: add filters for time, trigger text, and find way to make them work together if possible
                if (lastDayFilter.isChecked()) {
                    System.out.println("filter : show moods from past 24 hrs");
                    listener.filterByTime(FILTER_BY_DAY);
                }
                else if (lastWeekFilter.isChecked()) {
                    listener.filterByTime(FILTER_BY_WEEK);
                }
                else if (lastMonthFilter.isChecked()) {
                    listener.filterByTime(FILTER_BY_MONTH);
                }

                String triggerFilterInput = triggerFilter.getText().toString();
                if (!triggerFilterInput.isEmpty()) {
                    listener.filterByText(triggerFilterInput);
                }
                dialog.dismiss();
            });
            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(v -> {
                listener.resetFilters();
                dialog.dismiss();
            });
        });
        return dialog;
    }
}
