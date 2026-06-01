package com.example.app_ee3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DispatchAdapter extends RecyclerView.Adapter<DispatchAdapter.ViewHolder> {

    private List<DispatchModel> dispatchList;
    private Context context;
    private OnClickListener onClickListener;

    public DispatchAdapter(Context context, List<DispatchModel> dispatchList) {
        this.context = context;
        this.dispatchList = dispatchList;
    }

    @NonNull
    @Override
    public DispatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dispatchView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dispatch_view, parent, false);
        return new ViewHolder(dispatchView);
    }

    @Override
    public void onBindViewHolder(@NonNull DispatchAdapter.ViewHolder holder, int position) {
        DispatchModel dispatch = dispatchList.get(position);

        holder.date.setText(dispatch.getDate());
        holder.time.setText(dispatch.getTime());
        holder.location.setText(dispatch.getLocation());
        //holder.situation.setText(dispatch.getSituation());
        holder.dispatch_id.setText(dispatch.getDispatch_id());

        holder.dispatch.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.dispatchOnClick(holder.getAdapterPosition(), dispatch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dispatchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, location, situation, dispatch_id;
        ConstraintLayout dispatch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
            //situation = itemView.findViewById(R.id.situation);
            dispatch_id = itemView.findViewById(R.id.dispatch_id);
            dispatch = itemView.findViewById(R.id.cardContentLayout);
        }
    }

    public void setFilteredList(List<DispatchModel> filteredList) {
        this.dispatchList = filteredList;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void dispatchOnClick(int position, DispatchModel model);
    }
}
