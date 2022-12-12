package com.tqb.m_expense.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.tqb.m_expense.Utils.Payload;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@Entity
public class Trip{
    @PrimaryKey(autoGenerate = true)
    private int tripId;
    @ColumnInfo
    private String tripName;
    private String tripDestination;
    @ColumnInfo
    private String startDate;
    @ColumnInfo
    private int tripDate;
    @ColumnInfo(defaultValue = "0")
    private double tripBudget;
    @ColumnInfo(defaultValue = "$")
    private String tripCurrency;
    @ColumnInfo
    private boolean tripIsFinished;
    private boolean needAssessment;
    @ColumnInfo(defaultValue = "")
    private String tripDescription;

    public Trip() {}

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

    public String getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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

    public boolean isTripIsFinished() {
        return tripIsFinished;
    }

    public void setTripIsFinished(boolean tripIsFinished) {
        this.tripIsFinished = tripIsFinished;
    }

    public boolean isNeedAssessment() {
        return needAssessment;
    }

    public void setNeedAssessment(boolean needAssessment) {
        this.needAssessment = needAssessment;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public Payload.Data toPayload() {
        return new Payload.Data(this);
    }
}
