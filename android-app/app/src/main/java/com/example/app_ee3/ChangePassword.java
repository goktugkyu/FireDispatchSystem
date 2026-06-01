package com.example.app_ee3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePassword extends AppCompatActivity {

    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private final OkHttpClient client = new OkHttpClient();

    private EditText oldPasswordEditText, newPasswordEditText;
    private Button confirmButton;
    private String badgeNr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        badgeNr = prefs.getString("badge_nr", null);

        oldPasswordEditText = findViewById(R.id.old_password);
        newPasswordEditText = findViewById(R.id.new_password);
        confirmButton = findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();

            if (badgeNr == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                changePassword(badgeNr, oldPassword, newPassword);
            }
        });
    }
    //Fetch and Update code written with the help of https://www.youtube.com/watch?v=JSFisEN_TO0&ab_channel=vlogize AND https://www.baeldung.com/guide-to-okhttp
    private void changePassword(String badgeNr, String oldPassword, String newPassword) {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("input_badge_nr", Integer.parseInt(badgeNr));
                jsonBody.put("input_old_password", oldPassword);
                jsonBody.put("input_new_password", newPassword);

                String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/rpc/change_password";
                RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(ChangePassword.this, "Network error", Toast.LENGTH_SHORT).show()
                        );
                        Log.e("ChangePassword", "network er", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();

                        runOnUiThread(() -> {
                            if (!response.isSuccessful()) {
                                Toast.makeText(ChangePassword.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                Log.e("ChangePassword", "error code " + result);
                                return;
                            }

                            if (result.contains("success")) {
                                Toast.makeText(ChangePassword.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (result.contains("invalid_old_password")) {
                                Toast.makeText(ChangePassword.this, "Old password is incorrect", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ChangePassword.this, "Unknown error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } catch (Exception e) {
                Log.e("ChangePassword", "Exception", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
