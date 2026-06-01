package com.example.app_ee3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;

public class DispatchDetails extends AppCompatActivity {

    Toolbar titlebar;
    private TextView txtDate, txtTime, txtLocation, txtSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dispatch_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titlebar = findViewById(R.id.titlebar);

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtLocation = findViewById(R.id.txtLocation);
        txtSituation = findViewById(R.id.txtSituation);

        // Get intent data
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String location = getIntent().getStringExtra("location");
        String situation = getIntent().getStringExtra("situation");

        // Set data to UI
        txtDate.setText(date);
        txtTime.setText(time);
        txtLocation.setText(location);
        txtSituation.setText(situation);
    }
}
