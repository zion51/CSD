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

public class DiagnosticManagementActivity extends AppCompatActivity implements DiagnosticAdapter.OnDiagnosticActionListener {

    private RecyclerView recyclerDiagnostics;
    private DiagnosticAdapter adapter;
    private List<DiagnosticTest> diagnosticList;
    private MaterialToolbar toolbar;
    private FloatingActionButton fabAdd;
    private LinearProgressIndicator progressBar;

    private static final String BASE_URL = "https://csd.theeztech.xyz/api/";
    private static final String URL_GET = BASE_URL + "get_diagnostics.php";
    private static final String URL_ADD = BASE_URL + "add_diagnostic.php";
    private static final String URL_EDIT = BASE_URL + "edit_diagnostic.php";
    private static final String URL_DELETE = BASE_URL + "delete_diagnostic.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diagnostic_management);

        initViews();
        setupRecyclerView();
        setupToolbar();

        fetchDiagnostics();

        fabAdd.setOnClickListener(v -> showDiagnosticDialog(null));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerDiagnostics = findViewById(R.id.recyclerDiagnostics);
        fabAdd = findViewById(R.id.fabAddDiagnostic);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        diagnosticList = new ArrayList<>();
        adapter = new DiagnosticAdapter(diagnosticList, this);
        recyclerDiagnostics.setLayoutManager(new LinearLayoutManager(this));
        recyclerDiagnostics.setAdapter(adapter);
    }

    private void fetchDiagnostics() {
        progressBar.setVisibility(View.VISIBLE);
        String healthcareId = getSharedPreferences(MainActivity.SHARED_PREF_NAME, MODE_PRIVATE).getString("healthcare_id", "");

        if (healthcareId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("diagnostics");
                            diagnosticList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                diagnosticList.add(new DiagnosticTest(
                                        obj.getString("id"),
                                        obj.getString("test_name"),
                                        obj.getString("description"),
                                        obj.getString("price")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse tests", Toast.LENGTH_SHORT).show();
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

    private void showDiagnosticDialog(DiagnosticTest test) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etServiceName);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etServiceDesc);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etServicePrice);

        if (test != null) {
            etName.setText(test.getName());
            etDesc.setText(test.getDescription());
            etPrice.setText(test.getPrice());
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(test == null ? "Add Test" : "Edit Test")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
                    String price = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";

                    if (!name.isEmpty() && !price.isEmpty()) {
                        if (test == null) addDiagnostic(name, desc, price);
                        else editDiagnostic(test.getId(), name, desc, price);
                    } else {
                        Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addDiagnostic(String name, String desc, String price) {
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
                            fetchDiagnostics();
                            Toast.makeText(this, "Test added successfully", Toast.LENGTH_SHORT).show();
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
                params.put("test_name", name);
                params.put("description", desc);
                params.put("price", price);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void editDiagnostic(String id, String name, String desc, String price) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_EDIT,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchDiagnostics();
                            Toast.makeText(this, "Test updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Failed to update test");
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
                params.put("test_name", name);
                params.put("description", desc);
                params.put("price", price);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void deleteDiagnostic(String id) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            fetchDiagnostics();
                            Toast.makeText(this, "Test deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = obj.optString("message", "Failed to delete test");
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
    public void onEdit(DiagnosticTest test) {
        showDiagnosticDialog(test);
    }

    @Override
    public void onDelete(DiagnosticTest test) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Test")
                .setMessage("Are you sure you want to delete this diagnostic test?")
                .setPositiveButton("Delete", (dialog, which) -> deleteDiagnostic(test.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
