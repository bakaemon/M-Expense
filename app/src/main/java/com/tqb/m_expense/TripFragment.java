package com.tqb.m_expense;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Trip;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.Utils.HttpUtils;
import com.tqb.m_expense.Utils.LoadingScreen;
import com.tqb.m_expense.Utils.Payload;
import com.tqb.m_expense.View.Adapter.TripRecycleViewAdapter;
import com.tqb.m_expense.View.Model.TripView;
import com.tqb.m_expense.View.Model.TripViewModel;
import com.tqb.m_expense.databinding.FragmentTripBinding;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TripFragment extends Fragment {

    private FragmentTripBinding binding;
    private TripViewModel tripViewModel;
    private final ActivityResultLauncher<Intent> expenseActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent resultIntent = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (resultIntent.getBooleanExtra("show_toast", true)) {
                        Toast.makeText(requireContext(), resultIntent.getStringExtra("toast_msg"), Toast.LENGTH_SHORT).show();
                    }
                    // reload rv
                    tripViewModel.getData().observe(getViewLifecycleOwner(), this::loadRecycleView);
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTripBinding.inflate(inflater, container, false);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        tripViewModel.setDatabase(DatabaseHelper.getDatabase(getContext()));
        tripViewModel.getData().observe(getViewLifecycleOwner(), this::loadRecycleView);

        return binding.getRoot();

}


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabAddTrip.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("editorMode", "add");
            bundle.putString("editor_label", "Add Trip");
            navigate(R.id.action_TripFragment_to_editorFragment, bundle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void navigate(int actionId, Bundle bundle) {
        NavHostFragment.findNavController(this)
                .navigate(actionId, bundle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                tripViewModel.search(query).observe(getViewLifecycleOwner(), tripViews -> {
                    ((TripRecycleViewAdapter) Objects
                            .requireNonNull(binding.tripRecyclerView.getAdapter()))
                            .setTrips(tripViews);
                });
                return true;
            }
        });
        menu.findItem(R.id.action_filter).setOnMenuItemClickListener(item -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
            ((EditText)dialogView.findViewById(R.id.compare_date)).setText(DateUtils.getFormattedDate(new Date()));
            ((EditText) dialogView.findViewById(R.id.compare_date)).setShowSoftInputOnFocus(false);
            ((EditText) dialogView.findViewById(R.id.compare_date)).setText(DateUtils.getFormattedDate(new Date()));
            dialogView.findViewById(R.id.compare_date).setOnClickListener(v -> {
                DateUtils.showDatePickerDialog(requireContext(), dialogView.findViewById(R.id.compare_date));
            });
           new AlertDialog.Builder(requireContext())
                   .setTitle("Filter")
                   .setView(dialogView)
                   .setPositiveButton("OK", (dialog, which) -> {
                       CheckBox budgetFilter = dialogView.findViewById(R.id.budgetFilter);
                       CheckBox dateFilter = dialogView.findViewById(R.id.dateFilter);
                       RadioGroup isFinishedFilter = dialogView.findViewById(R.id.isFinishedFilter);
                       Spinner compareBudgetOption = dialogView.findViewById(R.id.compare_budget_option);
                       Spinner compareDateOption = dialogView.findViewById(R.id.compare_date_option);
                       EditText compareBudget = dialogView.findViewById(R.id.compare_budget);
                       EditText compareDate = dialogView.findViewById(R.id.compare_date);
                       tripViewModel.filter(tripView -> {
                            boolean isBudgetFilter = budgetFilter.isChecked();
                            boolean isDateFilter = dateFilter.isChecked();
                            boolean isFinishedFilterChecked = isFinishedFilter.getCheckedRadioButtonId() == R.id.isFinished;
                            boolean isNotFinishedFilterChecked = isFinishedFilter.getCheckedRadioButtonId() == R.id.isNotFinished;
                            boolean isFinished = tripView.isTripIsFinished();
                            boolean isNotFinished = !tripView.isTripIsFinished();
                            boolean isBudgetCompare = compareBudgetOption.getSelectedItemPosition() == 0;
                            boolean isDateCompare = compareDateOption.getSelectedItemPosition() == 0;
                            boolean isBudgetCompareValid = true;
                            boolean isDateCompareValid = true;
                            if (isBudgetFilter) {
                                 try {
                                      double compareBudgetValue = Double.parseDouble(compareBudget.getText().toString());
                                      if (isBudgetCompare) {
                                        isBudgetCompareValid = tripView.getTripBudget() >= compareBudgetValue;
                                      } else {
                                        isBudgetCompareValid = tripView.getTripBudget() <= compareBudgetValue;
                                      }
                                 } catch (NumberFormatException e) {
                                      isBudgetCompareValid = false;
                                 }
                            }
                            if (isDateFilter) {
                                 try {
                                      Date compareDateValue = DateUtils.getDate(compareDate.getText().toString());
                                      Date startDate = DateUtils.getDate(tripView.getStartDate());
                                      if (isDateCompare) {
                                        isDateCompareValid = DateUtils.getDateDiff(startDate, compareDateValue, "days") >= 0;
                                      } else {
                                        isDateCompareValid = DateUtils.getDateDiff(startDate, compareDateValue, "days") <= 0;
                                      }
                                 } catch (Exception e) {
                                      isDateCompareValid = false;
                                 }
                            }
                            return (!isBudgetFilter || isBudgetCompareValid) &&
                                      (!isDateFilter || isDateCompareValid) &&
                                      (!isFinishedFilterChecked || isFinished) &&
                                      (!isNotFinishedFilterChecked || isNotFinished);
                          }).observe(getViewLifecycleOwner(), tripViews -> {
                            ((TripRecycleViewAdapter) Objects
                                      .requireNonNull(binding.tripRecyclerView.getAdapter()))
                                      .setTrips(tripViews);
                          });

                       dialog.dismiss();
                   })
                   .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                   .show();
           return true;
        });
        menu.findItem(R.id.action_upload).setOnMenuItemClickListener(item -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Upload")
                    .setMessage("Are you sure you want to upload?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        uploadTrips();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });
    }

    private void loadRecycleView(List<TripView> tripViews) {
        TripRecycleViewAdapter adapter = new TripRecycleViewAdapter(requireContext(), tripViews);
        adapter.setOnItemClickListener((view, position) -> {
            LoadingScreen
                    .beginTransition(requireContext())
                    .doTask(v -> {
                        try {
                            Intent intent = new Intent(requireContext(), ExpenseActivity.class);
                            intent.putExtra("trip_id", tripViews.get(position).getTripId());
                            expenseActivityLauncher.launch(intent);
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Error: item may does not exist. Please restart the app.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    })
                    .done(p -> {
                        // do nothing
                    }).execute();
        });
        binding.tripRecyclerView.setAdapter(adapter);
        binding.tripRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
    public void uploadTrips() {
        Payload payload = new Payload();
        List<Payload.Data> tripPayloadData = new ArrayList<>();
        tripViewModel.getRaw().observe(getViewLifecycleOwner(), trips -> {
            for (Trip trip : trips) {
                tripPayloadData.add(trip.toPayload());
            }
            payload.setDetailList(tripPayloadData);
            payload.setUserId("abcd");
            LoadingScreen
                    .<Void, Void, String>beginTransition(getContext())
                    .doTask(voids -> {
                        try {
                            return new HttpUtils()
                                    .postJson(new URL("http://cwservice1786.herokuapp.com/sendPayLoad"),
                                            payload.toJson(), "jsonpayload");
                        } catch (Exception e) {
                            errorDialog("Unknown error", e);
                        }
                        return null;
                    })
                    .done(response -> {
                        if (response != null) {
                            responseDialog(response, payload);
                        } else {
                            errorDialog("Response is null!", null);
                        }
                    })
                    .execute();
        });
    }
    private void responseDialog(String response, Payload payload) {
        try {
            JSONObject json = new JSONObject(response);
            String message = "Message: " + json.getString("message") +"\n" +
                    "User ID: " + json.getString("userid") + "\n" +
                    "Number of trips uploaded: " + json.getString("number") + "\n";
            if (payload != null) message += "Payload: \n" + payload.toJson();
            if (json.getString("uploadResponseCode").equals("SUCCESS")) {
                new AlertDialog.Builder(getContext())
                        .setTitle(json.getString("uploadResponseCode"))
                        .setMessage(message)
                        .setNeutralButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(json.getString("uploadResponseCode"))
                        .setMessage(json.getString("message"))
                        .setNeutralButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        } catch (Exception e) {
            errorDialog("Unknown error", e);
        }
    }
    private void errorDialog(String default_msg, Exception e) {
        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(e != null ? e.getMessage() : default_msg)
                .setNeutralButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}
