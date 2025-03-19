package com.example.cmput301_team_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class MoodFilterFragment extends DialogFragment {

    public MoodDatabaseService moodDatabaseService;

    public MoodFilterFragment() {
        this.moodDatabaseService = MoodDatabaseService.getInstance();
    }

    interface MoodFilterDialogListener {

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

        Spinner filterByMood = view.findViewById(R.id.filter_by_mood);

        HintDropdownAdapter emotionAdapter = new HintDropdownAdapter(getContext(), new ArrayList<>(Arrays.asList(MoodEmotionEnum.values())));

        filterByMood.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        filterByMood.setAdapter(emotionAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setTitle("Filter Moods")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Set Filter", null)
                .create();

        dialog.setOnShowListener(dialog1 -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if (filterByMood.getSelectedItemPosition() == 0)
                    System.out.println("fart");
                dialog.dismiss();
            });
        });
        return dialog;
    }
}
