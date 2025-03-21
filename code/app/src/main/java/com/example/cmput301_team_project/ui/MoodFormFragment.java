package com.example.cmput301_team_project.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.cmput301_team_project.BuildConfig;
import com.example.cmput301_team_project.ui.HintDropdownAdapter;
import com.example.cmput301_team_project.ui.HintDropdownItemSelectedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.SessionManager;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.utils.ImageUtils;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A {@link DialogFragment} subclass that implements form for adding and editing moods
 * Use the {@link MoodFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodFormFragment extends DialogFragment {
    private final int MAX_IMAGE_SIZE = 65536;
    private final int MAX_TRIGGER_LENGTH = 200;

    private boolean isEditMode = false; // Flag to check if we're editing
    private Mood moodBeingEdited = null; // Reference to the mood being edited

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1; //to automatically show the locations or compelte it

    private MoodDatabaseService moodDatabaseService;

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;

    interface MoodFormDialogListener {
        void addMood(Mood mood);
        void replaceMood(Mood newMood);
    }
    private MoodFormDialogListener listener;

    public MoodFormFragment() {
        this.moodDatabaseService = MoodDatabaseService.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoodFormFragment.
     */
    public static MoodFormFragment newInstance(@Nullable Mood moodToEdit) {
        Bundle args = new Bundle();
        MoodFormFragment fragment = new MoodFormFragment();
        if (moodToEdit != null) {
            args.putSerializable("mood", moodToEdit);
            fragment.isEditMode = true; // Set edit mode
            fragment.moodBeingEdited = moodToEdit; // Store reference to existing mood
        }
        fragment.setArguments(args);
        return fragment;
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
        Switch locationAcess = view.findViewById(R.id.Get_current_location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationAcess.setOnCheckedChangeListener(((buttonView, isChecked) -> {
             if (isChecked){
                    getLastLocation(view);
             }
        }));

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.API_KEY);
        }


        if (isEditMode) {
            Mood editedMood = (Mood) getArguments().getSerializable("mood");
            ImageView moodImagePreview = view.findViewById(R.id.mood_image_preview);

            //display old mood's emotion
            MoodEmotionEnum oldEmotion = editedMood.getEmotion();
            emotion.setSelection(emotionAdapter.getPosition(oldEmotion.getDropdownDisplayName(getContext())));

            //display old moods social situation
            MoodSocialSituationEnum oldSituation = editedMood.getSocialSituation();
            socialSituation.setSelection(socialSituationAdapter.getPosition((oldSituation.getDropdownDisplayName(getContext()))));

            //display old moods trigger
            trigger.setText(editedMood.getTrigger());

            //display image
            if (editedMood.getImageBase64() != null && !editedMood.getImageBase64().isEmpty()) {
                moodImagePreview.setImageBitmap(ImageUtils.decodeBase64(editedMood.getImageBase64()));
                moodImagePreview.setVisibility(View.VISIBLE);
                ImageButton removePreviewButton = view.findViewById(R.id.remove_preview);
                removePreviewButton.setVisibility(View.VISIBLE);
            }
        }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            AlertDialog dialog = builder
                    .setView(view)
                    .setTitle(isEditMode ? "Edit Mood Event" :"Add Mood Event")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton(isEditMode ? "Save" : "Add", null)
                    .create();

        dialog.setOnShowListener(dialog1 -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if (emotion.getSelectedItemPosition() == 0) {
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

                //if (!isValidTriggerWordCount(inputtedTrigger)) {
                //    trigger.setError(String.format(getString(R.string.trigger_too_many_words), MAX_TRIGGER_WORDS));
                //    return;
                //}

                MoodEmotionEnum selectedEmotion = MoodEmotionEnum.values()[emotion.getSelectedItemPosition()];

                if (isEditMode) {
                    // New Emotion
                    Mood newMood = Mood.createMood(
                            selectedEmotion, // New Emotion
                            MoodSocialSituationEnum.values()[socialSituation.getSelectedItemPosition()],
                            trigger.getText().toString(),
                            moodBeingEdited.getAuthor(),
                            moodBeingEdited.getDate(),
                            imageViewToBase64(view.findViewById(R.id.mood_image_preview)
                            ), null
                    );

                    newMood.setId(moodBeingEdited.getId()); // Preserve Firestore document ID
                    listener.replaceMood(newMood); // Notify Adapter

                } else {
                    // Add a new mood event
                    Mood newMood = Mood.createMood(
                            selectedEmotion,
                            MoodSocialSituationEnum.values()[socialSituation.getSelectedItemPosition()],
                            inputtedTrigger,
                            SessionManager.getInstance().getCurrentUser(),
                            null,
                            imageViewToBase64(view.findViewById(R.id.mood_image_preview)
                            ), null);
                    listener.addMood(newMood);
                }
                dialog.dismiss();
            });
        });

        return dialog;
    }
    private void getLastLocation(View view) {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location using FusedLocationProviderClient
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Get the latitude and longitude
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Use Geocoder to reverse geocode if needed (optional)
                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                new Thread(() -> {
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        if (addresses != null && !addresses.isEmpty()) {
                                            String address = addresses.get(0).getAddressLine(0);  // Full address or you can extract parts like city, etc.
                                            // Update UI on the main thread
                                            getActivity().runOnUiThread(() -> {
                                                TextView locationAddress = view.findViewById(R.id.current_location);
                                                //Log.e("address", address);
                                                locationAddress.setText(address);

                                                // Create a GeoPoint (use your custom GeoPoint class or LatLng from Google Maps)
                                                GeoPoint geoPoint = new GeoPoint(latitude, longitude);  // Or LatLng
                                            });
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e("Geocoder", "Unable to get address", e);
                                    }
                                }).start();
                            }
                        }
                    });
        } else {
            // If permission is not granted, request permissions
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);  // Request code, you can customize it
        }
    }


    private void startAutocomplete() {
        // Set the fields to specify which types of place data to return
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        // Start the autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void handleSelectedPlace(Place place) {
        // Update UI with the selected place details
        TextView locationAddress = getView().findViewById(R.id.current_location);
        locationAddress.setText(place.getAddress());

        // You can also get the latitude and longitude
        LatLng latLng = place.getLatLng();
        if (latLng != null) {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // Get the selected place
                Place place = Autocomplete.getPlaceFromIntent(data);
                handleSelectedPlace(place); // Handle the selected place
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("Places", "Error: " + status.getStatusMessage());
            }
        }
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
     * Initializes the photo picker, allowing users to select or capture an image.
     * This method sets up an {@link ActivityResultLauncher} to handle image selection
     * from the gallery or camera and applies compression if necessary.
     *
     * @param view The parent view containing UI elements related to image selection.
     */
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
            preview.setImageDrawable(null);
        });
    }

    /**
     * Displays an error message related to image selection.
     *
     * @param view The parent view containing the error message UI element.
     * @param resId The resource ID of the error message string, or null if clearing the error.
     * @param visibility The visibility state to apply to the error message view.
     */
    private void setImageError(View view, @Nullable Integer resId, int visibility) {
        TextView errorView = view.findViewById(R.id.image_error_msg);
        if(resId != null) {
            errorView.setText(resId);
        }
        errorView.setVisibility(visibility);
    }

    /**
     * Attempts to compress a selected image to reduce its size while maintaining quality.
     * If the image cannot be compressed below the maximum allowed size, it returns null.
     *
     * @param uri The URI of the image to be compressed.
     * @return A compressed {@link Bitmap} object, or null if compression fails.
     * @throws IOException If an error occurs while retrieving the image.
     */
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

    /**
     * Converts an image displayed in an {@link ImageView} to a Base64-encoded string.
     *
     * @param imageView The {@link ImageView} containing the image to be encoded.
     * @return The Base64-encoded string representation of the image, or null if no image is present.
     */
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

    /**
     * Populates the fields of the mood form dialog with the data from an existing mood.
     * This method is should be used to pre-fill the dialog fields when editing an existing mood entry,
     * allowing users to see and modify current values.
     *
     * @param mood The mood object whose data is used to populate the form fields.
     */
    public void populateFields(Mood mood) {
        if (!isEditMode || moodBeingEdited == null) return;

        Dialog dialog = getDialog();
        if (dialog != null) {
            // Retrieve the form widgets from the dialog
            Spinner emotionSpinner = dialog.findViewById(R.id.form_emotion);
            Spinner situationSpinner = dialog.findViewById(R.id.form_situation);
            EditText triggerEditText = dialog.findViewById(R.id.form_trigger);
            ImageView moodImagePreview = dialog.findViewById(R.id.mood_image_preview);

            // Set emotion and situation spinners (ONLY WORKS IF ENUM ORDER MATCHES SPINNER ORDER)
            emotionSpinner.setSelection(mood.getEmotion().ordinal());
            situationSpinner.setSelection(mood.getSocialSituation().ordinal());

            // Set trigger text
            triggerEditText.setText(mood.getTrigger());

            // Set image if available
            if (mood.getImageBase64() != null && !mood.getImageBase64().isEmpty()) {
                moodImagePreview.setImageBitmap(ImageUtils.decodeBase64(mood.getImageBase64()));
                moodImagePreview.setVisibility(View.VISIBLE);
                ImageButton removePreviewButton = dialog.findViewById(R.id.remove_preview);
                removePreviewButton.setVisibility(View.VISIBLE);
            }
        }
    }
}