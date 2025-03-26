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
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.auth.User;


/**
 * The main activity that serves as the entry point for the application after login.
 * It manages navigation between different fragments using a bottom navigation bar.
 */
public class MainActivity extends BaseActivity {
    UserDatabaseService userDatabaseService = UserDatabaseService.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        BottomNavigationView navigation = findViewById(R.id.navigation_main);
        userDatabaseService.getMentionCount(FirebaseAuthenticationService.getInstance().getCurrentUser()).addOnSuccessListener(count -> {
            if (count != 0){
                showNotification(navigation);
            }else{
                removeNotification(navigation);
            }
        });
        navigation.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return insets;
        });



        replaceFragment(MoodFollowingFragment.newInstance());

        navigation.setOnItemSelectedListener(item -> {
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
                replaceFragment(MentionedMoodsFragment.newInstance());

            }
            userDatabaseService.getMentionCount(FirebaseAuthenticationService.getInstance().getCurrentUser()).addOnSuccessListener(count -> {
                if (count != 0){
                    showNotification(navigation);
                }else{
                    removeNotification(navigation);
                }
            });
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

    private void initializePlacesApi() {
        String apiKey = BuildConfig.MAPS_API_KEY;

        if (TextUtils.isEmpty(apiKey) || apiKey.equals("DEFAULT_API_KEY")) {
            Log.e("Places test", "No api key");
            finish();
            return;
        }
        Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);
    }

    private void showNotification(BottomNavigationView navigation){
        navigation.getMenu().findItem(R.id.mentioned_icon).setIcon(R.drawable.comment_icon_with_red);
    }
    private void removeNotification(BottomNavigationView navigation){
        navigation.getMenu().findItem(R.id.mentioned_icon).setIcon(R.drawable.ic_baseline_chat_24);
    }
}