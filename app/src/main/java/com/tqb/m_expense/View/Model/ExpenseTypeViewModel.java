package com.tqb.m_expense.View.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.Database.Entity.ExpenseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpenseTypeViewModel extends ViewModel {
    private MutableLiveData<List<ExpenseType>> data;
    private AppDatabase database;

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }
    public MutableLiveData<List<ExpenseType>> getData() {
        data = new MutableLiveData<>();
        data.setValue(Arrays.asList(database.getExpenseTypeDao().getAllExpenseTypes()));
        return data;
    }
    public void prepopulate() {
        if (database.getExpenseTypeDao().getRowsCount() == 0) {
            final String[] types = {"Transportation", "Rental", "Flight"};
            for (String type: types) {
                database.getExpenseTypeDao().insert(new ExpenseType(type));
            }
        }
    }
    public void deleteAll() {
        database.getExpenseTypeDao().deleteAll();
    }
}
