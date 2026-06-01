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

public class FirefighterAdapter extends RecyclerView.Adapter<FirefighterAdapter.ViewHolder>{
    private List<FirefighterModel> firefighterList;
    private Context context;

    public FirefighterAdapter(Context context, List<FirefighterModel> firefighterList) {
        this.firefighterList = firefighterList;
        this.context = context;
    }

    @NonNull
    @Override
    public FirefighterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View firefighterView = layoutInflater.inflate(R.layout.firefighter_view, parent, false);
        ViewHolder ViewHolder = new ViewHolder(firefighterView);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FirefighterAdapter.ViewHolder holder, int position) {
        FirefighterModel firefighter = firefighterList.get(position);
        ImageView status = (ImageView) holder.present.findViewById(R.id.status);

        ((TextView) holder.name.findViewById(R.id.name))
                .setText(firefighterList.get(position).getName());
        ((TextView) holder.batchNumber.findViewById(R.id.batch_number))
                .setText(Integer.toString(firefighterList.get(position).getBatch_number()));


        if(firefighter.isPresent()){
            status.setColorFilter(Color.argb(100, 0, 153, 51));
        }
        else{
            status.setColorFilter(Color.argb(100, 139, 0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return firefighterList.size();    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView batchNumber;
        ImageView present;

        public ViewHolder(@NonNull View firefighterView) {
            super(firefighterView);
            name = firefighterView.findViewById(R.id.name);
            batchNumber = firefighterView.findViewById(R.id.batch_number);
            present = firefighterView.findViewById(R.id.status);
        }
    }

    public void setFilteredList (List<FirefighterModel> filterdList)
    {
        this.firefighterList = filterdList;
        notifyDataSetChanged();
    }
}
