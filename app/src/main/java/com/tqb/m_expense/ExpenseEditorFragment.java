package com.tqb.m_expense;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.Database.Entity.ExpenseType;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.Utils.InputUtils;
import com.tqb.m_expense.View.Adapter.ExpenseTypeSpinnerAdapter;
import com.tqb.m_expense.View.Model.ExpenseTypeViewModel;
import com.tqb.m_expense.View.Model.ExpenseViewModel;
import com.tqb.m_expense.View.Model.TripViewModel;
import com.tqb.m_expense.databinding.FragmentExpenseEditorBinding;

import java.util.Objects;


public class ExpenseEditorFragment extends Fragment {
    private FragmentExpenseEditorBinding binding;
    private ExpenseViewModel expenseViewModel;
    private ExpenseTypeViewModel expenseTypeViewModel;
    private TripViewModel tripViewModel;
    private String editMode;
    private int tripId;
    private int expenseId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editMode = getArguments().getString("edit_mode");
            String editorLabel = getArguments().getString("editor_label");
            tripId = getArguments().getInt("trip_id");
            expenseId = getArguments().getInt("expense_id");

            // setup database and view models
            AppDatabase database = DatabaseHelper.getDatabase(getContext());
            expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
            expenseViewModel.setDatabase(database);
            expenseTypeViewModel = new ViewModelProvider(this).get(ExpenseTypeViewModel.class);
            expenseTypeViewModel.setDatabase(database);
            tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
            tripViewModel.setDatabase(database);

            expenseTypeViewModel.prepopulate();
            requireActivity().setTitle(editorLabel);
        }
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExpenseEditorBinding.inflate(inflater, container, false);
        binding.expenseDate.setShowSoftInputOnFocus(false);
        binding.expenseDate.setOnClickListener(view -> {
            DateUtils.showDatePickerDialog(requireContext(), binding.expenseDate);
        });
        expenseTypeViewModel.getData().observe(getViewLifecycleOwner(), expenseTypes -> {
            binding.expenseType.setAdapter(new ExpenseTypeSpinnerAdapter(requireContext(), expenseTypes));
        });
        if (editMode.contentEquals("edit")) {
            requireActivity().setTitle("Edit Expense");
            expenseViewModel.getById(expenseId).observe(getViewLifecycleOwner(), expense -> {
                binding.expenseType.setSelection(expense.getExpenseType().getExpenseTypeId() - 1);
                binding.amount.setText(String.valueOf(expense.getExpenseAmount()));
                binding.expenseDate.setText(expense.getExpenseDate());
                binding.description.setText(expense.getExpenseDescription());
                binding.submitButton.setOnClickListener((v) -> {
                    InputUtils.checkInput(getContext(),binding.amount, binding.expenseDate);
                    v.setAlpha(0.5f);
                    v.setEnabled(false);
                    expense.setExpenseType((ExpenseType) binding.expenseType.getSelectedItem());
                    expense.setExpenseAmount(Double.parseDouble(binding.amount.getText().toString()));
                    expense.setExpenseDate(binding.expenseDate.getText().toString());
                    expense.setExpenseDescription(binding.description.getText().toString());
                    expenseViewModel.updateExpense(expense).observe(getViewLifecycleOwner(), error -> {
                        if (error == null) {
                            requireActivity().onBackPressed();
                        } else {
                            v.setAlpha(1f);
                            v.setEnabled(true);
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();

                        }
                    });
                });
            });
        } else if (editMode.contentEquals("add")) {
            requireActivity().setTitle("Add Expense");
            binding.expenseDate.setText(DateUtils.getCurrentDate());
            binding.submitButton.setOnClickListener((v) -> {
                v.setAlpha(0.5f);
                v.setEnabled(false);
                Expense expense = new Expense();
                expense.setExpenseType((ExpenseType) binding.expenseType.getSelectedItem());
                expense.setExpenseAmount(Double.parseDouble(binding.amount.getText().toString()));
                expense.setExpenseDate(binding.expenseDate.getText().toString());
                expense.setExpenseDescription(binding.description.getText().toString());
                tripViewModel.getRawById(tripId).observe(getViewLifecycleOwner(), trip -> {
                    if (trip == null) {
                        Toast.makeText(requireContext(), "Error: Trip is null!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        expense.setTrip(trip);
                        expenseViewModel.addExpense(expense).observe(getViewLifecycleOwner(), error -> {
                            if (error == null) {
                                Toast.makeText(requireContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed();
                            } else {
                                v.setAlpha(1f);
                                v.setEnabled(true);
                                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            });
        }
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}