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

public class JobsActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerCategory;
    private RecyclerView recyclerJobs;
    private UserJobAdapter adapter;
    private List<Job> jobList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private static final String URL_GET_ALL_JOBS = "https://csd.theeztech.xyz/api/get_all_jobs.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jobs);

        initViews();
        setupRecyclerView();
        setupSpinner();

        fetchJobs("");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerCategory = findViewById(R.id.spinnerJobCategory);
        recyclerJobs = findViewById(R.id.recyclerJobs);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        jobList = new ArrayList<>();
        adapter = new UserJobAdapter(this, jobList);
        recyclerJobs.setLayoutManager(new LinearLayoutManager(this));
        recyclerJobs.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] categories = {"All", "Sales & Marketing", "IT & Telecommunication", "Creative & Design", "Customer Service", "Accounting/Finance", "HR & Admin", "Production & Operation", "Garments/Textile", "Education/Training", "Hospitality/Travel", "Medical/Healthcare", "NGO/Development", "Other"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories));

        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selected = categories[position];
            if (selected.equals("All")) {
                fetchJobs("");
            } else {
                fetchJobs(selected);
            }
        });
    }

    private void fetchJobs(String categoryFilter) {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        String url = URL_GET_ALL_JOBS;
        if (!categoryFilter.isEmpty()) {
            url += "?category=" + categoryFilter.replace("&", "%26");
        }

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    jobList.clear();
                    try {
                        JSONArray array;
                        String trimmed = response.trim();
                        if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            array = obj.optJSONArray("data");
                            if (array == null) array = obj.optJSONArray("jobs");
                            if (array == null && !obj.optBoolean("success", true)) {
                                emptyState.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else {
                            array = new JSONArray(trimmed);
                        }

                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                jobList.add(new Job(
                                        obj.optString("id"),
                                        obj.optString("job_title"),
                                        obj.optString("job_category"),
                                        obj.optString("job_type"),
                                        obj.optString("vacancies"),
                                        obj.optString("salary"),
                                        obj.optString("workplace"),
                                        obj.optString("education"),
                                        obj.optString("experience"),
                                        obj.optString("deadline"),
                                        obj.optString("description"),
                                        obj.optString("owner_phone", obj.optString("user_phone", ""))
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (jobList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (jobList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Jobs: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (jobList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
