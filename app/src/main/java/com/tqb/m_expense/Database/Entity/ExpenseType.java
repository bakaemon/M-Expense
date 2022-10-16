package com.tqb.m_expense.Database.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense_type")
public class ExpenseType {
    @PrimaryKey(autoGenerate = true)
    private int expenseTypeId;
    private String expenseTypeName;

    public ExpenseType(String expenseTypeName) {
        this.expenseTypeName = expenseTypeName;
    }

    public int getExpenseTypeId() {
        return expenseTypeId;
    }

    public void setExpenseTypeId(int expenseTypeId) {
        this.expenseTypeId = expenseTypeId;
    }

    public String getExpenseTypeName() {
        return expenseTypeName;
    }

    public void setExpenseTypeName(String expenseTypeName) {
        this.expenseTypeName = expenseTypeName;
    }
}
