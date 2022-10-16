package com.tqb.m_expense.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tqb.m_expense.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingRecycleViewAdapter extends RecyclerView.Adapter<SettingRecycleViewAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private final ArrayList<OnItemClickListener> onItemClickListener = new ArrayList<>();
    private final List<String> settingOptions = Arrays.asList("Export to Server");
    private final LayoutInflater layoutInflater;

    public void addClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener.add(onItemClickListener);
    }
    public void setClickListenerAtPosition(int position, OnItemClickListener onItemClickListener) {
        this.onItemClickListener.set(position, onItemClickListener);
    }
    public SettingRecycleViewAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.setting_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.settingTitle.setText(settingOptions.get(position));
    }

    @Override
    public int getItemCount() {
        return settingOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView settingTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            settingTitle = itemView.findViewById(R.id.settingTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.get(getAdapterPosition()).onItemClick(v, getAdapterPosition());
        }
    }
}
