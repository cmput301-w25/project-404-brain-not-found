package com.example.cmput301_team_project.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cmput301_team_project.BuildConfig;
import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * The main activity that serves as the entry point for the application after login.
 * It manages navigation between different fragments using a bottom navigation bar.
 */
public class MainActivity extends BaseActivity{
    /**
     * Called when the activity is first created.
     * Sets up the main layout, navigation bar, and initial fragment.
     * @param savedInstanceState If the activity is being re-created, this contains saved data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        checkNotification();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        BottomNavigationView navigation = findViewById(R.id.navigation_main);
        navigation.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return insets;
        });

        replaceFragment(MoodFollowingFragment.newInstance());

        navigation.setSelectedItemId(R.id.mood_following_icon);

        navigation.setOnItemSelectedListener(item -> {
            checkNotification();
            if(item.getItemId() == R.id.mood_history_icon) {
                replaceFragment(MoodHistoryFragment.newInstance());
            }
            else if(item.getItemId() == R.id.user_icon) {
                replaceFragment(UserFragment.newInstance());

            }
            else if(item.getItemId() == R.id.mood_following_icon) {
                replaceFragment(MoodFollowingFragment.newInstance());
            }
            else if(item.getItemId() == R.id.mentioned_icon){
                replaceFragment(MentionedMoodsFragment.newInstance(this::checkNotification));
            }
            return true;
        });

        initializePlacesApi();
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Initializes the Google Places API with the API key from BuildConfig.
     * If no valid API key is found, logs an error and closes the activity.
     */
    private void initializePlacesApi() {
        String apiKey = BuildConfig.MAPS_API_KEY;

        if (TextUtils.isEmpty(apiKey)) {
            Log.e("Maps key test", "No api key");
            finish();
            return;
        }
        else if(apiKey.equals("DEFAULT_API_KEY")) {
            Log.d("Maps key test", "Using default api key, google maps functionality is disabled");
        }
        Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);
    }

    /**
     * Sets up the notification badge for a specified icon in the navbar.
     *
     * @param id     The menu id where the badge should be set.
     * @param alerts The number of alerts made to the user.
     */
    private void badgeSetup(int id, int alerts){
        BottomNavigationView navigation = findViewById(R.id.navigation_main);
        BadgeDrawable badge = navigation.getOrCreateBadge(id);
        badge.setVisible(true);
        badge.setNumber(alerts);
    }

    /**
     * Clears the notification badge for the specified icon in the navbar.
     *
     * @param id The id of the navbar icon.
     */
    private void badgeClear(int id){
        BottomNavigationView navigation = findViewById(R.id.navigation_main);
        BadgeDrawable badgeDrawable = navigation.getBadge(id);
        if (badgeDrawable != null){
            badgeDrawable.setVisible(false);
            badgeDrawable.clearNumber();
        }
    }

    /**
     * Checks for mentions made to the user. If there is mentions, then the badge is created/updated
     * with the count of the user mentions. If there is no mentions, then the badge is cleared.
     */
    public void checkNotification(){
        UserDatabaseService.getInstance().getMentionCount(FirebaseAuthenticationService.getInstance().getCurrentUser()).addOnSuccessListener(count -> {
            if (count != 0){
                int countInt = count.intValue();
                badgeSetup(R.id.mentioned_icon, countInt);
            }else{
                badgeClear(R.id.mentioned_icon);

            }
        });
    }
}