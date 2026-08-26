package com.theeztech.communityservicedashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransportActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerVehicleType;
    private RecyclerView recyclerTransports;
    private TransportAdapter adapter;
    private List<Transport> transportList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;
    private MaterialToolbar toolbar;
    
    private RecyclerView recyclerStats;
    private DashboardStatAdapter statsAdapter;
    private List<DashboardStatAdapter.StatItem> statList;

    private static final String URL_GET_TRANSPORTS = "https://csd.theeztech.xyz/api/get_transports.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transport);

        initViews();
        setupRecyclerView();
        setupSpinner();

        // Initial fetch
        fetchTransports("");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerVehicleType = findViewById(R.id.spinnerVehicleType);
        recyclerTransports = findViewById(R.id.recyclerTransports);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        
        setupStatsRecyclerView();
    }

    private void setupStatsRecyclerView() {
        recyclerStats = findViewById(R.id.recyclerStats);
        statList = new ArrayList<>();
        statsAdapter = new DashboardStatAdapter(statList);
        recyclerStats.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerStats.setAdapter(statsAdapter);
    }

    private void setupRecyclerView() {
        transportList = new ArrayList<>();
        adapter = new TransportAdapter(this, transportList);
        recyclerTransports.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransports.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] vehicleTypes = {"All", "Ambulance", "Car", "Microbus", "Pickup", "Rickshaw", "Truck"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehicleTypes);
        spinnerVehicleType.setAdapter(spinnerAdapter);

        spinnerVehicleType.setOnItemClickListener((parent, view, position, id) -> {
            String selected = vehicleTypes[position];
            if (selected.equals("All")) {
                fetchTransports("");
            } else {
                fetchTransports(selected);
            }
        });
    }

    private void fetchTransports(String filter) {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        String url = URL_GET_TRANSPORTS;
        if (!filter.isEmpty()) {
            url += "?vehicle_type=" + filter;
        }

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    transportList.clear();
                    try {
                        JSONArray responseArray;
                        String trimmed = response.trim();
                        if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            responseArray = obj.optJSONArray("data");
                            if (responseArray == null) responseArray = obj.optJSONArray("transports");
                            if (responseArray == null && !obj.optBoolean("success", true)) {
                                throw new JSONException(obj.optString("message", "Error"));
                            }
                        } else {
                            responseArray = new JSONArray(trimmed);
                        }

                        if (responseArray != null) {
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject obj = responseArray.getJSONObject(i);
                                transportList.add(new Transport(
                                        obj.optString("id"),
                                        obj.optString("vehicle_type"),
                                        obj.optString("vehicle_number"),
                                        obj.optString("driver_name"),
                                        obj.optString("contact_number")
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (filter.isEmpty() && responseArray != null) {
                            calculateAndShowStats(responseArray);
                        }
                        
                        if (transportList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Transports: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (transportList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void calculateAndShowStats(org.json.JSONArray response) {
        try {
            int total = response.length();
            int ambulance = 0, car = 0, microbus = 0, pickup = 0, bike = 0, rickshaw = 0, truck = 0;

            for (int i = 0; i < response.length(); i++) {
                String type = response.getJSONObject(i).optString("vehicle_type").toLowerCase();
                if (type.contains("ambulance")) ambulance++;
                else if (type.contains("car")) car++;
                else if (type.contains("microbus")) microbus++;
                else if (type.contains("pickup")) pickup++;
                else if (type.contains("bike")) bike++;
                else if (type.contains("rickshaw")) rickshaw++;
                else if (type.contains("truck")) truck++;
            }

            statList.clear();
            statList.add(new DashboardStatAdapter.StatItem("Total", total));
            statList.add(new DashboardStatAdapter.StatItem("Ambulance", ambulance));
            statList.add(new DashboardStatAdapter.StatItem("Car", car));
            statList.add(new DashboardStatAdapter.StatItem("Microbus", microbus));
            statList.add(new DashboardStatAdapter.StatItem("Pickup", pickup));
            statList.add(new DashboardStatAdapter.StatItem("Bike", bike));
            statList.add(new DashboardStatAdapter.StatItem("Rickshaw", rickshaw));
            statList.add(new DashboardStatAdapter.StatItem("Truck", truck));
            statsAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
