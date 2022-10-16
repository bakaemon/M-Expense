package com.tqb.m_expense;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.View.Model.ExpenseViewModel;
import com.tqb.m_expense.View.Model.TripViewModel;
import com.tqb.m_expense.databinding.FragmentStatisticBinding;

import java.text.ParseException;
import java.util.Date;

public class StatisticFragment extends Fragment {
    private FragmentStatisticBinding binding;
    private TripViewModel tripViewModel;
    private ExpenseViewModel expenseViewModel;
    private int tripId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        tripViewModel.setDatabase(DatabaseHelper.getDatabase(requireContext()));
        expenseViewModel.setDatabase(DatabaseHelper.getDatabase(requireContext()));
        tripId = getArguments().getInt("tripId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticBinding.inflate(inflater, container, false);
        expenseViewModel.getTotalAmount(tripId).observe(getViewLifecycleOwner(), totalAmount -> {
            tripViewModel.getRawById(tripId).observe(getViewLifecycleOwner(), trip -> {
                binding.remain.setText(trip.getTripBudget() - totalAmount + trip.getTripCurrency());
                expenseViewModel.getTotalAmount(tripId, DateUtils.getCurrentDate()).observe(getViewLifecycleOwner(), value -> {
                    binding.totalUpToDate.setText(value + trip.getTripCurrency());
                });
                expenseViewModel.getRowsCount(tripId).observe(getViewLifecycleOwner(), value -> {
                    binding.totalExpense.setText(String.valueOf(value));
                    try {
                        double average = trip.getTripBudget() / trip.getTripDate();
                        binding.averagePerDay.setText(String.format("%.2f", average)+ trip.getTripCurrency());
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                binding.startDate.setText(trip.getStartDate());
                binding.riskAssessment.setText(trip.isNeedAssessment() ? "Yes" : "No");
                binding.description.setText(trip.getTripDescription());
            });
        });

        return binding.getRoot();
    }
}