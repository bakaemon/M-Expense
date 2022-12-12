package com.tqb.m_expense;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.location.LocationListenerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.Database.Entity.ExpenseType;
import com.tqb.m_expense.Utils.CameraUtil;
import com.tqb.m_expense.Utils.DateUtils;
import com.tqb.m_expense.Utils.InputUtils;
import com.tqb.m_expense.Utils.LocationUtils;
import com.tqb.m_expense.Utils.PermissionUtils;
import com.tqb.m_expense.View.Adapter.ExpenseTypeSpinnerAdapter;
import com.tqb.m_expense.View.Model.ExpenseTypeViewModel;
import com.tqb.m_expense.View.Model.ExpenseViewModel;
import com.tqb.m_expense.View.Model.TripViewModel;
import com.tqb.m_expense.databinding.FragmentExpenseEditorBinding;

import java.io.File;


public class ExpenseEditorFragment extends Fragment {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FragmentExpenseEditorBinding binding;
    private ExpenseViewModel expenseViewModel;
    private ExpenseTypeViewModel expenseTypeViewModel;
    private TripViewModel tripViewModel;
    private String editMode;
    private int tripId;
    private int expenseId;
    private String encodedImage;
    private File imageFile;
    private CameraUtil cameraUtil = CameraUtil.with(this);
    private LocationManager locationManager;
    private String latLong;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentExpenseEditorBinding.inflate(getLayoutInflater());
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
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

//        Bundle mapViewBundle = outState.getBundle("MapViewBundleKey");
//        if (mapViewBundle == null) {
//            mapViewBundle = new Bundle();
//            outState.putBundle("MapViewBundleKey", mapViewBundle);
//        }

//        binding.mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        binding = FragmentExpenseEditorBinding.inflate(inflater, container, false);
//        initGoogleMap(savedInstanceState);
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
                encodedImage = expense.getExpenseImage();

                // here is the part which decode the base64 back to bitmap, to show the image to textview
                if (encodedImage != null ) {
                    if (encodedImage.length() != 0) {
                        binding.picturePreview.setImageBitmap(CameraUtil.decodeBase64(encodedImage));
                    }
                }
                setLatLong(expense.getExpenseLocation() == null ? "0.0,0.0" : expense.getExpenseLocation());
                binding.submitButton.setOnClickListener((v) -> {
                    InputUtils.checkInput(getContext(), binding.amount, binding.expenseDate);
                    v.setAlpha(0.5f);
                    v.setEnabled(false);
                    expense.setExpenseType((ExpenseType) binding.expenseType.getSelectedItem());
                    expense.setExpenseAmount(Double.parseDouble(binding.amount.getText().toString()));
                    expense.setExpenseDate(binding.expenseDate.getText().toString());
                    expense.setExpenseDescription(binding.description.getText().toString());
                    expense.setExpenseImage(encodedImage);
                    if (latLong == null) {
                        setLatLong("0.0,0.0");
                    }
                    expense.setExpenseLocation(latLong);
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
        } else if (editMode.contentEquals("add")) { // this is the insert mode
            requireActivity().setTitle("Add Expense");
            binding.expenseDate.setText(DateUtils.getCurrentDate());
            binding.submitButton.setOnClickListener((v) -> {
                if (!InputUtils.checkInput(getContext(), binding.amount, binding.expenseDate)) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if the user doesn't let the app get location, use default lat long
                if (latLong == null) {
                    setLatLong("0.0,0.0");
                }
                v.setAlpha(0.5f);
                v.setEnabled(false);
                Expense expense = new Expense();
                expense.setExpenseType((ExpenseType) binding.expenseType.getSelectedItem());
                expense.setExpenseAmount(Double.parseDouble(binding.amount.getText().toString()));
                expense.setExpenseDate(binding.expenseDate.getText().toString());
                expense.setExpenseDescription(binding.description.getText().toString());
                expense.setExpenseImage(encodedImage);
                expense.setExpenseLocation(latLong);
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
        // this is the part allow user to take picture
        binding.picturePreview.setOnClickListener(v -> {

            // ask user if they want to pick from gallery or take a picture
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose an option");
            builder.setItems(new String[]{"Take a picture", "Pick from gallery"}, (dialog, which) -> {
                // choosing either option will turn the image into base64 and save it to database
                if (which == 0) {
                    imageFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "expense_" + expenseId + "_trip_" + tripId + ".jpg");
                    // take a picture
                    cameraUtil.takePicture(imageFile, (bitmap) -> {
                        binding.picturePreview.setImageBitmap(bitmap);
                        encodedImage = CameraUtil.encodeToBase64(bitmap);
                        CameraUtil.delete(imageFile);
                    });
                } else if (which == 1) {
                    imageFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "expense_" + expenseId + "_trip_" + tripId + ".jpg");
                    // pick from gallery
                    cameraUtil.pickFromGallery(imageFile, (bitmap) -> {
                        binding.picturePreview.setImageBitmap(bitmap);
                        encodedImage = CameraUtil.encodeToBase64(bitmap);
                        CameraUtil.delete(imageFile);
                    });
                }
            });
            builder.show();
        });
        // this is the location button
        binding.autoSetLocationBtn.setOnClickListener(v -> {
            binding.latLong.setText("Loading...");
            // ask the user if they really want to allow app to have location permission
            PermissionUtils.checkPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,
                    new PermissionUtils.PermissionAskListener() {
                        @Override
                        public void onPermissionPreviouslyDenied() {
                            PermissionUtils.requestPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                                    PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE);
                        }

                        @Override
                        public void onPermissionDisabled() {
                            Toast.makeText(requireContext(),
                                    "Please enable location permission in settings",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionAsk() {
                            PermissionUtils.requestPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                                    PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE);
                        }

                        @Override
                        public void onPermissionGranted() {
                            // if granted, take location
                            LocationUtils locationUtils = LocationUtils.init(requireContext(), locationManager);
                            locationUtils.updateLocation((LocationListenerCompat) location -> {
                                Log.d("Location", "onLocationChanged: " + location);
                            });
                            // set marker on map
                            Location location = locationUtils.getLastKnownLocation();
                            setLatLong(location.getLatitude() + "," + location.getLongitude());
//                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                            binding.mapView.getMapAsync(googleMap -> {
//                                googleMap.clear();
//                                googleMap.addMarker(new MarkerOptions().position(latLng));
//                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//                            });


                        }
                    });
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        binding.mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
//        binding.mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        binding.mapView.onStop();
    }

    @Override
    public void onDestroy() {
//        binding.mapView.onDestroy();
        super.onDestroy();
        binding = null;
    }

//    private void initGoogleMap(Bundle savedInstanceState){
//        // *** IMPORTANT ***
//        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
//        // objects or sub-Bundles.
//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
//        }
//
//        binding.mapView.onCreate(mapViewBundle);
//
//        binding.mapView.getMapAsync(map -> {
//            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            map.getUiSettings().setZoomControlsEnabled(true);
//            map.getUiSettings().setCompassEnabled(true);
//            map.getUiSettings().setMyLocationButtonEnabled(true);
//            map.getUiSettings().setMapToolbarEnabled(true);
//            map.getUiSettings().setIndoorLevelPickerEnabled(true);
//            map.getUiSettings().setAllGesturesEnabled(true);
//            map.getUiSettings().setRotateGesturesEnabled(true);
//            map.getUiSettings().setScrollGesturesEnabled(true);
//            map.getUiSettings().setTiltGesturesEnabled(true);
//            map.getUiSettings().setZoomGesturesEnabled(true);
//        });
//    }

    // set lat long to textview
    private void setLatLong(String latLong){
        this.latLong = latLong;
        binding.latLong.setText(String.format("Lat: %.2f, Long: %.2f",
                Float.parseFloat(latLong.split(",")[0]),
                Float.parseFloat(latLong.split(",")[1])));
    }
}