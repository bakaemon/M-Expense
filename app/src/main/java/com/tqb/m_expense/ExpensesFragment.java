package com.tqb.m_expense;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.View.Adapter.ExpenseRecycleViewAdapter;
import com.tqb.m_expense.View.Model.ExpenseView;
import com.tqb.m_expense.View.Model.ExpenseViewModel;
import com.tqb.m_expense.databinding.FragmentExpensesBinding;

import java.util.List;
import java.util.Objects;


public class ExpensesFragment extends Fragment {
    private FragmentExpensesBinding binding;
    private ExpenseViewModel expenseViewModel;
    private int tripId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        tripId = requireActivity().getIntent().getIntExtra("trip_id", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        AppDatabase db = DatabaseHelper.getDatabase(requireContext());
        expenseViewModel.setDatabase(db);
        // setup recycler view
        expenseViewModel.getByTripId(tripId).observe(getViewLifecycleOwner(), this::loadRecycleView);
        binding.fab.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("edit_mode", "add");
            bundle.putInt("trip_id", tripId);
            bundle.putString("editor_label", "Add Expense");
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_expenseFragment_to_expenseEditorFragment, bundle);
        });
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(binding.expenseRecycleView.getAdapter()).notifyDataSetChanged();
    }

    private void loadRecycleView(List<ExpenseView> expenseViews) {
        ExpenseRecycleViewAdapter adapter = new ExpenseRecycleViewAdapter(requireContext(), expenseViews);
        adapter.setOnItemClickListener((view, position) -> {
            int expenseId = expenseViews.get(position).getId();
            Bundle bundle = new Bundle();
            bundle.putInt("expense_id", expenseId);
            bundle.putInt("trip_id", tripId);
            navigate(R.id.action_expenseFragment_to_expenseDetailFragment, bundle);
        });
        binding.expenseRecycleView.setAdapter(adapter);
        binding.expenseRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    // navigating fragments method
    public void navigate(int actionId, Bundle bundle) {
        NavHostFragment.findNavController(this)
                .navigate(actionId, bundle);
    }
}