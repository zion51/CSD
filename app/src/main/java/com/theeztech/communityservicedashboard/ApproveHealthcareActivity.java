package com.theeztech.communityservicedashboard;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveHealthcareActivity extends AppCompatActivity implements ApproveHealthcareAdapter.OnHealthcareApprovalListener {

    private RecyclerView recyclerPending;
    private ApproveHealthcareAdapter adapter;
    private List<HealthcareCenter> healthcareList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private static final String URL_GET_PENDING = "https://csd.theeztech.xyz/api/get_pending_healthcare.php";
    private static final String URL_UPDATE_STATUS = "https://csd.theeztech.xyz/api/update_healthcare_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_healthcare);

        initViews();
        setupToolbar();
        setupRecyclerView();

        fetchPendingHealthcare();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerPending = findViewById(R.id.recyclerPendingHealthcare);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        healthcareList = new ArrayList<>();
        adapter = new ApproveHealthcareAdapter(healthcareList, this);
        recyclerPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerPending.setAdapter(adapter);
    }

    private void fetchPendingHealthcare() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_PENDING,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    healthcareList.clear();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            JSONArray array = json.optJSONArray("healthcare");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    healthcareList.add(new HealthcareCenter(
                                            obj.getString("id"),
                                            obj.getString("healthcare_name"),
                                            obj.getString("healthcare_type"),
                                            obj.getString("contact_number"),
                                            obj.getString("email"),
                                            obj.getString("address"),
                                            obj.getString("description")
                                    ));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        
                        if (healthcareList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (healthcareList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (healthcareList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onApprove(HealthcareCenter center) {
        showConfirmationDialog(center, "Approved");
    }

    @Override
    public void onReject(HealthcareCenter center) {
        showConfirmationDialog(center, "Rejected");
    }

    private void showConfirmationDialog(HealthcareCenter center, String status) {
        String displayStatus = status.equals("Rejected") ? "Cancel" : "Approve";
        new MaterialAlertDialogBuilder(this)
                .setTitle(displayStatus + " Healthcare")
                .setMessage("Are you sure you want to " + displayStatus.toLowerCase() + " " + center.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> updateStatus(center.getId(), status))
                .setNegativeButton("No", null)
                .show();
    }

    private void updateStatus(String id, String status) {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_STATUS,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            Toast.makeText(this, "Status updated to " + status, Toast.LENGTH_SHORT).show();
                            fetchPendingHealthcare(); // Refresh list
                        } else {
                            Toast.makeText(this, json.optString("message", "Update failed"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
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
                params.put("status", status.toLowerCase()); // Convert to lowercase for PHP API
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
