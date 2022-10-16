package com.tqb.m_expense.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tqb.m_expense.R;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.View.Model.TripView;

import java.util.Date;
import java.util.List;

public class TripRecycleViewAdapter extends RecyclerView.Adapter<TripRecycleViewAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private OnItemClickListener onItemClickListener;
    private List<TripView> trips;
    private LayoutInflater layoutInflater;
    public TripRecycleViewAdapter(Context context, List<TripView> trips) {
        this.layoutInflater = LayoutInflater.from(context);
        setData(trips);
    }
    public void clearData() {
        trips.clear();
        this.notifyItemChanged(0, trips.size());
    }
    public void setData(List<TripView> trips) {
        this.trips = trips;
        this.notifyItemChanged(0);
    }
    public List<TripView> getTrips() {
        return trips;
    }
    public void setTrips(List<TripView> trips) {
        this.trips = trips;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.trip_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TripView trip = trips.get(position);
        String finished = trip.isFinished() ? "(Finished)" : "";
        holder.tripName.setText(trip.getTripName().concat(finished));
        holder.tripDate.setText(String.valueOf(trip.getTripDate()).concat(" days."));
        holder.tripBudget.setText(String.valueOf(trip.getTripBudget()));
        holder.tripCurrency.setText(trip.getTripCurrency().concat(" - "));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tripName, tripDate, tripBudget, tripCurrency;


        public ViewHolder(View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.tripName);
            tripDate = itemView.findViewById(R.id.tripDate);
            tripBudget = itemView.findViewById(R.id.tripBudget);
            tripCurrency = itemView.findViewById(R.id.tripCurrency);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


}
