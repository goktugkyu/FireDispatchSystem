package com.example.app_ee3;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatisticsFragment extends Fragment {


    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<Integer> dispatchNumbers = new ArrayList<>();

    ArrayList<Entry> averageEntries = new ArrayList<>();
    float average;
    Entry minimum;
    Entry maximum;
    //InterventionAdapter adapter;
    private LineChart lineChart;
    int primaryTextColor;

    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        //adapter = new TruckAdapter(getActivity(), interventionList);
        lineChart = view.findViewById(R.id.lineChart);

        setColors();
        getEntries();
        return view;
    }

    private void getEntries(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        int departmentId = Integer.parseInt(sharedPreferences.getString("department_id", "-1"));

        String url = "https://nnaargbghhanhvbntwpd.supabase.co/rest/v1/intervention"
                + "?select=*"
                + "&department=eq." + departmentId
                + "&order=intervention_id.asc"
                + "&limit=10";


        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Failed to fetch interventions", Toast.LENGTH_SHORT).show();
                });
                Log.e("StatisticsFragment", "Network error: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Error fetching interventions", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    entries.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject statisticObject = jsonArray.getJSONObject(i);
                        int dispatch = statisticObject.getInt("dispatch");

                        int duration = 0;
                        if (!statisticObject.isNull("duration")) {
                            duration = statisticObject.getInt("duration");
                        }

                        entries.add(new Entry(i + 1, duration));
                        dispatchNumbers.add(dispatch);
                    }



                    requireActivity().runOnUiThread(() -> {
                        calculateAverage();
                        setupLineChart();
                    });

                    //requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (JSONException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "JSON parse error", Toast.LENGTH_SHORT).show()
                    );
                    Log.e("StatisticsFragment", "JSON Error", e);
                }
            }
        });
    }

    private void calculateAverage(){
        averageEntries.clear();
        float sum = 0;
        for (Entry entry : entries) {
            sum += entry.getY();
        }
        average = sum / entries.size();

        // Create a dataset for the average line
        for (int i = 0; i < entries.size(); i++) {
            averageEntries.add(new Entry(i+1, average)); // All y-values for the average line are the same
        }
    }

    private void setupLineChart() {
        minimum = entries.stream()
                .min(Comparator.comparing(Entry::getY))
                .orElse(null); // Handle case when the list is empty
        maximum = entries.stream()
                .max(Comparator.comparing(Entry::getY))
                .orElse(null); // Handle case when the list is empty

        //Setup colors of circles
        ArrayList<Integer> circleColors = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.equals(minimum)) {
                circleColors.add(Color.parseColor("#808000")); // Min = green
            } else if (entry.equals(maximum)) {
                circleColors.add(Color.parseColor("#B22222")); // Max = rood
            } else {
                circleColors.add(Color.parseColor("#8B4513")); // brown
            }
        }

        //dataset for interventions
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#8B4513"));
        dataSet.setCircleColors(circleColors);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setForm(Legend.LegendForm.NONE);

        //dataset for the average line
        LineDataSet averageDataSet = new LineDataSet(averageEntries, "Average");
        averageDataSet.setColor(Color.parseColor("#FFA500"));
        averageDataSet.setDrawValues(false);
        averageDataSet.setDrawCircles(false);

        //dummy entry for min
        ArrayList<Entry> minEntry = new ArrayList<>();
        minEntry.add(new Entry(minimum.getX(), minimum.getY()));
        LineDataSet minDataSet = new LineDataSet(minEntry, "Minimum");
        minDataSet.setColor(Color.parseColor("#808000"));
        minDataSet.setDrawValues(false);
        minDataSet.setDrawCircles(true);
        minDataSet.setCircleRadius(4f);

        //dummy entry for max
        ArrayList<Entry> maxEntry = new ArrayList<>();
        maxEntry.add(new Entry(maximum.getX(), maximum.getY()));
        LineDataSet maxDataSet = new LineDataSet(maxEntry, "Maximum");
        maxDataSet.setColor(Color.parseColor("#B22222"));
        maxDataSet.setDrawValues(false);
        maxDataSet.setDrawCircles(true);
        maxDataSet.setCircleRadius(4f);

        // Combine all datasets
        LineData lineData = new LineData(averageDataSet, minDataSet, maxDataSet, dataSet);
        lineChart.setData(lineData);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(14f);
        legend.setTextColor(primaryTextColor);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setDrawInside(false);
        legend.setYOffset(5f);
        legend.setXEntrySpace(30f);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Belangrijk: stappen van 1
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(primaryTextColor);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value - 1;
                if (index >= 0 && index < dispatchNumbers.size()) {
                    return String.valueOf(dispatchNumbers.get(index));
                } else {
                    return "";
                }
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawLabels(true); // ensures labels are drawn
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(primaryTextColor); // Set dynamic text color from theme

        lineChart.getAxisRight().setEnabled(false); // Hide right Y-axis

        lineChart.invalidate(); // Refresh the chart

    }

    private void setColors(){
        // Get the current context (activity) to access theme attributes
        Context context = getContext();
        if (context == null) return;  // Early return if context is not available

        // Get theme-aware text color (primary color)
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        primaryTextColor = ContextCompat.getColor(context, typedValue.resourceId);
    }
}

