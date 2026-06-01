package com.example.app_ee3;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.ViewHolder> {

    private List<TruckModel> truckList;
    private Context context;

    public TruckAdapter(Context context, List<TruckModel> truckList) {
        this.truckList = truckList;
        this.context = context;
    }

    @NonNull
    @Override
    public TruckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View truckView = layoutInflater.inflate(R.layout.truck_view, parent, false);
        return new ViewHolder(truckView);
    }

    @Override
    public void onBindViewHolder(@NonNull TruckAdapter.ViewHolder holder, int position) {
        TruckModel truck = truckList.get(position);
        holder.truckId.setText(truck.getTruckId());
        holder.waterLevel.setText(truck.getWaterLevel());

        if (truck.isPresent()) {
            holder.truckStatus.setColorFilter(Color.argb(100, 0, 153, 51)); // green
        } else {
            holder.truckStatus.setColorFilter(Color.argb(100, 139, 0, 0)); // red
        }
    }

    @Override
    public int getItemCount() {
        return truckList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView truckId;
        TextView waterLevel;
        ImageView truckStatus;

        public ViewHolder(@NonNull View truckView) {
            super(truckView);
            truckId = truckView.findViewById(R.id.truck_id);
            waterLevel = truckView.findViewById(R.id.waterlevel);
            truckStatus = truckView.findViewById(R.id.truck_status); // <-- new ImageView
        }
    }

    public void setFilteredList(List<TruckModel> filteredList) {
        this.truckList = filteredList;
        notifyDataSetChanged();
    }
}
