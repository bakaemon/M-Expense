package com.tqb.m_expense.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tqb.m_expense.Database.Entity.ExpenseType;

import java.util.List;

public class ExpenseTypeSpinnerAdapter extends ArrayAdapter<ExpenseType> {
    public ExpenseTypeSpinnerAdapter(@NonNull Context context, @NonNull List<ExpenseType> objects) {
        super(context,0, objects);
    }
    private View itemView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        ExpenseType expenseType = getItem(position);
        TextView title = convertView.findViewById(android.R.id.text1);
        if (expenseType != null) {
            title.setText(expenseType.getExpenseTypeName());
        }
        return convertView;
    }
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return itemView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return itemView(position, convertView, parent);
    }
}
