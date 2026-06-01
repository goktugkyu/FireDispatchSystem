package com.example.app_ee3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterPage extends AppCompatActivity {

    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private final OkHttpClient client = new OkHttpClient();

    EditText firstNameInput, surnameInput, badgeNrInput, passwordInput;
    Spinner departmentSpinner;
    Button registerBtn;

    ArrayList<String> departmentLocations = new ArrayList<>();
    HashMap<String, Integer> departmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        firstNameInput = findViewById(R.id.first_name);
        surnameInput = findViewById(R.id.surname);
        badgeNrInput = findViewById(R.id.badge_nr);
        passwordInput = findViewById(R.id.password);
        departmentSpinner = findViewById(R.id.department_spinner);
        registerBtn = findViewById(R.id.register_btn);

        fetchDepartments();

        registerBtn.setOnClickListener(v -> {
            String first = firstNameInput.getText().toString().trim();
            String last = surnameInput.getText().toString().trim();
            String badge = badgeNrInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();
            String selectedDept = departmentSpinner.getSelectedItem().toString();

            if (first.isEmpty() || last.isEmpty() || badge.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                int deptId = departmentMap.getOrDefault(selectedDept, -1);
                registerUser(first, last, Integer.parseInt(badge), pass, deptId);
            }
        });
    }

    private void fetchDepartments() {
        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/department?select=*";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RegisterPage.this, "failed to load departments", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONArray array = new JSONArray(responseData);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String location = obj.getString("location"); // Corrected column name
                        int id = obj.getInt("department_id");
                        departmentLocations.add(location);
                        departmentMap.put(location, id);
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterPage.this,
                                android.R.layout.simple_spinner_item, departmentLocations);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departmentSpinner.setAdapter(adapter);
                    });

                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(RegisterPage.this, "error parsing departments", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void registerUser(String first, String last, int badgeNr, String password, int departmentId) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("input_first_name", first);
                json.put("input_sur_name", last);
                json.put("input_badge_nr", badgeNr);
                json.put("input_password", password);
                json.put("input_department", departmentId);

                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url("https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/rpc/register_firefighter")
                        .addHeader("apikey", SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                );
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Unexpected error", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

}
