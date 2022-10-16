package com.tqb.m_expense.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tqb.m_expense.R;
import com.tqb.m_expense.View.Model.ExpenseView;

import java.util.List;

public class ExpenseRecycleViewAdapter extends RecyclerView.Adapter<ExpenseRecycleViewAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private OnItemClickListener onItemClickListener;
    private List<ExpenseView> expenses;
    private LayoutInflater layoutInflater;

    public ExpenseRecycleViewAdapter(Context context, List<ExpenseView> expenses) {
        this.layoutInflater = LayoutInflater.from(context);
        setData(expenses);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setData(List<ExpenseView> expenses) {
        this.expenses = expenses;
        this.notifyItemChanged(0);
    }

    private void clearData() {
        expenses.clear();
        this.notifyItemChanged(0, expenses.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.expense_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.expenseType.setText(expenses.get(position).getExpenseType());
        holder.expenseAmount.setText(String.valueOf(expenses.get(position).getExpenseAmount()));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView expenseType;
        public TextView expenseAmount;
        public ViewHolder(View itemView) {
            super(itemView);
            expenseType = itemView.findViewById(R.id.expenseType);
            expenseAmount = itemView.findViewById(R.id.expenseAmount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
