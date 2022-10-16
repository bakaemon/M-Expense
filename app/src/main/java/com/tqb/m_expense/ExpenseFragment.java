package com.tqb.m_expense;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.DAO.ExpenseDao;
import com.tqb.m_expense.Database.DAO.TripDao;
import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.Database.Entity.Trip;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.Utils.NumberUtils;
import com.tqb.m_expense.View.Adapter.SectionsPagerAdapter;
import com.tqb.m_expense.View.Model.ExpenseViewModel;
import com.tqb.m_expense.View.Model.TripViewModel;
import com.tqb.m_expense.databinding.FragmentExpenseBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class ExpenseFragment extends Fragment {
    private FragmentExpenseBinding binding;
    private TripViewModel tripViewModel;
    private ExpenseViewModel expenseViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = DatabaseHelper.getDatabase(requireContext());
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        tripViewModel.setDatabase(db);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.setDatabase(db);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        int tripId = requireActivity().getIntent().getIntExtra("trip_id", 0);

        if (tripId == 0) {
            Toast.makeText(requireContext(), "Error: Incorrect arguments.", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }
        Activity activity = (ExpenseActivity)requireActivity();
        tripViewModel.getRawById(tripId).observe(getViewLifecycleOwner(), trip -> {
            binding.toolbarExpense.setTitle(trip.getTripName());
        });

        binding.toolbarExpense.setNavigationIcon(R.drawable.ic_arrow_back_24);
        binding.toolbarExpense.setNavigationOnClickListener(v -> {
            activity.onBackPressed();
        });
        binding.toolbarExpense.addMenuProvider(new MenuProvider(){

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.add("Edit");
                menu.add("Delete");
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int tripId = requireActivity().getIntent().getIntExtra("trip_id", 0);
                CharSequence title = menuItem.getTitle();
                int id = menuItem.getItemId();
                if (id == android.R.id.home) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("show_toast", false);
                    requireActivity().setResult(Activity.RESULT_OK, intent);
                    requireActivity().finish();
                    return true;
                }
                if ("Edit".contentEquals(title)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("tripId", tripId);
                    bundle.putString("editorMode", "edit");
                    bundle.putString("editor_label", "Edit Trip");
                    switchFragment(new EditorFragment(), bundle);
                    return true;
                } else if ("Delete".contentEquals(title)) {
                    new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setTitle("Delete this trip")
                            .setMessage("Are you sure you want to delete this trip?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", (dialog, which) -> {
                                tripViewModel.deleteTrip(tripId).observe(getViewLifecycleOwner(), isSuccess -> {
                                    if (isSuccess) {
                                        Intent intent = new Intent();
                                        intent.putExtra("toast_msg", "Trip deleted successfully.");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        requireActivity().setResult(Activity.RESULT_OK, intent);
                                        requireActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), "Error: Trip not deleted.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            })
                            .show();
                    return true;
                }
                return false;
            }
        });
        tripViewModel.getRawById(tripId).observe(getViewLifecycleOwner(), trip -> {
            binding.balance.setText(trip.getTripCurrency()+ " " + NumberUtils.format(trip.getTripBudget()));
        });

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        sectionsPagerAdapter.setListener(new SectionsPagerAdapter.OnFragmentInteractionListener() {
            @Override
            public Fragment onFragmentInteraction(Fragment fragment) {
                Bundle bundle = new Bundle();
                bundle.putInt("tripId", tripId);
                fragment.setArguments(bundle);
                return fragment;

            }
        });
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        return binding.getRoot();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(((ExpenseActivity) requireActivity()).getSupportActionBar()).hide();
    }
    // on created view
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((ExpenseActivity) requireActivity()).getSupportActionBar()).show();
    }
    // method to navigate fragment with bundle
    public void navigateFragment(int id, Bundle bundle) {
        NavHostFragment.findNavController(this)
                .navigate(id, bundle);
    }
    // method to switch fragment with bundle
    public void switchFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}