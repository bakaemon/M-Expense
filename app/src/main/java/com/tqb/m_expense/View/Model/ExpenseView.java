package com.tqb.m_expense.View.Model;

import androidx.recyclerview.widget.RecyclerView;

import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.View.Adapter.ExpenseRecycleViewAdapter;

public class ExpenseView {
    public int expenseId;
    public String expenseType;
    public String expenseDate;
    public double expenseAmount;
    public String expenseDescription;

    public ExpenseView() {
    }
    public ExpenseView(Expense expense) {
        this.expenseId = expense.getExpenseId();
        this.expenseType = expense.getExpenseType().getExpenseTypeName();
        this.expenseDate = expense.getExpenseDate();
        this.expenseAmount = expense.getExpenseAmount();
        this.expenseDescription = expense.getExpenseDescription();
    }
    public ExpenseView(int expenseId, String expenseType, String expenseDate, double expenseAmount, String expenseDescription) {
        this.expenseId = expenseId;
        this.expenseType = expenseType;
        this.expenseDate = expenseDate;
        this.expenseAmount = expenseAmount;
        this.expenseDescription = expenseDescription;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseDescription() {
        return expenseDescription;
    }

    public void setExpenseDescription(String expenseDescription) {
        this.expenseDescription = expenseDescription;
    }

    public void bind(ExpenseRecycleViewAdapter.ViewHolder holder) {
        holder.expenseType.setText(expenseType);
        holder.expenseAmount.setText(String.valueOf(expenseAmount));
    }

    public int getId() {
        return expenseId;
    }
//    public void setId(int id) {
//        this.expenseId = id;
//    }
}
