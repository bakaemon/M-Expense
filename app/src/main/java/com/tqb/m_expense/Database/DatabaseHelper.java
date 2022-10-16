package com.tqb.m_expense.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;

public class DatabaseHelper {
    public static final String DATABASE_NAME = "m_expense.db";
    public static AppDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context,
                        AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull androidx.sqlite.db.SupportSQLiteDatabase database) {
                        // Empty
                    }
                })
                .build();
    }
}
