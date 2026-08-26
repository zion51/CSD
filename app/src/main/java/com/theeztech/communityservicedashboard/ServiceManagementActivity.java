package com.theeztech.communityservicedashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class ServiceManagementActivity extends AppCompatActivity implements ServiceAdapter.OnServiceActionListener {

    private RecyclerView recyclerServices;
    private ServiceAdapter adapter;
    private List<HealthcareService> serviceList;
    private MaterialToolbar toolbar;
    private FloatingActionButton fabAdd;
    private LinearProgressIndicator progressBar;

    private static final String BASE_URL = "https://csd.theeztech.xyz/api/";
    private static final String URL_GET = "https://csd.theeztech.xyz/api/get_services.php";
    private static final String URL_ADD = "https://csd.theeztech.xyz/api/add_service.php";
    private static final String URL_EDIT = BASE_URL + "edit_service.php";
    private static final String URL_DELETE = BASE_URL + "delete_service.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_management);

        initViews();
        setupRecyclerView();
        setupToolbar();

        fetchServices();

        fabAdd.setOnClickListener(v -> showServiceDialog(null));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerServices = findViewById(R.id.recyclerServices);
        fabAdd = findViewById(R.id.fabAddService);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, this);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this));
        recyclerServices.setAdapter(adapter);
    }

    private void fetchServices() {
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
                            JSONArray array = json.getJSONArray("services");
                            serviceList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                serviceList.add(
                                        new HealthcareService(
                                                obj.getString("id"),
                                                obj.getString("service_name"),
                                                obj.getString("description"),
                                                obj.getString("price")
                                        )
                                );
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Fetch: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
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

    private void showServiceDialog(HealthcareService service) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etServiceName);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etServiceDesc);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etServicePrice);

        if (service != null) {
            etName.setText(service.getName());
            etDesc.setText(service.getDescription());
            etPrice.setText(service.getPrice());
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(service == null ? "Add Service" : "Edit Service")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
                    String price = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";

                    if (!name.isEmpty() && !price.isEmpty()) {
                        if (service == null) addService(name, desc, price);
                        else editService(service.getId(), name, desc, price);
                    } else {
                        Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addService(String name, String desc, String price) {
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
                            fetchServices();
                            Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show();
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
                params.put("service_name", name);
                params.put("description", desc);
                params.put("price", price);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void editService(String id, String name, String desc, String price) {
        progressBar.setVisibility(View.VISIBLE);
        String ownerPhone = getSharedPreferences(MainActivity.SHARED_PREF_NAME, MODE_PRIVATE).getString(MainActivity.KEY_PHONE, "");
        StringRequest request = new StringRequest(Request.Method.POST, URL_EDIT,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchServices();
                            Toast.makeText(this, "Service updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Failed to update service");
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
                params.put("phone", ownerPhone);
                params.put("service_name", name);
                params.put("description", desc);
                params.put("price", price);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void deleteService(String id) {
        progressBar.setVisibility(View.VISIBLE);
        String ownerPhone = getSharedPreferences(MainActivity.SHARED_PREF_NAME, MODE_PRIVATE).getString(MainActivity.KEY_PHONE, "");
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchServices();
                            Toast.makeText(this, "Service deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Failed to delete service");
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
                params.put("phone", ownerPhone);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onEdit(HealthcareService service) {
        showServiceDialog(service);
    }

    @Override
    public void onDelete(HealthcareService service) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service?")
                .setPositiveButton("Delete", (dialog, which) -> deleteService(service.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
