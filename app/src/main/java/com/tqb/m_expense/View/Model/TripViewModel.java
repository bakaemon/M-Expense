package com.tqb.m_expense.View.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.Entity.Trip;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TripViewModel extends ViewModel {
    public interface Filter {
        boolean filter(Trip trip);
    }

    private MutableLiveData<List<TripView>> data;
    List<TripView> tripViews = new ArrayList<>();
    AppDatabase database;

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public LiveData<List<TripView>> getData() {
        data = new MutableLiveData<>();
        loadData();
        data.setValue(tripViews);
        return data;
    }
    public LiveData<Trip> getRawById(int id) {
        MutableLiveData<Trip> rawData = new MutableLiveData<>();
        rawData.setValue(database.getTripDao().getTripById(id));
        return rawData;
    }
    public LiveData<List<Trip>> getRaw() {
        MutableLiveData<List<Trip>> rawData = new MutableLiveData<>();
        List<Trip> data = Arrays.stream(database.getTripDao().getAllTrips()).collect(Collectors.toList());
        rawData.setValue(data);
        return rawData;
    }
    public LiveData<Boolean> deleteTrip(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        result.setValue(false);
        new Thread(() -> {
            database.getTripDao().deleteTripById(id);
            result.postValue(true);
        }).start();
        return result;
    }

    public LiveData<List<TripView>> search(String tripName) {
        Trip[] result;
        if (tripName == null || tripName.isEmpty()) {
            result = database.getTripDao().getAllTrips();
        } else result = database.getTripDao().getTripsByNameLike("%" + tripName + "%");
        data.setValue(Arrays.stream(result).map(TripView::new).collect(Collectors.toList()));
        return data;
    }
    public LiveData<List<TripView>> filter(Filter filter) {
        List<Trip> filteredList = Arrays
                .stream(database.getTripDao().getAllTrips())
                .filter(filter::filter).collect(Collectors.toList());
        loadData(filteredList);
        data.setValue(tripViews);
        return data;
    }
    private void loadData() {
        tripViews = Arrays.stream(database.getTripDao().getAllTrips())
                .map(TripView::new)
                .collect(Collectors.toList());
    }
    private void loadData(List<Trip> trips) {
        tripViews = trips.stream()
                .map(TripView::new)
                .collect(Collectors.toList());
    }


}
