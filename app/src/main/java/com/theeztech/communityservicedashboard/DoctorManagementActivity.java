package com.theeztech.communityservicedashboard;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorManagementActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorActionListener {

    private RecyclerView recyclerDoctors;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private MaterialToolbar toolbar;
    private FloatingActionButton fabAdd;
    private LinearProgressIndicator progressBar;

    private static final String BASE_URL = "https://csd.theeztech.xyz/api/";
    private static final String URL_GET = BASE_URL + "get_doctors.php";
    private static final String URL_ADD = BASE_URL + "add_doctor.php";
    private static final String URL_EDIT = BASE_URL + "edit_doctor.php";
    private static final String URL_DELETE = BASE_URL + "delete_doctor.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_management);

        initViews();
        setupRecyclerView();
        setupToolbar();

        fetchDoctors();

        fabAdd.setOnClickListener(v -> showDoctorDialog(null));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerDoctors = findViewById(R.id.recyclerDoctors);
        fabAdd = findViewById(R.id.fabAddDoctor);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(doctorList, this);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerDoctors.setAdapter(adapter);
    }

    private void fetchDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        String healthcareId = getSharedPreferences(MainActivity.SHARED_PREF_NAME, MODE_PRIVATE).getString("healthcare_id", "");

        if (healthcareId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_GET,
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
                                        obj.getString("chamber_time")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("healthcare_id", healthcareId);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showDoctorDialog(Doctor doctor) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_doctor, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etDoctorName);
        TextInputEditText etSpecialization = dialogView.findViewById(R.id.etSpecialty);
        TextInputEditText etQualification = dialogView.findViewById(R.id.etQualification);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etDoctorPhone);
        TextInputEditText etTime = dialogView.findViewById(R.id.etVisitingHours);
        TextInputEditText etFee = dialogView.findViewById(R.id.etFee);

        if (doctor != null) {
            etName.setText(doctor.getName());
            etSpecialization.setText(doctor.getSpecialization());
            etQualification.setText(doctor.getQualification());
            etPhone.setText(doctor.getPhone());
            etTime.setText(doctor.getChamberTime());
            etFee.setText(doctor.getVisitFee());
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(doctor == null ? "Add Doctor" : "Edit Doctor")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String spec = etSpecialization.getText() != null ? etSpecialization.getText().toString().trim() : "";
                    String qual = etQualification.getText() != null ? etQualification.getText().toString().trim() : "";
                    String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
                    String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";
                    String fee = etFee.getText() != null ? etFee.getText().toString().trim() : "";

                    if (!name.isEmpty() && !spec.isEmpty()) {
                        if (doctor == null) addDoctor(name, spec, qual, phone, fee, time);
                        else editDoctor(doctor.getId(), name, spec, qual, phone, fee, time);
                    } else {
                        Toast.makeText(this, "Name and Specialty are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addDoctor(String name, String spec, String qual, String phone, String fee, String time) {
        progressBar.setVisibility(View.VISIBLE);
        String healthcareId = getSharedPreferences(MainActivity.SHARED_PREF_NAME, MODE_PRIVATE).getString("healthcare_id", "");

        if (healthcareId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Healthcare ID not found. Please refresh Profile.", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchDoctors();
                            Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Server error");
                            Toast.makeText(this, "Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show();
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
                params.put("doctor_name", name);
                params.put("specialization", spec);
                params.put("qualification", qual);
                params.put("phone", phone);
                params.put("visit_fee", fee);
                params.put("chamber_time", time);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void editDoctor(String id, String name, String spec, String qual, String phone, String fee, String time) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_EDIT,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchDoctors();
                            Toast.makeText(this, "Doctor updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Failed to update doctor");
                            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
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
                params.put("id", id);
                params.put("doctor_name", name);
                params.put("specialization", spec);
                params.put("qualification", qual);
                params.put("phone", phone);
                params.put("visit_fee", fee);
                params.put("chamber_time", time);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void deleteDoctor(String id) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchDoctors();
                            Toast.makeText(this, "Doctor deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Failed to delete doctor");
                            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
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
                params.put("id", id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onEdit(Doctor doctor) {
        showDoctorDialog(doctor);
    }

    @Override
    public void onDelete(Doctor doctor) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Doctor")
                .setMessage("Are you sure you want to delete this doctor?")
                .setPositiveButton("Delete", (dialog, which) -> deleteDoctor(doctor.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
