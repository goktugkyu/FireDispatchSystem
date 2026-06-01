package com.example.app_ee3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrucksFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<TruckModel> truckList = new ArrayList<>();
    private TruckAdapter adapter;
    private SearchView searchView;
    private Toolbar titlebar;
    private TextView departmentName;
    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private final OkHttpClient client = new OkHttpClient();

    public TrucksFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trucks, container, false);

        adapter = new TruckAdapter(getActivity(), truckList);
        recyclerView = root.findViewById(R.id.trucksView);
        titlebar = root.findViewById(R.id.titlebar);
        departmentName = titlebar.findViewById(R.id.toolbar_title);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchView = root.findViewById(R.id.searchView);
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

        fetchTrucks();
        return root;
    }

    //Fetch code written with the help of https://www.youtube.com/watch?v=JSFisEN_TO0&ab_channel=vlogize AND https://www.baeldung.com/guide-to-okhttp
    private void fetchTrucks() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        int departmentId = Integer.parseInt(sharedPreferences.getString("department_id", "-1"));

        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/truck?select=*&department=eq." + departmentId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "failed to fetch trucks", Toast.LENGTH_SHORT).show());
                }
                Log.e("TrucksFragment", "Network error: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "error fetching trucks", Toast.LENGTH_SHORT).show());
                    }
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    truckList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject truckObject = jsonArray.getJSONObject(i);
                        String truckId = "Truck " + truckObject.getInt("truck_id");
                        String waterLevel = truckObject.getInt("water_level") + "%";
                        boolean present = truckObject.getBoolean("present");

                        truckList.add(new TruckModel(truckId, waterLevel, present));
                    }

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                } catch (JSONException e) {
                    Log.e("TrucksFragment", "JSON parse error " + e.getMessage(), e);
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "error parsing trucks", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void filterList(String newText) {
        List<TruckModel> filteredList = new ArrayList<>();
        for (TruckModel truck : truckList) {
            if (truck.getTruckId().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(truck);
            }
        }
        adapter.setFilteredList(filteredList);
    }
}
