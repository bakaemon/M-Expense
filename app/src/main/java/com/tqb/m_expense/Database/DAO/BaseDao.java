package com.tqb.m_expense.Database.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T t);
    @Insert
    void insertMany(T... t);
    @Update
    void update(T t);
    @Update
    void updateMany(T... t);
    @Delete
    void delete(T t);
    @Delete
    void deleteMany(T... t);


}
