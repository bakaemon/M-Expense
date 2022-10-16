package com.tqb.m_expense.View.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.Entity.Expense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseViewModel extends ViewModel {
    private MutableLiveData<List<ExpenseView>> data;
    List<ExpenseView> expenseViews = new ArrayList<>();
    AppDatabase database;

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public MutableLiveData<List<ExpenseView>> getData() {
        data = new MutableLiveData<>();
        loadData();
        data.setValue(expenseViews);
        return data;
    }

    public void loadData() {
        expenseViews = Arrays.stream(database.getExpenseDao().getAllExpenses())
                .map(ExpenseView::new)
                .collect(Collectors.toList());
    }

    public MutableLiveData<List<ExpenseView>> search(String expenseType) {
        Expense[] result;
        if (expenseType == null || expenseType.isEmpty()) {
            result = database.getExpenseDao().getAllExpenses();
        } else result = database.getExpenseDao().getExpensesByTypeNameLike("%" + expenseType + "%");
        data.setValue(Arrays.stream(result).map(ExpenseView::new).collect(Collectors.toList()));
        return data;
    }

    public MutableLiveData<List<ExpenseView>> getByTripId(int tripId) {
        MutableLiveData<List<ExpenseView>> rawData = new MutableLiveData<>();
        rawData.setValue(Arrays.stream(database
                .getExpenseDao()
                .getExpensesByTripId(tripId))
                .map(ExpenseView::new).collect(Collectors.toList()));
        return rawData;
    }

    public MutableLiveData<Expense> getById(int expenseId) {
        MutableLiveData<Expense> rawData = new MutableLiveData<>();
        rawData.setValue(database.getExpenseDao().getExpenseById(expenseId));
        return rawData;
    }

    public LiveData<String> addExpense(Expense expense) {
        MutableLiveData<String> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                database.getExpenseDao().insert(expense);
                result.postValue(null);
            } catch (Exception e) {
                result.postValue(e.getMessage());
            }
        }).start();
        return result;
    }

    public LiveData<String> updateExpense(Expense expense) {
        MutableLiveData<String> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                database.getExpenseDao().update(expense);
                result.postValue(null);
            } catch (Exception e) {
                result.postValue(e.getMessage());
            }
        }).start();
        return result;
    }

    public LiveData<String> deleteExpense(Expense expense) {
        MutableLiveData<String> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                database.getExpenseDao().delete(expense);
                result.postValue(null);
            } catch (Exception e) {
                result.postValue(e.getMessage());
            }
        }).start();
        return result;
    }
    public LiveData<String> deleteExpense(int expenseId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                database.getExpenseDao().deleteExpenseById(expenseId);
                result.postValue(null);
            } catch (Exception e) {
                result.postValue(e.getMessage());
            }
        }).start();
        return result;
    }
    public LiveData<Double> getTotalAmount(int tripId) {
        MutableLiveData<Double> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                result.postValue(database.getExpenseDao().getTotalAmount(tripId));
            } catch (Exception e) {
                result.postValue(0.0);
            }
        }).start();
        return result;
    }
    public LiveData<Double> getTotalAmount(int tripId, String expenseDate) {
        MutableLiveData<Double> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                result.postValue(database.getExpenseDao().getTotalAmount(tripId, expenseDate));
            } catch (Exception e) {
                result.postValue(0.0);
            }
        }).start();
        return result;
    }
    // row count
    public LiveData<Integer> getRowsCount(int tripId) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                result.postValue(database.getExpenseDao().getRowsCount(tripId));
            } catch (Exception e) {
                result.postValue(0);
            }
        }).start();
        return result;
    }
}
