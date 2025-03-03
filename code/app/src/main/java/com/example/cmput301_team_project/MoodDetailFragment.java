package com.example.cmput301_team_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * MoodDetailFragment is a dialog fragment that allows users to view the details about the mood.
 * It can also be used in view-only mode to display details without editing capability.
 */
public class MoodDetailFragment extends DialogFragment {

    /**
     * UI elements for mood details display
     */
    private TextView emotionName;
    private TextView dateAns;
    private TextView triggerName;
    private TextView SocialSituation_name;
    private TextView ImageBase64name;

    // Interface for communication with the parent activity
    private OnFragmentInteractionListener listener;

    // Model object to display
    private Mood mood;

    /**
     * Constructor to set the mood object to display
     * @param mood The mood object containing details to display
     */
    public MoodDetailFragment(Mood mood) {
        this.mood = mood;
    }

    /**
     * Interface for communication with the parent activity
     */
    public interface OnFragmentInteractionListener {
        void onOkPressed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mood_details, null);

        // Initialize UI elements
        emotionName = view.findViewById(R.id.emotionName);
        dateAns = view.findViewById(R.id.dateAns);
        triggerName = view.findViewById(R.id.triggerName);
        SocialSituation_name = view.findViewById(R.id.SocialSituation_name);
        ImageBase64name = view.findViewById(R.id.ImageBase64name);

        // Populate UI with mood data
        populateUI();

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Mood Details")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Just close the dialog
                        if (listener != null) {
                            listener.onOkPressed();
                        }
                    }
                })
                .create();
    }

    /**
     * Populates the UI elements with mood data
     */
    private void populateUI() {
        if (mood != null) {
            emotionName.setText(mood.getEmotion().toString());
            dateAns.setText(mood.getDate().toString());

            // Check if trigger is null or empty before setting
            String trigger = mood.getTrigger();
            if (trigger != null && !trigger.isEmpty()) {
                triggerName.setText(trigger);
            } else {
                triggerName.setText("No trigger specified");
            }

            // Check if social situation is null or empty before setting
            String socialSituation = mood.getSocialSituation().toString();
            if (socialSituation != null && !socialSituation.isEmpty()) {
                SocialSituation_name.setText(socialSituation);
            } else {
                SocialSituation_name.setText("No social situation specified");
            }

            // Check if image exists before setting
            String imageBase64 = mood.getImageBase64();
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                ImageBase64name.setText("Image available");
            } else {
                ImageBase64name.setText("No image");
            }
        }
    }
}