package com.example.app_ee3;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class DialogActivity extends Activity {
    Dialog dialog;
    Button BtnAPPLY;
    TextView txtSubTitle;
    TextView txtTitle;

    private static final String TAG = "FCMService";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5uYWFyZ2JnaGhhbmh2Ym50d3BkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA2NzA3MTMsImV4cCI6MjA1NjI0NjcxM30.C8Jxeyp-s_TJ2StQXu8_UPj0D1cUFATZL24lDuGnnPY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PopUpNotification();
    }

    private void PopUpNotification() {
        dialog = new Dialog(this);

        String body = getIntent().getStringExtra("body");
        String title = getIntent().getStringExtra("title");
        String dispatchIdStr = getIntent().getStringExtra("intervention"); // IMPORTANT: we now get dispatch id instead!

        dialog.setContentView(R.layout.pop_up_intervention);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.pop_up_intervention_background));
        dialog.setCancelable(false);

        txtSubTitle = dialog.findViewById(R.id.txtSubTitle);
        txtTitle = dialog.findViewById(R.id.txtTitle);
        BtnAPPLY = dialog.findViewById(R.id.btnAPPLY);

        txtSubTitle.setText(body != null ? body : "");
        txtTitle.setText(title != null ? title : "No information");

        if (dispatchIdStr == null) {
            Log.e("DialogActivity", "Dispatch ID is missing.");
            return;
        }
        int dispatchId = Integer.parseInt(dispatchIdStr);

        fetchInterventionIdFromDispatch(dispatchId, new InterventionIdCallback() {
            @Override
            public void onInterventionIdFetched(int interventionId) {
                BtnAPPLY.setOnClickListener(v -> {
                    int firefighterId = Integer.parseInt(
                            getSharedPreferences("UserData", MODE_PRIVATE).getString("badge_nr", "-1"));

                    if (firefighterId == -1) {
                        Log.e("DialogActivity", "No firefighter ID found.");
                        return;
                    }
                    insertUserAtIntervention(firefighterId, interventionId);
                });

                checkFirefighterCount(interventionId, dialog);
            }
        });

        dialog.show();
    }

    private void checkFirefighterCount(int interventionId, final Dialog dialog) {
        fetchFirefighterCount(interventionId, new FirefighterCountCallback() {
            @Override
            public void onCountFetched(int count) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (count >= 2) {
                        Toast.makeText(DialogActivity.this, "Enough firefighters already applied!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    } else {
                        new Handler().postDelayed(() -> checkFirefighterCount(interventionId, dialog), 2000);
                    }
                });
            }
        });
    }

    private void fetchFirefighterCount(int interventionId, FirefighterCountCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/user_at_intervention"
                + "?intervention=eq." + interventionId
                + "&select=firefighter";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DialogActivity", "Count fetch failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(DialogActivity.this, "Failed to fetch firefighter count", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int count = 0;
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        count = jsonArray.length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                final int finalCount = count;
                runOnUiThread(() -> callback.onCountFetched(finalCount));
            }
        });
    }

    public interface FirefighterCountCallback {
        void onCountFetched(int count);
    }

    private void insertUserAtIntervention(int firefighterId, int interventionId) {
        OkHttpClient client = new OkHttpClient();

        String json = "{ \"firefighter\": " + firefighterId + ", \"intervention\": " + interventionId + " }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request insertRequest = new Request.Builder()
                .url("https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/user_at_intervention")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Prefer", "return=representation")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(insertRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DialogActivity", "Insert failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("DialogActivity", "Firefighter added to intervention");
                    dialog.dismiss();
                    finish();
                } else if (response.body().string().contains("row-level security")) {
                    runOnUiThread(() ->
                            Toast.makeText(DialogActivity.this, "Enough firefighters already applied!", Toast.LENGTH_LONG).show()
                    );
                    dialog.dismiss();
                    finish();
                } else {
                    Log.e("DialogActivity", "Insert failed: " + response.code() + " - " + response.body().string());
                }
            }
        });
    }

    private void fetchInterventionIdFromDispatch(int dispatchId, InterventionIdCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/intervention"
                + "?dispatch=eq." + dispatchId
                + "&select=intervention_id";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DialogActivity", "Fetch intervention ID failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            int interventionId = jsonArray.getJSONObject(0).getInt("intervention_id");
                            new Handler(Looper.getMainLooper()).post(() -> callback.onInterventionIdFetched(interventionId));
                        } else {
                            Log.e("DialogActivity", "No intervention found for this dispatch");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("DialogActivity", "Fetch intervention ID failed: " + response.code());
                }
            }
        });
    }

    public interface InterventionIdCallback {
        void onInterventionIdFetched(int interventionId);
    }
}
