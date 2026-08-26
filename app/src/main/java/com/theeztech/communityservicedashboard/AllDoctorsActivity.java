package com.theeztech.communityservicedashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AllDoctorsActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorClickListener {

    private RecyclerView recyclerDoctors;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private MaterialToolbar toolbar;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private static final String URL_GET_ALL_DOCTORS = "https://csd.theeztech.xyz/api/get_all_doctors.php";
    private static final String URL_BOOK_SERIAL = "https://csd.theeztech.xyz/api/add_serial.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_doctors);

        initViews();
        setupToolbar();
        setupRecyclerView();

        fetchAllDoctors();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerDoctors = findViewById(R.id.recyclerAllDoctors);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(doctorList, this);
        adapter.setShowBookButton(true);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerDoctors.setAdapter(adapter);
    }

    private void fetchAllDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_ALL_DOCTORS,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray array;
                        String trimmed = response.trim();
                        if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            array = obj.optJSONArray("data");
                            if (array == null) array = obj.optJSONArray("doctors");
                        } else {
                            array = new JSONArray(trimmed);
                        }

                        if (array != null) {
                            doctorList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                doctorList.add(new Doctor(
                                        obj.optString("id"),
                                        obj.optString("doctor_name"),
                                        obj.optString("specialization"),
                                        obj.optString("qualification"),
                                        obj.optString("phone"),
                                        obj.optString("visit_fee"),
                                        obj.optString("chamber_time")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }

                        if (doctorList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (doctorList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Doctors: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (doctorList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

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
                        bookSerial(doctor, name, phone, date);
                    } else {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void bookSerial(Doctor doctor, String patientName, String patientPhone, String date) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_BOOK_SERIAL,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            String serialNo = obj.optString("serial_no", "");
                            String msg = "Serial booked successfully!";
                            if (!serialNo.isEmpty()) msg += " Your Serial No: " + serialNo;
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, obj.optString("message", "Booking failed"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
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
