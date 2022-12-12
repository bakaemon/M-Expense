package com.tqb.m_expense.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class Expense{
    @PrimaryKey(autoGenerate = true)
    private int expenseId;
    @Embedded
    private ExpenseType expenseType;
    @ColumnInfo
    private String expenseDate;
    @ColumnInfo
    private double expenseAmount;
    @ColumnInfo
    private String expenseDescription;
    private String expenseImage;
    private String expenseLocation;

    @Embedded
    private Trip trip;

    public Expense() {}

    public String getExpenseImage() {
        return expenseImage;
    }

    public void setExpenseImage(String expenseImage) {
        this.expenseImage = expenseImage;
    }

    public Trip getTrip() {
        return trip;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }
    public void setExpenseType(ExpenseType expenseType) {
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

    public String getExpenseLocation() {
        return expenseLocation;
    }

    public void setExpenseLocation(String expenseLocation) {
        this.expenseLocation = expenseLocation;
    }
}
