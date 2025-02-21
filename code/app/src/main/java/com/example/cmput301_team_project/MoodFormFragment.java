package com.example.cmput301_team_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link MoodFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodFormFragment extends DialogFragment {

    public MoodFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoodFormFragment.
     */
    public static MoodFormFragment newInstance() {
        return new MoodFormFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_mood_form, null);

        Spinner emotion = view.findViewById(R.id.form_emotion);
        Spinner socialSituation = view.findViewById(R.id.form_situation);
        EditText trigger = view.findViewById(R.id.form_trigger);

        HintDropdownAdapter emotionAdapter = new HintDropdownAdapter(getActivity(), new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.emotional_states))));
        HintDropdownAdapter socialSituationAdapter = new HintDropdownAdapter(getActivity(), new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.social_situations))));

        emotion.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        emotion.setAdapter(emotionAdapter);

        socialSituation.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        socialSituation.setAdapter(socialSituationAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Add Mood Event")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", (dialog, which) -> {

                })
                .create();
    }
}