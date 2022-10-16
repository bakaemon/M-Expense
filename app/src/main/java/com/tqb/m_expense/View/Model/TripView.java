package com.tqb.m_expense.View.Model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.tqb.m_expense.Database.Entity.Trip;

public class TripView {
    private int tripId;
    private String tripName;
    private int tripDate;
    private double tripBudget;
    private String tripCurrency;
    private boolean tripIsFinished;

    public TripView() {
    }
    public TripView(Trip trip) {
        this.tripId = trip.getTripId();
        this.tripName = trip.getTripName();
        this.tripDate = trip.getTripDate();
        this.tripBudget = trip.getTripBudget();
        this.tripCurrency = trip.getTripCurrency();
        this.tripIsFinished = trip.isTripIsFinished();
    }
    public int getTripId() {
        return tripId;
    }
    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public int getTripDate() {
        return tripDate;
    }

    public void setTripDate(int tripDate) {
        this.tripDate = tripDate;
    }

    public double getTripBudget() {
        return tripBudget;
    }

    public void setTripBudget(double tripBudget) {
        this.tripBudget = tripBudget;
    }

    public String getTripCurrency() {
        return tripCurrency;
    }

    public void setTripCurrency(String tripCurrency) {
        this.tripCurrency = tripCurrency;
    }

    public boolean isFinished() {
        return tripIsFinished;
    }

    public void setFinished(boolean finished) {
        tripIsFinished = finished;
    }
}
