package com.tqb.m_expense;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Utils.CameraUtil;
import com.tqb.m_expense.View.Model.ExpenseViewModel;
import com.tqb.m_expense.databinding.FragmentExpenseDetailBinding;

import java.util.Objects;


public class ExpenseDetailFragment extends Fragment {
    private FragmentExpenseDetailBinding binding;
    private int expenseId;
    private ExpenseViewModel expenseViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseId = getArguments().getInt("expense_id");
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.setDatabase(DatabaseHelper.getDatabase(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpenseDetailBinding.inflate(inflater, container, false);
        binding.comment.setEnabled(false);
        expenseViewModel.getById(expenseId).observe(getViewLifecycleOwner(), expense -> {
            binding.toolbarExpenseDetail.setTitle(expense.getExpenseType().getExpenseTypeName());
            binding.budget.setText(String.valueOf(expense.getExpenseAmount()));
            binding.date.setText(expense.getExpenseDate());
            binding.comment.setText(expense.getExpenseDescription());
            String latLong = expense.getExpenseLocation();
            binding.latLong.setText(String.format("Lat: %.2f, Long: %.2f",
                    Float.parseFloat(latLong.split(",")[0]),
                    Float.parseFloat(latLong.split(",")[1])));
            if (expense.getExpenseImage() != null) {
                if (expense.getExpenseImage().length() > 0) {
                    binding.imageView.setImageBitmap(CameraUtil.decodeBase64(expense.getExpenseImage()));
                }
            }
        });
        binding.toolbarExpenseDetail.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.add("DELETE")
                        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                        .setOnMenuItemClickListener(item -> {
                            expenseViewModel.deleteExpense(expenseId).observe(getViewLifecycleOwner(), error -> {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Confirm deletion")
                                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                        .setPositiveButton("Yes", ((dialog, which) -> {
                                            if (error == null) {

                                                requireActivity().onBackPressed();
                                            } else
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                        })).show();
                            });
                            return false;
                        });
            }

                @Override
                public boolean onMenuItemSelected (@NonNull MenuItem menuItem){
                    return false;
                }
            });
        binding.fabEdit.setOnClickListener(v ->

            {
                Bundle bundle = new Bundle();
                bundle.putString("edit_mode", "edit");
                bundle.putInt("expense_id", expenseId);
                bundle.putString("editor_label", "Edit Expense");
                NavHostFragment.findNavController(ExpenseDetailFragment.this)
                        .navigate(R.id.action_expenseDetailFragment_to_expenseEditorFragment, bundle);
            });
        return binding.getRoot();
        }
        @Override
        public void onActivityCreated (@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            Objects.requireNonNull(((ExpenseActivity) requireActivity()).getSupportActionBar()).hide();
        }
        @Override
        public void onStop () {
            super.onStop();
            Objects.requireNonNull(((ExpenseActivity) requireActivity()).getSupportActionBar()).show();
        }
    }