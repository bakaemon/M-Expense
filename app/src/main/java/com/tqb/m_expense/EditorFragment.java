package com.tqb.m_expense;

import static com.tqb.m_expense.Utils.InputUtils.checkInput;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.DAO.TripDao;
import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Trip;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.databinding.FragmentEditorBinding;

import java.util.Date;

public class EditorFragment extends Fragment {
    private FragmentEditorBinding binding;
    private TripDao tripDao;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = DatabaseHelper.getDatabase(requireContext());
        tripDao = db.getTripDao();
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditorBinding.inflate(inflater, container, false);
        if (getArguments() == null) {
            Toast.makeText(getContext(), "Error: Incorrect arguments.", Toast.LENGTH_SHORT).show();
            navigate(R.id.action_editorFragment_to_TripFragment, null);
        }
        binding.startDate.setText(DateUtils.getCurrentDate());
        binding.startDate.setOnClickListener(v -> {
            DateUtils.showDatePickerDialog(requireContext(), binding.startDate);
        });
        binding.endDate.setOnClickListener(v -> {
            DateUtils.showDatePickerDialog(requireContext(), binding.endDate);
        });

        switch (getArguments().getString("editorMode")) {
            case "add":
                requireActivity().setTitle("Add Trip");
                break;
            case "edit":
                requireActivity().setTitle("Edit Trip");
                break;
            default:
                Toast.makeText(getContext(), "Error: Incorrect arguments.", Toast.LENGTH_SHORT).show();
                navigate(R.id.action_editorFragment_to_TripFragment, null);
        }
        binding.endDate.setText(DateUtils.getFormattedDate(DateUtils.addDays(new Date(), 7)));

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        switch (getArguments().getString("editorMode")) {
            case "add":
                binding.fabSubmit.setOnClickListener(v -> {
                    try {
                        submitAdd();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "edit":
                try {
                    Trip trip = tripDao.getTripById(getArguments().getInt("tripId"));
                    binding.tripName.setText(trip.getTripName());
                    binding.tripCurrency.setText(trip.getTripCurrency());
                    binding.tripBudget.setText(String.valueOf(trip.getTripBudget()));
                    binding.startDate.setText(trip.getStartDate());
                    binding.endDate.setText(DateUtils.getFormattedDate(DateUtils
                            .addDays(DateUtils.getDate(trip.getStartDate()),
                            trip.getTripDate())));
                    binding.riskAssessment.check(trip.isNeedAssessment() ? R.id.rsYes : R.id.rsNo);
                    binding.fabSubmit.setOnClickListener(v -> {
                        try {
                            submitEdit();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    private void submitAdd() throws Exception {
        if (!checkInput(getContext(), binding.tripName, binding.startDate, binding.endDate, binding.riskAssessment)) {
            return;
        }
        Trip trip = new Trip();
        trip.setTripName(binding.tripName.getText().toString());
        if (binding.startDate.getText().toString().isEmpty()) {
            throw new Exception("Start date is null.");
        }
        Date startDate;
        if (binding.startDate.getText().toString().equals("Today")) {
            trip.setStartDate(DateUtils.getFormattedDate(new Date()));
            startDate = new Date();
        } else if (DateUtils.isValidDate(binding.startDate.getText().toString())) {
            trip.setStartDate(binding.startDate.getText().toString());
            startDate = DateUtils.getDate(binding.startDate.getText().toString());
        } else throw new Exception("Start date is invalid.");
        Date endDate = DateUtils.getDate(binding.endDate.getText().toString());
        trip.setTripDate(DateUtils.getDateDiff(startDate, endDate, DateUtils.UNIT_DAY));
        if (!binding.tripCurrency.getText().toString().isEmpty()) {
            trip.setTripCurrency(binding.tripCurrency.getText().toString());
        } else {
            trip.setTripCurrency("$");
        }
        if (!binding.tripBudget.getText().toString().isEmpty()) {
            trip.setTripBudget(Double.parseDouble(binding.tripBudget.getText().toString()));
        } else {
            trip.setTripBudget(0);
        }
        trip.setNeedAssessment(binding.riskAssessment.getCheckedRadioButtonId() == R.id.rsYes);
        tripDao.insert(trip);
        Toast.makeText(getContext(), "Trip added successfully.", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(EditorFragment.this)
                .navigate(R.id.action_editorFragment_to_TripFragment);
    }

    private void submitEdit() throws Exception {
        if (!checkInput(getContext(), binding.tripName, binding.startDate, binding.endDate, binding.riskAssessment)) {
            //Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        assert getArguments() != null;
        Trip trip = tripDao.getTripById(getArguments().getInt("tripId"));
        trip.setTripName(binding.tripName.getText().toString());
        Date startDate = DateUtils.getDate(binding.startDate.getText().toString());
        trip.setStartDate(binding.startDate.getText().toString());
        Date endDate = DateUtils.getDate(binding.endDate.getText().toString());
        trip.setTripDate(DateUtils.getDateDiff(startDate, endDate, DateUtils.UNIT_DAY));
        trip.setTripBudget(Double.parseDouble(binding.tripBudget.getText().toString()));
        trip.setTripCurrency(binding.tripCurrency.getText().toString());
        trip.setNeedAssessment(binding.riskAssessment.getCheckedRadioButtonId() == R.id.rsYes);
        trip.setTripDescription(binding.tripDescription.getText().toString());
        tripDao.update(trip);
        Intent intent = new Intent();
        intent.putExtra("toast_msg", "Trip updated successfully.");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().setResult(Activity.RESULT_OK, intent);
        requireActivity().finish();
    }

    // navigation to other fragment
    public void navigate(int actionId, Bundle bundle) {
        NavHostFragment.findNavController(this)
                .navigate(actionId, bundle);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                requireActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}