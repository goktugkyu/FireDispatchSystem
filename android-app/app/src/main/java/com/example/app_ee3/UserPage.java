package com.example.app_ee3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class UserPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);


        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("full_name", "Unknown");
        String badgeNr = sharedPreferences.getString("badge_nr", "Unknown");
        String departmentName = sharedPreferences.getString("department", "Unknown");


        TextView username = findViewById(R.id.username);
        TextView badgeNo = findViewById(R.id.badge_no);
        TextView station = findViewById(R.id.station);
        Button changePasswordButton = findViewById(R.id.change_password_button);

        username.setText("Username: " + fullName);
        badgeNo.setText("Badge Number: " + badgeNr);
        station.setText("Station: " + departmentName);


        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserPage.this, ChangePassword.class);
            startActivity(intent);
        });
    }

}
