package com.example.app_ee3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DispatchesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<DispatchModel> dispatchList = new ArrayList<>();
    private DispatchAdapter adapter;
    private SearchView searchView;
    private Toolbar titlebar;
    private TextView departmentName;
    private Button refreshButton;

    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private final OkHttpClient client = new OkHttpClient();

    public DispatchesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dispatches, container, false);

        adapter = new DispatchAdapter(getActivity(), dispatchList);
        recyclerView = root.findViewById(R.id.dispatchesView);
        titlebar = root.findViewById(R.id.titlebar);
        departmentName = titlebar.findViewById(R.id.toolbar_title);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchView = root.findViewById(R.id.searchView);
        refreshButton = root.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            fetchDispatchesFromSupabase(); // Re-fetch the data
        });

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                departmentName.setVisibility(newText.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                filterList(newText);
                return true;
            }

        });

        adapter.setOnClickListener((position, dispatch) -> {
            Intent intent = new Intent(getActivity(), DispatchDetails.class);
            intent.putExtra("date", dispatch.getDate());
            intent.putExtra("time", dispatch.getTime());
            intent.putExtra("location", dispatch.getLocation());
            intent.putExtra("situation", dispatch.getSituation());
            startActivity(intent);
        });

        fetchDispatchesFromSupabase();
        return root;
    }

    private void fetchDispatchesFromSupabase() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        int departmentId = Integer.parseInt(sharedPreferences.getString("department_id", "-1"));

        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/dispatch?department=eq." + departmentId + "&select=date,time,location,situation,dispatch_id&order=date.desc";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch dispatches", Toast.LENGTH_SHORT).show());
                }
                Log.e("DispatchesFragment", "Network error: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        dispatchList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String date = obj.optString("date");
                            String time = obj.optString("time");
                            String location = obj.optString("location");
                            String situation = obj.optString("situation");
                            String dispatch_id = obj.optString("dispatch_id");

                            dispatchList.add(new DispatchModel(date, time, location, situation, dispatch_id));
                        }

                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                        }
                    } catch (Exception e) {
                        Log.e("DispatchesFragment", "JSON parse error: " + e.getMessage(), e);
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "Error parsing dispatches", Toast.LENGTH_SHORT).show());
                        }
                    }
                } else {
                    Log.e("DispatchesFragment", "API Error: " + response.message());
                }
            }
        });
    }

    private void filterList(String newText) {
        List<DispatchModel> filteredList = new ArrayList<>();
        for (DispatchModel dispatch : dispatchList) {
            if (dispatch.getDate().toLowerCase().contains(newText.toLowerCase()) || dispatch.getDispatch_id().contains(newText)) {
                filteredList.add(dispatch);
            }
        }
        adapter.setFilteredList(filteredList);
    }
}
