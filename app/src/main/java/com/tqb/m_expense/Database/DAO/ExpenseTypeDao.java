package com.tqb.m_expense.Database.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.tqb.m_expense.Database.Entity.ExpenseType;

@Dao
public interface ExpenseTypeDao extends BaseDao<ExpenseType> {
    @Query("SELECT * FROM expense_type")
    ExpenseType[] getAllExpenseTypes();
    @Query("SELECT * FROM expense_type WHERE expenseTypeName LIKE :searchString")
    ExpenseType[] getExpenseTypesByNameLike(String searchString);
    @Query("SELECT COUNT(*) FROM expense_type")
    int getRowsCount();
    @Query("DELETE FROM expense_type")
    void deleteAll();
}
