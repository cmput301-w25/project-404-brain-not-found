package com.example.cmput301_team_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A {@link DialogFragment} subclass that implements form for adding and editing moods
 * Use the {@link MoodFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodFormFragment extends DialogFragment {
    private final int MAX_IMAGE_SIZE = 65536;

    interface MoodFormDialogListener {
        void addMood(Mood mood);
    }
    private MoodFormDialogListener listener;

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

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof MoodFormDialogListener) {
            listener = (MoodFormDialogListener) parentFragment;
        }
        else {
            throw new RuntimeException(parentFragment + " must implement MoodFormDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_mood_form, null);

        initializePhotoPicker(view);
        Spinner emotion = view.findViewById(R.id.form_emotion);
        Spinner socialSituation = view.findViewById(R.id.form_situation);
        EditText trigger = view.findViewById(R.id.form_trigger);

        HintDropdownAdapter emotionAdapter = new HintDropdownAdapter(getContext(), new ArrayList<>(Arrays.asList(MoodEmotionEnum.values())));
        HintDropdownAdapter socialSituationAdapter = new HintDropdownAdapter(getContext(), new ArrayList<>(Arrays.asList(MoodSocialSituationEnum.values())));

        emotion.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        emotion.setAdapter(emotionAdapter);

        socialSituation.setOnItemSelectedListener(new HintDropdownItemSelectedListener());
        socialSituation.setAdapter(socialSituationAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setTitle("Add Mood Event")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)
                .create();

        dialog.setOnShowListener(dialog1 -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if(emotion.getSelectedItemPosition() == 0) {
                    emotionAdapter.setError(emotion.getSelectedView(), getString(R.string.no_emotional_state_error_msg));
                    return;
                }

                Mood mood = Mood.createMood(MoodEmotionEnum.values()[emotion.getSelectedItemPosition()],
                        MoodSocialSituationEnum.values()[socialSituation.getSelectedItemPosition()],
                        trigger.getText().toString(),
                        null);
                listener.addMood(mood);

                dialog.dismiss();
            });
        });

        return dialog;
    }

    private void initializePhotoPicker(View view)
    {
        ImageView preview = view.findViewById(R.id.mood_image_preview);
        ImageButton removePreview = view.findViewById(R.id.remove_preview);

        File cameraFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_" + System.currentTimeMillis() + ".jpg");
        Uri cameraImgUri = FileProvider.getUriForFile(requireContext(), "com.example.cmput301_team_project.fileprovider", cameraFile);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();
                        Uri uri;
                        if(data != null && data.getData() != null) {
                            // user selected an image from gallery
                            uri = data.getData();
                        }
                        else {
                            // user captured an image on camera
                            uri = cameraImgUri;
                        }

                        try {
                            if(!validateImage(uri))
                            {
                                setImageError(view, R.string.image_size_exceeded, View.VISIBLE);
                                return;
                            }
                        } catch (IOException e) {
                            setImageError(view, R.string.image_invalid, View.VISIBLE);
                            return;
                        }

                        setImageError(view, null, View.GONE);
                        preview.setImageURI(uri);
                        preview.setVisibility(View.VISIBLE);
                        removePreview.setVisibility(View.VISIBLE);
                    }
                }
        );

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImgUri);

        Intent chooser = Intent.createChooser(galleryIntent, "Select Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{ cameraIntent });

        MaterialButton addImageButton = view.findViewById(R.id.add_image);
        addImageButton.setOnClickListener(v -> activityResultLauncher.launch(chooser));

        removePreview.setOnClickListener(v -> {
            preview.setVisibility(View.GONE);
            removePreview.setVisibility(View.GONE);
            preview.setImageURI(null);
        });
    }

    private void setImageError(View view, @Nullable Integer resId, int visibility)
    {
        TextView errorView = view.findViewById(R.id.image_error_msg);
        if(resId != null) {
            errorView.setText(resId);
        }
        errorView.setVisibility(visibility);
    }

    private boolean validateImage(Uri uri) throws IOException {
        long fileSize = Long.MAX_VALUE;
        AssetFileDescriptor fileDescriptor = requireContext().getContentResolver().openAssetFileDescriptor(uri , "r");

        if(fileDescriptor != null) {
            fileSize = fileDescriptor.getLength();
            fileDescriptor.close();
        }

        return fileSize <= MAX_IMAGE_SIZE;
    }
}