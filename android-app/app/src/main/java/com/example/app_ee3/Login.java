package com.example.app_ee3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLogin;
    private EditText txtBadge;
    private EditText txtPassword;

    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5uYWFyZ2JnaGhhbmh2Ym50d3BkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA2NzA3MTMsImV4cCI6MjA1NjI0NjcxM30.C8Jxeyp-s_TJ2StQXu8_UPj0D1cUFATZL24lDuGnnPY";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        txtBadge = findViewById(R.id.txtBadge);
        txtPassword = findViewById(R.id.txtPassword);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.txtPassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin.setOnClickListener(v -> onBtnLogin_Clicked());
        btnRegister.setOnClickListener(v -> onBtnRegister_Clicked());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            String body = intent.getStringExtra("body");
            String interventionId = intent.getStringExtra("intervention");

            Intent dialogIntent = new Intent(getApplicationContext(), DialogActivity.class);
            dialogIntent.putExtra("body", body);
            dialogIntent.putExtra("title", title);
            dialogIntent.putExtra("intervention", interventionId);

            startActivity(dialogIntent);
            setIntent(new Intent()); // Clear old notification intent
        }
    }

    public void onBtnRegister_Clicked() {
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }

    public void onBtnLogin_Clicked() {
        String badgeNumber = txtBadge.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (badgeNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter badge number and password", Toast.LENGTH_SHORT).show();
            return;
        }
        validateLogin(badgeNumber, password);
    }

    private void validateLogin(String badgeNumber, String password) {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("input_badge_nr", Integer.parseInt(badgeNumber));
                jsonBody.put("input_password", password);

                String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/rpc/check_login";

                RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String responseData = response.body().string();

                    if (!response.isSuccessful()) {
                        Log.e("LOGIN_ERROR", "API Error: Code " + response.code() + " | Body: " + responseData);
                        throw new IOException("Unexpected code " + response.code());
                    }

                    JSONArray result = new JSONArray(responseData);

                    runOnUiThread(() -> {
                        try {
                            if (result.length() > 0) {
                                JSONObject user = result.getJSONObject(0);
                                String badgeNr = user.getString("badge_nr");
                                String firstName = user.getString("first_name");
                                String surName = user.getString("sur_name");
                                String departmentName = user.getString("department_name"); // department NAME
                                String departmentId = user.getString("department"); // department ID

                                String fullName = firstName + " " + surName;

                                // Save UserData
                                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("badge_nr", badgeNr);
                                editor.putString("full_name", fullName);
                                editor.putString("department", departmentName);   // (for showing)
                                editor.putString("department_id", departmentId);  // (for filtering)
                                editor.apply();

                                // Move to BottomNavigation
                                Intent intent = new Intent(Login.this, BottomNavigation.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Invalid badge number or password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("LOGIN_ERROR", "JSON Parsing error: " + e.getMessage(), e);
                        }
                    });
                }
            } catch (IOException e) {
                Log.e("LOGIN_ERROR", "IOException: " + e.getMessage(), e);
            } catch (Exception e) {
                Log.e("LOGIN_ERROR", "Unexpected error: " + e.getMessage(), e);
            }
        }).start();
    }
}
