package com.theeztech.communityservicedashboard;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class ManageDoctorSerialsActivity extends AppCompatActivity implements SerialAdapter.OnSerialActionListener {

    private RecyclerView recyclerActive, recyclerCompleted, recyclerCancelled;
    private SerialAdapter activeAdapter, completedAdapter, cancelledAdapter;
    private List<DoctorSerial> activeList, completedList, cancelledList;
    private MaterialToolbar toolbar;
    private LinearProgressIndicator progressBar;

    private String doctorId, doctorName;

    private static final String URL_GET_SERIALS = "https://csd.theeztech.xyz/api/get_doctor_serials.php";
    private static final String URL_UPDATE_STATUS = "https://csd.theeztech.xyz/api/update_serial_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_doctor_serials);

        doctorId = getIntent().getStringExtra("doctor_id");
        doctorName = getIntent().getStringExtra("doctor_name");

        initViews();
        setupRecyclerView();
        setupToolbar();

        if (doctorId != null) {
            fetchSerials();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        if (doctorName != null) toolbar.setTitle("Serials: " + doctorName);
        recyclerActive = findViewById(R.id.recyclerActiveSerials);
        recyclerCompleted = findViewById(R.id.recyclerCompletedSerials);
        recyclerCancelled = findViewById(R.id.recyclerCancelledSerials);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        activeList = new ArrayList<>();
        activeAdapter = new SerialAdapter(activeList, this);
        recyclerActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerActive.setAdapter(activeAdapter);

        completedList = new ArrayList<>();
        completedAdapter = new SerialAdapter(completedList, this);
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompleted.setAdapter(completedAdapter);

        cancelledList = new ArrayList<>();
        cancelledAdapter = new SerialAdapter(cancelledList, this);
        recyclerCancelled.setLayoutManager(new LinearLayoutManager(this));
        recyclerCancelled.setAdapter(cancelledAdapter);
    }

    private void fetchSerials() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_SERIALS,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("serials");
                            activeList.clear();
                            completedList.clear();
                            cancelledList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String serialDate = obj.getString("serial_date");
                                String status = obj.optString("status", "");

                                if (isDateValid(serialDate)) {
                                    DoctorSerial serial = new DoctorSerial(
                                            obj.getString("id"),
                                            doctorName,
                                            obj.getString("patient_name"),
                                            obj.getString("patient_phone"),
                                            obj.getString("serial_no"),
                                            serialDate,
                                            status
                                    );
                                    
                                    if (status.equalsIgnoreCase("Cancelled")) {
                                        cancelledList.add(serial);
                                    } else if (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("Complete")) {
                                        completedList.add(serial);
                                    } else {
                                        activeList.add(serial);
                                    }
                                }
                            }
                            activeAdapter.notifyDataSetChanged();
                            completedAdapter.notifyDataSetChanged();
                            cancelledAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Fetch: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doctor_id", doctorId);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateSerialStatus(String id, String status) {
        if (id == null || id.isEmpty() || status == null || status.isEmpty()) {
            Toast.makeText(this, "ID or Status is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_STATUS,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchSerials(); // Refresh the list
                            Toast.makeText(this, "Status updated: " + status, Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = obj.optString("message", "Update failed");
                            Toast.makeText(this, "Server: " + msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Update: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id.trim());
                params.put("status", status.trim());
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

            // Get current date without time
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            // Return true if serial date is today or in the future
            return !serialDate.before(today);

        } catch (Exception e) {
            return true; // If parsing fails, show it anyway
        }
    }

    @Override
    public void onUpdateStatus(DoctorSerial serial) {
        String[] menuItems = {"Pending", "Completed", "Cancelled"};
        String[] serverValues = {"Pending", "Completed", "Cancelled"};
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Status")
                .setItems(menuItems, (dialog, which) -> {
                    updateSerialStatus(serial.getId(), serverValues[which]);
                })
                .show();
    }
}
