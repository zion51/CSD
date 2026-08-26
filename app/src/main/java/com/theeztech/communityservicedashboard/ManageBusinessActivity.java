package com.theeztech.communityservicedashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManageBusinessActivity extends AppCompatActivity implements JobAdapter.OnJobActionListener {

    private MaterialToolbar toolbar;
    private MaterialCardView cardPostJob;
    private RecyclerView recyclerMyJobs;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;

    private JobAdapter adapter;
    private List<Job> jobList;
    private String userPhone, businessId;

    private static final String URL_POST_JOB = "https://csd.theeztech.xyz/api/add_job.php";
    private static final String URL_GET_MY_JOBS = "https://csd.theeztech.xyz/api/get_my_jobs.php";
    private static final String URL_DELETE_JOB = "https://csd.theeztech.xyz/api/delete_job.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_business);

        userPhone = getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(MainActivity.KEY_PHONE, "");
        businessId = getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString("business_id", "");

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        fetchMyJobs();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cardPostJob = findViewById(R.id.cardPostJob);
        recyclerMyJobs = findViewById(R.id.recyclerMyJobs);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        jobList = new ArrayList<>();
        adapter = new JobAdapter(jobList, this);
        recyclerMyJobs.setLayoutManager(new LinearLayoutManager(this));
        recyclerMyJobs.setAdapter(adapter);
    }

    private void setupListeners() {
        cardPostJob.setOnClickListener(v -> showPostJobDialog());
    }

    private void fetchMyJobs() {
        if (userPhone.isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        String url = URL_GET_MY_JOBS + "?user_phone=" + userPhone;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            JSONArray array = json.getJSONArray("jobs");
                            jobList.clear();
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
                                        obj.optString("user_phone", userPhone)
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        
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
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (jobList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showPostJobDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_post_job, null);
        
        TextInputEditText etTitle = dialogView.findViewById(R.id.etJobTitle);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerJobCategory);
        AutoCompleteTextView spinnerType = dialogView.findViewById(R.id.spinnerJobType);
        TextInputEditText etVacancies = dialogView.findViewById(R.id.etJobVacancies);
        TextInputEditText etSalary = dialogView.findViewById(R.id.etJobSalary);
        AutoCompleteTextView spinnerWorkplace = dialogView.findViewById(R.id.spinnerWorkplace);
        TextInputEditText etEducation = dialogView.findViewById(R.id.etJobEducation);
        TextInputEditText etExperience = dialogView.findViewById(R.id.etJobExperience);
        TextInputEditText etDeadline = dialogView.findViewById(R.id.etJobDeadline);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etJobDescription);

        // Setup Dropdowns
        String[] categories = {"Sales & Marketing", "IT & Telecommunication", "Creative & Design", "Customer Service", "Accounting/Finance", "HR & Admin", "Production & Operation", "Garments/Textile", "Education/Training", "Hospitality/Travel", "Medical/Healthcare", "NGO/Development", "Other"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories));

        String[] jobTypes = {"Full-time", "Part-time", "Contract", "Internship", "Freelance"};
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jobTypes));

        String[] workplaceTypes = {"Office", "Remote", "Hybrid"};
        spinnerWorkplace.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, workplaceTypes));

        // Date Picker for Deadline
        Calendar calendar = Calendar.getInstance();
        etDeadline.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                etDeadline.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Post a New Job")
                .setView(dialogView)
                .setPositiveButton("Post", (dialog, which) -> {
                    String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
                    String category = spinnerCategory.getText().toString().trim();
                    String type = spinnerType.getText().toString().trim();
                    String vacancies = etVacancies.getText() != null ? etVacancies.getText().toString().trim() : "";
                    String salary = etSalary.getText() != null ? etSalary.getText().toString().trim() : "";
                    String workplace = spinnerWorkplace.getText().toString().trim();
                    String education = etEducation.getText() != null ? etEducation.getText().toString().trim() : "";
                    String experience = etExperience.getText() != null ? etExperience.getText().toString().trim() : "";
                    String deadline = etDeadline.getText() != null ? etDeadline.getText().toString().trim() : "";
                    String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

                    if (!title.isEmpty() && !category.isEmpty() && !desc.isEmpty()) {
                        submitJob(title, category, type, vacancies, salary, workplace, education, experience, deadline, desc);
                    } else {
                        Toast.makeText(this, "Title, Category and Description are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitJob(String title, String category, String type, String vacancies, String salary, 
                           String workplace, String education, String experience, String deadline, String desc) {
        
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting job...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_JOB,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optBoolean("success")) {
                            Toast.makeText(this, "Job posted successfully", Toast.LENGTH_LONG).show();
                            fetchMyJobs(); // Refresh list
                        } else {
                            Toast.makeText(this, jsonObject.optString("message", "Post failed"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_phone", userPhone);
                params.put("job_title", title);
                params.put("job_category", category);
                params.put("job_type", type);
                params.put("vacancies", vacancies);
                params.put("salary", salary);
                params.put("workplace", workplace);
                params.put("education", education);
                params.put("experience", experience);
                params.put("deadline", deadline);
                params.put("description", desc);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onDelete(Job job) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job post?")
                .setPositiveButton("Delete", (dialog, which) -> deleteJob(job.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteJob(String jobId) {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_JOB,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optBoolean("success")) {
                            Toast.makeText(this, "Job deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchMyJobs(); // Refresh list
                        } else {
                            Toast.makeText(this, jsonObject.optString("message", "Delete failed"), Toast.LENGTH_SHORT).show();
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
                params.put("job_id", jobId);
                params.put("user_phone", userPhone);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
