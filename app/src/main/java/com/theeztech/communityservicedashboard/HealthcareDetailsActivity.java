package com.theeztech.communityservicedashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthcareDetailsActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorClickListener {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerServices, recyclerDoctors;
    private LinearProgressIndicator progressBar;
    
    private List<HealthcareService> serviceList;
    private List<Doctor> doctorList;
    private ServiceAdapter serviceAdapter;
    private DoctorAdapter doctorAdapter;
    
    private String healthcareId, healthcareName;

    private static final String URL_GET_SERVICES = "https://csd.theeztech.xyz/api/get_services.php";
    private static final String URL_GET_DOCTORS = "https://csd.theeztech.xyz/api/get_doctors.php";
    private static final String URL_BOOK_SERIAL = "https://csd.theeztech.xyz/api/add_serial.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_healthcare_details);

        healthcareId = getIntent().getStringExtra("healthcare_id");
        healthcareName = getIntent().getStringExtra("healthcare_name");

        initViews();
        setupToolbar();
        setupRecyclers();
        
        if (healthcareId != null) {
            fetchServices();
            fetchDoctors();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerServices = findViewById(R.id.recyclerServices);
        recyclerDoctors = findViewById(R.id.recyclerDoctors);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        if (healthcareName != null) toolbar.setTitle(healthcareName);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclers() {
        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(serviceList);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this));
        recyclerServices.setAdapter(serviceAdapter);

        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList, this);
        doctorAdapter.setShowBookButton(true); // Enable Book button for users
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerDoctors.setAdapter(doctorAdapter);
    }

    private void fetchServices() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_SERVICES,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("services");
                            serviceList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                serviceList.add(new HealthcareService(
                                        obj.getString("id"),
                                        obj.getString("service_name"),
                                        obj.getString("description"),
                                        obj.getString("price")
                                ));
                            }
                            serviceAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("healthcare_id", healthcareId);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_DOCTORS,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("doctors");
                            doctorList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                doctorList.add(new Doctor(
                                        obj.getString("id"),
                                        obj.getString("doctor_name"),
                                        obj.getString("specialization"),
                                        obj.getString("qualification"),
                                        obj.getString("phone"),
                                        obj.getString("visit_fee"),
                                        obj.getString("chamber_time"),
                                        obj.optString("chamber_day", ""),
                                        null
                                ));
                            }
                            doctorAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("healthcare_id", healthcareId);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onDoctorClick(Doctor doctor) {
        showBookSerialDialog(doctor);
    }

    private void showBookSerialDialog(Doctor doctor) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_book_serial, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etPatientName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPatientPhone);
        TextInputEditText etDate = dialogView.findViewById(R.id.etDate);

        SharedPreferences prefs = getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        etName.setText(prefs.getString(MainActivity.KEY_NAME, ""));
        etPhone.setText(prefs.getString(MainActivity.KEY_PHONE, ""));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));

        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etDate.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Book Serial for " + doctor.getName())
                .setView(dialogView)
                .setPositiveButton("Book Now", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
                    String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";

                    if (!name.isEmpty() && !phone.isEmpty() && !date.isEmpty()) {
                        if (validateBookingTime(doctor, date)) {
                            bookSerial(doctor, name, phone, date);
                        }
                    } else {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateBookingTime(Doctor doctor, String selectedDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date selectedDate = sdf.parse(selectedDateStr);
            if (selectedDate == null) return false;

            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            // 1. Check if the doctor visits on this day
            String dayName = new SimpleDateFormat("EEE", Locale.ENGLISH).format(selectedDate);
            if (!doctor.getChamberDay().contains(dayName)) {
                Toast.makeText(this, "Doctor does not visit on this day (" + dayName + ")", Toast.LENGTH_LONG).show();
                return false;
            }

            // 2. Find start time for this specific day
            String startTimeStr = "";
            String fullTime = doctor.getChamberTime();
            if (fullTime.contains(dayName + ": ")) {
                String dayPart = fullTime.split(dayName + ": ")[1];
                startTimeStr = dayPart.split("-")[0].trim();
            }

            if (startTimeStr.isEmpty()) return true;

            int hour = 0;
            int minute = 0;
            try {
                String timeOnly = startTimeStr.replaceAll("[a-zA-Z]", "");
                String ampm = startTimeStr.replaceAll("[0-9:]", "").toUpperCase();
                
                if (timeOnly.contains(":")) {
                    hour = Integer.parseInt(timeOnly.split(":")[0]);
                    minute = Integer.parseInt(timeOnly.split(":")[1]);
                } else {
                    hour = Integer.parseInt(timeOnly);
                }

                if (ampm.equals("PM") && hour < 12) hour += 12;
                if (ampm.equals("AM") && hour == 12) hour = 0;
            } catch (Exception e) {
                return true;
            }

            Calendar startCal = (Calendar) selectedCal.clone();
            startCal.set(Calendar.HOUR_OF_DAY, hour);
            startCal.set(Calendar.MINUTE, minute);
            startCal.set(Calendar.SECOND, 0);

            Calendar openCal = (Calendar) startCal.clone();
            openCal.add(Calendar.HOUR_OF_DAY, -24);

            long now = System.currentTimeMillis();
            
            if (now < openCal.getTimeInMillis()) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
                Toast.makeText(this, "Booking starts from " + timeFormat.format(openCal.getTime()), Toast.LENGTH_LONG).show();
                return false;
            }

            if (now > startCal.getTimeInMillis()) {
                Toast.makeText(this, "Visiting time has already started/passed for this day.", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;

        } catch (Exception e) {
            return true;
        }
    }

    private void bookSerial(Doctor doctor, String patientName, String patientPhone, String date) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_BOOK_SERIAL,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            String serialNo = obj.optString("serial_no", "");
                            String msg = "Serial booked successfully!";
                            if (!serialNo.isEmpty()) msg += " Your Serial No: " + serialNo;
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, obj.optString("message", "Booking failed"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Booking: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doctor_id", doctor.getId());
                params.put("patient_name", patientName);
                params.put("patient_phone", patientPhone);
                params.put("serial_date", date);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
