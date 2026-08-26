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

public class ApproveTutorActivity extends AppCompatActivity implements ApproveTutorAdapter.OnTutorApprovalListener {

    private RecyclerView recyclerPending;
    private ApproveTutorAdapter adapter;
    private List<Tutor> tutorList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private static final String URL_GET_PENDING = "https://csd.theeztech.xyz/api/get_pending_tutors.php";
    private static final String URL_UPDATE_STATUS = "https://csd.theeztech.xyz/api/update_tutor_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_tutor);

        initViews();
        setupToolbar();
        setupRecyclerView();

        fetchPendingTutors();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerPending = findViewById(R.id.recyclerPendingTutor);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        tutorList = new ArrayList<>();
        adapter = new ApproveTutorAdapter(tutorList, this);
        recyclerPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerPending.setAdapter(adapter);
    }

    private void fetchPendingTutors() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_PENDING,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    tutorList.clear();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            JSONArray array = json.getJSONArray("tutors");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                tutorList.add(new Tutor(
                                        obj.getString("id"),
                                        obj.getString("name"),
                                        obj.getString("user_phone"),
                                        obj.getString("education"),
                                        obj.getString("subjects"),
                                        obj.getString("expected_salary"),
                                        obj.getString("address"),
                                        obj.getString("experience"),
                                        obj.getString("status")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        
                        if (tutorList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (tutorList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (tutorList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onApprove(Tutor tutor) {
        showConfirmationDialog(tutor, "Approved");
    }

    @Override
    public void onReject(Tutor tutor) {
        showConfirmationDialog(tutor, "Rejected");
    }

    private void showConfirmationDialog(Tutor tutor, String status) {
        String displayStatus = status.equals("Rejected") ? "Cancel" : "Approve";
        new MaterialAlertDialogBuilder(this)
                .setTitle(displayStatus + " Tutor")
                .setMessage("Are you sure you want to " + displayStatus.toLowerCase() + " " + tutor.getName() + "'s application?")
                .setPositiveButton("Yes", (dialog, which) -> updateStatus(tutor.getId(), status))
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
                            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
                            fetchPendingTutors(); 
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
                params.put("status", status.toLowerCase());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
