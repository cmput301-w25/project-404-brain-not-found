package com.example.cmput301_team_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
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

    private final int MAX_TRIGGER_LENGTH = 20;
    private final int MAX_TRIGGER_WORDS = 3;
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
                // get the string input of trigger
                String inputtedTrigger = trigger.getText().toString();
                // invoke error if the length is less than max length
                if (!isValidTriggerLength(inputtedTrigger)) {
                    trigger.setError(String.format(getString(R.string.trigger_too_many_chars), MAX_TRIGGER_LENGTH));
                    return;
                }
                if (!isValidTriggerWordCount(inputtedTrigger)) {
                    trigger.setError(String.format(getString(R.string.trigger_too_many_words), MAX_TRIGGER_WORDS));
                    return;
                }
                Mood mood = Mood.createMood(MoodEmotionEnum.values()[emotion.getSelectedItemPosition()],
                        MoodSocialSituationEnum.values()[socialSituation.getSelectedItemPosition()],
                        inputtedTrigger,
                        SessionManager.getInstance().getCurrentUser(),
                        null,
                        imageViewToBase64(view.findViewById(R.id.mood_image_preview)));
                listener.addMood(mood);

                dialog.dismiss();
            });
        });

        return dialog;
    }
    /**
     * Checks if the trigger text is of valid length
     *
     * @param inputtedTrigger The trigger to be validated
     * @return {@code true} if the length of the trigger is less than {@code MAX_TRIGGER_LENGTH} otherwise {@code false}
     * */
    private boolean isValidTriggerLength(String inputtedTrigger) {
        return inputtedTrigger.length() <= MAX_TRIGGER_LENGTH;
    }
    /**
     * Checks if the trigger text is of valid word count
     *
     * @param inputtedTrigger The trigger to be validated
     * @return {@code true} if the length of the trigger is less than {@code MAX_TRIGGER_WORDS} otherwise {@code false}
     * */
    private boolean isValidTriggerWordCount(String inputtedTrigger) {
        // splits the trigger string into a list of words (separated by whitespace)
        String[] words = inputtedTrigger.trim().split("\\s+");
        return words.length <= MAX_TRIGGER_WORDS;
    }
    private void initializePhotoPicker(View view) {
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
                        Uri uri = (data != null && data.getData() != null) ? data.getData() : cameraImgUri;

                        Bitmap image;

                        try {
                            image = tryCompressImage(uri);
                            if(image == null)
                            {
                                setImageError(view, R.string.image_size_exceeded, View.VISIBLE);
                                return;
                            }
                        } catch (IOException e) {
                            setImageError(view, R.string.image_invalid, View.VISIBLE);
                            return;
                        }

                        setImageError(view, null, View.GONE);
                        preview.setImageBitmap(image);
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

    private void setImageError(View view, @Nullable Integer resId, int visibility) {
        TextView errorView = view.findViewById(R.id.image_error_msg);
        if(resId != null) {
            errorView.setText(resId);
        }
        errorView.setVisibility(visibility);
    }

    private Bitmap tryCompressImage(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);

        int quality = 100;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        do {
            byteArrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            quality -= 5;
        } while(quality > 10 && 4 * byteArrayOutputStream.toByteArray().length > 3 * MAX_IMAGE_SIZE);

        if(4 * byteArrayOutputStream.toByteArray().length > 3 * MAX_IMAGE_SIZE) {
            return null;
        }

        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);
    }

    private String imageViewToBase64(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if(drawable == null) {
            return null;
        }
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}