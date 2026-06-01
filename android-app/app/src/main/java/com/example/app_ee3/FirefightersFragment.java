package com.example.app_ee3;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;

import android.os.Bundle;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;


public class FirefightersFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<FirefighterModel> firefighterList = new ArrayList<>();
    private SearchView searchView;
    FirefighterAdapter adapter;
    Toolbar titlebar;
    TextView departmentName;

    public FirefightersFragment() {
        // Required empty public constructor
    }

    public static FirefightersFragment newInstance() {
        FirefightersFragment fragment = new FirefightersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //setUpFirefighterModel();
        fetchFirefighters();

        View root = inflater.inflate(R.layout.fragment_firefighters, container, false);
        adapter = new FirefighterAdapter(getActivity(), firefighterList);
        recyclerView = root.findViewById( R.id.firefighterView );
        titlebar = root.findViewById(R.id.titlebar);
        departmentName = titlebar.findViewById(R.id.toolbar_title);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false));
        searchView = (SearchView) root.findViewById(R.id.searchView);
        searchView.clearFocus();
        /*Listener for the searchview*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            /*Triggered when the user submits their query;
             * usually by pressing the "enter" key or the search button on the keyboard*/
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            /*Triggered every time the user types or deletes a character in the SearchView (Dynamically change if showed list)*/
            public boolean onQueryTextChange(String newText) {
                /*Checks if the text typed in the searchview in the is empty or not*/
                if(!newText.isEmpty())
                {
                    /*It is not empty, so the department name should become invisible*/
                    departmentName.setVisibility(View.INVISIBLE);
                }
                else
                {
                    /*It is empty, so the department name should become visible*/
                    departmentName.setVisibility(View.VISIBLE);
                }
                filterList(newText);
                return true;
            }
        });
        return root;
    }
    private void filterList(String newText)
    {
        List<FirefighterModel> filteredList = new ArrayList<>();
        for (FirefighterModel firefighter: firefighterList)
        {
            if (firefighter.getName().toLowerCase().contains(newText.toLowerCase()))
            {
                filteredList.add(firefighter);
            }
        }
        if (filteredList.isEmpty())
        {
            Toast.makeText(getActivity(), "No dispatch found", Toast.LENGTH_LONG).show();
        }
        /*Updates the data and refreshes the view.*/
        adapter.setFilteredList(filteredList);
    }

    private final OkHttpClient client = new OkHttpClient();
    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;

    private void fetchFirefighters() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        int departmentId = Integer.parseInt(sharedPreferences.getString("department_id", "-1"));

        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/firefighters?select=*&department=eq." + departmentId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to fetch firefighters", Toast.LENGTH_SHORT).show()
                );
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Error fetching firefighters", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    firefighterList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String fullName = obj.getString("first_name") + " " + obj.getString("sur_name");
                        int badgeNr = obj.getInt("badge_nr");
                        boolean present = obj.getBoolean("present");

                        firefighterList.add(new FirefighterModel(fullName, badgeNr, present));
                    }

                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (JSONException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Error parsing firefighter data", Toast.LENGTH_SHORT).show()
                    );
                    e.printStackTrace();
                }
            }
        });
    }


    private void setUpFirefighterModel(){

        firefighterList.add(new FirefighterModel("Silke", 1234, true));
        firefighterList.add(new FirefighterModel("Nienke",2345, true));
        firefighterList.add(new FirefighterModel("Harry", 3456, false));
        firefighterList.add(new FirefighterModel("Li", 4567, true));
        firefighterList.add(new FirefighterModel("Göktug", 5678, false));
    }
}