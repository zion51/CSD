package com.theeztech.communityservicedashboard;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;

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

public class SerialManagementActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorClickListener {

    private RecyclerView recyclerDoctors;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private MaterialToolbar toolbar;
    private LinearProgressIndicator progressBar;

    private static final String URL_GET_DOCTORS = "https://csd.theeztech.xyz/api/get_doctors.php";
    private static final String URL_GET_SERIALS = "https://csd.theeztech.xyz/api/get_serials.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_serial_management);

        initViews();
        setupRecyclerView();
        setupToolbar();

        fetchDoctors();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Doctor");
        recyclerDoctors = findViewById(R.id.recyclerSerials);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(doctorList, this);
        adapter.setShowPendingCount(true);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerDoctors.setAdapter(adapter);
    }

    private void fetchDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        String healthcareId = getSharedPreferences(MainActivity.SHARED_PREF_NAME, MODE_PRIVATE).getString("healthcare_id", "");

        if (healthcareId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Healthcare ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_DOCTORS,
                response -> {
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
                            fetchSerialsAndCount(healthcareId);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Doctors: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
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

    private void fetchSerialsAndCount(String healthcareId) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_SERIALS,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("serials");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String status = obj.optString("status", "");
                                String drName = obj.optString("doctor_name", "");
                                String serialDate = obj.optString("serial_date", "");

                                if (status.equalsIgnoreCase("Pending") && isDateValid(serialDate)) {
                                    for (Doctor d : doctorList) {
                                        if (d.getName().equalsIgnoreCase(drName)) {
                                            d.setPendingCount(d.getPendingCount() + 1);
                                            break;
                                        }
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
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

    private boolean isDateValid(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equals("0000-00-00")) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date serialDate = sdf.parse(dateStr);
            if (serialDate == null) return false;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
            return !serialDate.before(today);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void onDoctorClick(Doctor doctor) {
        Intent intent = new Intent(this, ManageDoctorSerialsActivity.class);
        intent.putExtra("doctor_id", doctor.getId());
        intent.putExtra("doctor_name", doctor.getName());
        startActivity(intent);
    }
}
