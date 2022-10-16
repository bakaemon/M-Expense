package com.tqb.m_expense;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Trip;
import com.tqb.m_expense.Utils.HttpUtils;
import com.tqb.m_expense.Utils.Payload;
import com.tqb.m_expense.Utils.LoadingScreen;
import com.tqb.m_expense.View.Adapter.SettingRecycleViewAdapter;
import com.tqb.m_expense.View.Model.TripViewModel;
import com.tqb.m_expense.databinding.FragmentSettingBinding;

import org.json.JSONObject;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;
    private TripViewModel tripViewModel;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        tripViewModel.setDatabase(DatabaseHelper.getDatabase(getContext()));
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        SettingRecycleViewAdapter adapter = new SettingRecycleViewAdapter(getContext());
        adapter.addClickListener((view, position) -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Export to Server")
                    .setMessage("Are you sure you want to export all trips to server?")
                    .setPositiveButton("Yes", (dialog, which) -> uploadTrips())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        binding.settingRecycleView.setAdapter(adapter);
        binding.settingRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.settingRecycleView.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.HORIZONTAL));
        return binding.getRoot();
    }

    public void uploadTrips() {
        Payload payload = new Payload();
        List<Payload.Data> tripPayloadData = new ArrayList<>();
        tripViewModel.getRaw().observe(getViewLifecycleOwner(), trips -> {
            for (Trip trip : trips) {
                tripPayloadData.add(trip.toPayload());
            }
            payload.setDetailList(tripPayloadData);
            payload.setUserId("wm123");
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
    private void responseDialog(String response) {
        responseDialog(response, null);
    }
    private void responseDialogRaw(String response) {
        try {
            JSONObject json = new JSONObject(response);
            new AlertDialog.Builder(getContext())
                    .setTitle("Response")
                    .setMessage(json.toString(4))
                    .setNeutralButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
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