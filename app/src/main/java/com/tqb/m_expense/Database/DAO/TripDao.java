package com.tqb.m_expense.Database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tqb.m_expense.Database.Entity.Trip;

@Dao
public interface TripDao extends BaseDao<Trip> {

    @Query("DELETE FROM trip WHERE tripId = :tripId")
    void deleteTripById(int tripId);
    @Query("SELECT * FROM trip")
    Trip[] getAllTrips();
    @Query("SELECT * FROM trip WHERE tripId = :tripId")
    Trip getTripById(int tripId);
    @Query("SELECT * FROM trip WHERE tripName = :tripName")
    Trip[] getTripsByName(String tripName);
    @Query("SELECT * FROM trip WHERE tripName LIKE :tripName")
    Trip[] getTripsByNameLike(String tripName);
}
