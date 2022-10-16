package com.tqb.m_expense.Database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tqb.m_expense.Database.Entity.Expense;

@Dao
public interface ExpenseDao extends BaseDao<Expense> {

    @Query("SELECT * FROM expense")
    Expense[] getAllExpenses();
    @Query("SELECT * FROM expense WHERE expenseId = :expenseId")
    Expense getExpenseById(int expenseId);
    @Query("SELECT * FROM expense WHERE tripId = :tripId")
    Expense[] getExpensesByTripId(int tripId);
    @Query("SELECT * FROM expense WHERE expenseTypeId = :expenseTypeId")
    Expense[] getExpensesByExpenseTypeId(int expenseTypeId);
    @Query("SELECT * FROM expense WHERE expenseTypeName LIKE :expenseTypeName")
    Expense[] getExpensesByTypeNameLike(String expenseTypeName);
    @Query("DELETE FROM expense WHERE expenseId = :expenseId")
    void deleteExpenseById(int expenseId);
    // get amount by date
    @Query("SELECT SUM(expenseAmount) FROM expense WHERE expenseDate = :expenseDate AND tripId = :tripId")
    double getTotalAmount(int tripId, String expenseDate);
    // get total expense
    @Query("SELECT SUM(expenseAmount) FROM expense WHERE tripId = :tripId")
    double getTotalAmount(int tripId);
    // count
    @Query("SELECT COUNT(expenseId) FROM expense")
    int getRowsCount();
    @Query("SELECT COUNT(expenseId) FROM expense WHERE tripId = :tripId")
    int getRowsCount(int tripId);
}
