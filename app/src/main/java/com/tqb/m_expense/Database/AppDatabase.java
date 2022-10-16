package com.tqb.m_expense.Database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tqb.m_expense.Database.DAO.ExpenseDao;
import com.tqb.m_expense.Database.DAO.ExpenseTypeDao;
import com.tqb.m_expense.Database.DAO.TripDao;
import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.Database.Entity.ExpenseType;
import com.tqb.m_expense.Database.Entity.Trip;

@Database(entities = {Trip.class, Expense.class, ExpenseType.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TripDao getTripDao();

    public abstract ExpenseDao getExpenseDao();

    public abstract ExpenseTypeDao getExpenseTypeDao();
}
