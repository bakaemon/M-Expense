package com.tqb.m_expense.Utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String UNIT_DAY = "days";
    public static final String UNIT_WEEK = "weeks";
    public static final String UNIT_MONTH = "months";
    public static final String UNIT_YEAR = "years";
    // method to get date difference
    public static int getDateDiff(Date date1, Date date2, String timeUnit) {
        long diffInMilliseconds = date2.getTime() - date1.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);
        long diffInWeeks = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) / 7;
        long diffInMonths = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) / 30;
        long diffInYears = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) / 365;
        int years = (int) diffInYears;
        int months = (int) diffInMonths;
        int weeks = (int) diffInWeeks;
        int days = (int) diffInDays;
        switch (timeUnit) {
            case UNIT_YEAR:
                return Math.abs(years);
            case UNIT_MONTH:
                return Math.abs(months);
            case UNIT_WEEK:
                return Math.abs(weeks);
            case UNIT_DAY:
                return Math.abs(days);
        }
        return 0;
    }

    // method to get date from string
    @SuppressLint("SimpleDateFormat")
    public static Date getDate(String dateString) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
    }
    // method to get current date in formatted form
    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }
    public static String getFormattedDate(String date) throws ParseException {
        return getFormattedDate(getDate(date));
    }

    // method to add days to a date
    public static Date addDays(Date date, int days) {
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
    }

    public static String getCurrentDate() {
        return getFormattedDate(new Date());
    }
    // check if the datestring can be converted to date
    public static boolean isValidDate(String dateString) {
        try {
            getDate(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public static void showDatePickerDialog(Context context, EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            String date = DATE_FORMAT.replace("yyyy", String.valueOf(year))
                    .replace("MM", String.valueOf(month + 1))
                    .replace("dd", String.valueOf(dayOfMonth));
            editText.setText(date);
        }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
