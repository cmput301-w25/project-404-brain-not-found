package com.example.cmput301_team_project;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements MoodDetailFragment.OnFragmentInteractionListener{

    private String currentUsername; // Add this variable to store the current user's username

    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        currentUsername = getCurrentUsername();


        BottomNavigationView navigation = findViewById(R.id.navigation_main);
        //TODO: This should probably be changed to mood following list once its fragment is created so that's the first tab user sees
        //replaceFragment(new MoodHistoryFragment());
        replaceFragment(MoodHistoryFragment.newInstance(currentUsername));

        navigation.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.mood_history_icon) {
                replaceFragment(MoodHistoryFragment.newInstance(currentUsername));
            }
            return true;
        });
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onOkPressed() {
        // Handle the "OK" button press here, e.g., show a toast or close the dialog
        Toast.makeText(this, "OK pressed in MainActivity", Toast.LENGTH_SHORT).show();
    }

    // Method to show the MoodDetailFragment with mood details
    public void showMoodDetails(Mood mood) {
        // Create and show the MoodDetailFragment with the given mood
        MoodDetailFragment fragment = new MoodDetailFragment(mood);
        fragment.show(getSupportFragmentManager(), "MoodDetailFragment");
    }

}