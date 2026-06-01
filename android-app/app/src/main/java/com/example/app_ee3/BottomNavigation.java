package com.example.app_ee3;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/*Firebase imports*/
import com.google.firebase.messaging.FirebaseMessaging;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;


public class BottomNavigation extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar titlebar;
    ImageView userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_navigation);

        //initializing variables
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        titlebar = findViewById(R.id.titlebar);
        userIcon = titlebar.findViewById(R.id.user);

        /* Restore UserPage logic exactly as it was */
        if (userIcon != null) {
            Log.d("BottomNavigation", "User icon found successfully.");
        } else {
            Log.e("BottomNavigation", "User icon is null!");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Handle BottomNavigation */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.trucks) {
                replaceFragment(new TrucksFragment());
            } else if (item.getItemId() == R.id.dispatches) {
                replaceFragment(new DispatchesFragment());
            } else if (item.getItemId() == R.id.firefighter) {
                replaceFragment(new FirefightersFragment());
            } else if (item.getItemId() == R.id.statistics) {
                replaceFragment(new StatisticsFragment());
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.dispatches);
        replaceFragment(new DispatchesFragment());


        /*source: https://www.youtube.com/watch?v=NF0RzhXDRKw*/
        getFCMToken();
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FCM", "Fetching FCM token failed", task.getException());
                return;
            }

            String token = task.getResult();
            Log.d("FCM", "Token: " + token);

            sendTokenToSupabase(token);
        });
    }

    private void sendTokenToSupabase(String token) {
        // Get the logged-in badge number from SharedPreferences
        String badgeNr = getSharedPreferences("UserData", MODE_PRIVATE)
                .getString("badge_nr", null);

        if (badgeNr == null) {
            Log.e("FCM", "No badge number found in SharedPreferences");
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // Build JSON body
        String json = "{ \"fcm_token\": \"" + token + "\" }";
        RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json"));

        // Supabase URL to update the firefighter
        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/firefighters?badge_nr=eq." + badgeNr;

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5uYWFyZ2JnaGhhbmh2Ym50d3BkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA2NzA3MTMsImV4cCI6MjA1NjI0NjcxM30.C8Jxeyp-s_TJ2StQXu8_UPj0D1cUFATZL24lDuGnnPY")  // replace if needed
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5uYWFyZ2JnaGhhbmh2Ym50d3BkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA2NzA3MTMsImV4cCI6MjA1NjI0NjcxM30.C8Jxeyp-s_TJ2StQXu8_UPj0D1cUFATZL24lDuGnnPY")  // replace if needed
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FCM", "Failed to send FCM token to Supabase: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Token successfully updated in Supabase");
                } else {
                    Log.e("FCM", "Supabase update failed: " + response.code() + " - " + response.body().string());
                }
            }
        });
    }



    public void onUserIconClicked(View view) {
        Log.d("BottomNavigation", "User icon clicked! Starting UserPage...");
        Intent intent = new Intent(this, UserPage.class);
        startActivity(intent);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
