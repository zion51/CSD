package com.theeztech.communityservicedashboard;

import android.content.Intent;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HealthcareActivity extends AppCompatActivity implements HealthcareAdapter.OnCenterClickListener {

    private RecyclerView recyclerHealthcare;
    private HealthcareAdapter adapter;
    private List<HealthcareCenter> healthcareList;
    private MaterialToolbar toolbar;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private static final String URL_GET_HEALTHCARE = "https://csd.theeztech.xyz/api/get_healthcare_centers.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_healthcare);

        initViews();
        setupToolbar();
        setupRecyclerView();

        fetchHealthcareCenters();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerHealthcare = findViewById(R.id.recyclerHealthcare);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        healthcareList = new ArrayList<>();
        adapter = new HealthcareAdapter(this, healthcareList, this);
        recyclerHealthcare.setLayoutManager(new LinearLayoutManager(this));
        recyclerHealthcare.setAdapter(adapter);
    }

    @Override
    public void onCenterClick(HealthcareCenter center) {
        Intent intent = new Intent(this, HealthcareDetailsActivity.class);
        intent.putExtra("healthcare_id", center.getId());
        intent.putExtra("healthcare_name", center.getName());
        startActivity(intent);
    }

    private void fetchHealthcareCenters() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_HEALTHCARE,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray array;
                        String trimmedResponse = response.trim();
                        
                        if (trimmedResponse.startsWith("{")) {
                            JSONObject json = new JSONObject(trimmedResponse);
                            // Try common keys: "data", "healthcare_centers", "centers"
                            array = json.optJSONArray("data");
                            if (array == null) array = json.optJSONArray("healthcare_centers");
                            if (array == null) array = json.optJSONArray("centers");
                            
                            // If still null, check if success is true but array is missing
                            if (array == null && !json.optBoolean("success", false)) {
                                emptyState.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (trimmedResponse.startsWith("[")) {
                            array = new JSONArray(trimmedResponse);
                        } else {
                            throw new Exception("Invalid JSON format");
                        }

                        if (array != null) {
                            healthcareList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                healthcareList.add(new HealthcareCenter(
                                        obj.optString("id"),
                                        obj.optString("healthcare_name", obj.optString("name")),
                                        obj.optString("healthcare_type", obj.optString("type")),
                                        obj.optString("contact_number", obj.optString("phone")),
                                        obj.optString("email"),
                                        obj.optString("address"),
                                        obj.optString("description")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        
                        if (healthcareList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (healthcareList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Healthcare: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (healthcareList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
