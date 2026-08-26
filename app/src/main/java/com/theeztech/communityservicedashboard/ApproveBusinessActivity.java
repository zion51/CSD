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

public class ApproveBusinessActivity extends AppCompatActivity implements ApproveBusinessAdapter.OnBusinessApprovalListener {

    private RecyclerView recyclerPending;
    private ApproveBusinessAdapter adapter;
    private List<Business> businessList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private static final String URL_GET_PENDING = "https://csd.theeztech.xyz/api/get_pending_businesses.php";
    private static final String URL_UPDATE_STATUS = "https://csd.theeztech.xyz/api/update_business_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_business);

        initViews();
        setupToolbar();
        setupRecyclerView();

        fetchPendingBusinesses();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerPending = findViewById(R.id.recyclerPendingBusiness);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        businessList = new ArrayList<>();
        adapter = new ApproveBusinessAdapter(businessList, this);
        recyclerPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerPending.setAdapter(adapter);
    }

    private void fetchPendingBusinesses() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_PENDING,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    businessList.clear();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            JSONArray array = json.getJSONArray("businesses");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                businessList.add(new Business(
                                        obj.getString("id"),
                                        obj.getString("business_name"),
                                        obj.getString("owner_name"),
                                        obj.getString("mobile"),
                                        obj.getString("address"),
                                        obj.getString("category"),
                                        obj.getString("description")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        
                        if (businessList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (businessList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (businessList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onApprove(Business business) {
        showConfirmationDialog(business, "Approved");
    }

    @Override
    public void onReject(Business business) {
        showConfirmationDialog(business, "Rejected");
    }

    private void showConfirmationDialog(Business business, String status) {
        String displayStatus = status.equals("Rejected") ? "Cancel" : "Approve";
        new MaterialAlertDialogBuilder(this)
                .setTitle(displayStatus + " Business")
                .setMessage("Are you sure you want to " + displayStatus.toLowerCase() + " " + business.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> updateStatus(business.getId(), status))
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
                            fetchPendingBusinesses(); // Refresh list
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
                params.put("status", status);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
