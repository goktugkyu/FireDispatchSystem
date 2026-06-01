package com.example.app_ee3;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //sendPostRequest();
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String interventionId = remoteMessage.getData().get("intervention");
            Intent dialogIntent = new Intent(getApplicationContext(), DialogActivity.class);
            dialogIntent.putExtra("body", body);
            dialogIntent.putExtra("title", title);
            dialogIntent.putExtra("intervention", interventionId);
            //source: CHATGPT
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Nodig om Activity vanuit een Service te starten
            startActivity(dialogIntent);
        }
    }

    /*source: Claude.ai*/
    private void sendPostRequest() {
//        new Thread(() -> {
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .writeTimeout(10,TimeUnit.SECONDS)
//                    .readTimeout(10, TimeUnit.SECONDS)
//                    .build();
//
//            String url = "http://192.168.137.49/webhook";
//            String json = "{\"message\": \"Activate buzzer\"}";
//
//            MediaType JSON = MediaType.get("application/json; charset=utf-8");
//            RequestBody body = RequestBody.create(json, JSON);
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(body)
//                    .build();
//
//            Log.d("HTTP_REQUEST", "Sending request to: " + url);
//            Log.d("HTTP_REQUEST", "Payload: " + json);
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    e.printStackTrace();
//                    Log.e("HTTP_ERROR", "Request failed: " + e.getMessage());
//                    /*new Handler(Looper.getMainLooper()).post(() ->
//                            Toast.makeText(getApplicationContext(),
//                                    "Error: " + e.getMessage(),
//                                    Toast.LENGTH_SHORT).show()
//                    );*/
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    int responseCode = response.code();
//                    String responseBody = "";
//
//                    if (response.body() != null) {
//                        responseBody = response.body().string();
//                    }
//
//                    if (!response.isSuccessful()) {
//                        Log.e("HTTP_ERROR", "Unexpected response: " + responseCode);
//                    } else {
//                        Log.d("HTTP_SUCCESS", "Response: " + responseBody);
//                    }
//                }
//            });
//        }).start();
        new Thread(() -> {
            try {
                /*ESP32 Web Server ondersteunt standaard geen HTTPS,
                omdat de ingebouwde ESP32 HTTP-server (zoals esp_http_server) geen SSL/TLS gebruikt.*/
                URL url = new URL("http://192.168.137.49/webhook");// ESP32 IP-adres en endpoint
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setDoOutput(true);

                // JSON payload
                String jsonInputString = "{\"message\": \"Activate buzzer\"}";

                // Data verzenden
                try (OutputStream os = conn.getOutputStream();
                     OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                     BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

                    bufferedWriter.write(jsonInputString);
                    bufferedWriter.flush();
                }

                int responseCode = conn.getResponseCode();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(this, "Response: " + responseCode, Toast.LENGTH_SHORT).show()
                );
                Log.d("HTTP_REQUEST", "Response code: " + responseCode);

                conn.disconnect();
            } catch (Exception e) {
                //Toast.makeText(this, "Error sending POST request", Toast.LENGTH_SHORT).show();
                Log.e("HTTP_REQUEST", "Error sending POST request", e);
            }
        }).start();

    }
}
