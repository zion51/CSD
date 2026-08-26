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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BloodActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerBlood;
    private RecyclerView recyclerDonors;
    private DonorAdapter adapter;
    private List<Donor> donorList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;
    private MaterialToolbar toolbar;

    private RecyclerView recyclerStats;
    private DashboardStatAdapter statsAdapter;
    private List<DashboardStatAdapter.StatItem> statList;

    private static final String URL_GET_DONORS = "https://csd.theeztech.xyz/api/get_donors.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blood);

        initViews();
        setupRecyclerViews();
        setupSpinner();

        // Initial fetch for all donors and dashboard stats
        fetchDonors("");

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

        spinnerBlood = findViewById(R.id.spinnerBlood);
        recyclerDonors = findViewById(R.id.recyclerDonors);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        recyclerStats = findViewById(R.id.recyclerStats);
    }

    private void setupRecyclerViews() {
        // Main list
        donorList = new ArrayList<>();
        adapter = new DonorAdapter(this, donorList);
        recyclerDonors.setLayoutManager(new LinearLayoutManager(this));
        recyclerDonors.setAdapter(adapter);

        // Stats Grid
        statList = new ArrayList<>();
        statsAdapter = new DashboardStatAdapter(statList);
        recyclerStats.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerStats.setAdapter(statsAdapter);
    }

    private void setupSpinner() {
        String[] bloodGroups = {"All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
        spinnerBlood.setAdapter(bloodAdapter);

        spinnerBlood.setOnItemClickListener((parent, view, position, id) -> {
            String selected = bloodGroups[position];
            if (selected.equals("All")) {
                fetchDonors("");
            } else {
                fetchDonors(selected);
            }
        });
    }

    private void fetchDonors(String bloodFilter) {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        String url = URL_GET_DONORS;
        if (!bloodFilter.isEmpty()) {
            String encodedBlood = bloodFilter.replace("+", "%2B");
            url += "?blood_group=" + encodedBlood;
        }

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    donorList.clear();
                    try {
                        JSONArray responseArray;
                        String trimmed = response.trim();
                        if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            responseArray = obj.optJSONArray("data");
                            if (responseArray == null) responseArray = obj.optJSONArray("donors");
                            if (responseArray == null) {
                                if (!(obj.optBoolean("success", false) || obj.optString("status").equals("success"))) {
                                    throw new JSONException(obj.optString("message", "Unknown server error"));
                                }
                            }
                        } else {
                            responseArray = new JSONArray(trimmed);
                        }

                        if (responseArray != null) {
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject obj = responseArray.getJSONObject(i);
                                String lastDate = obj.optString("last_donation_date", "Never");

                                if (isEligible(lastDate)) {
                                    donorList.add(new Donor(
                                            obj.getString("name"),
                                            obj.getString("phone"),
                                            obj.getString("blood_group"),
                                            lastDate
                                    ));
                                }
                            }
                        }

                        // Sort donors: oldest donation first
                        sortDonors();

                        adapter.notifyDataSetChanged();

                        // Only calculate dashboard stats when viewing "All" donors
                        if (bloodFilter.isEmpty() && responseArray != null) {
                            calculateAndShowStats(responseArray);
                        }

                        if (donorList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Donors: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (donorList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void sortDonors() {
        Collections.sort(donorList, (d1, d2) -> {
            String date1 = d1.getLastDonationDate();
            String date2 = d2.getLastDonationDate();

            boolean never1 = isNever(date1);
            boolean never2 = isNever(date2);

            if (never1 && never2) return 0;
            if (never1) return -1;
            if (never2) return 1;

            return date1.compareTo(date2);
        });
    }

    private void calculateAndShowStats(org.json.JSONArray response) {
        try {
            int total = response.length();
            int aPos = 0, aNeg = 0, bPos = 0, bNeg = 0, abPos = 0, abNeg = 0, oPos = 0, oNeg = 0;

            for (int i = 0; i < response.length(); i++) {
                String bg = response.getJSONObject(i).optString("blood_group");
                if (bg.equalsIgnoreCase("A+")) aPos++;
                else if (bg.equalsIgnoreCase("A-")) aNeg++;
                else if (bg.equalsIgnoreCase("B+")) bPos++;
                else if (bg.equalsIgnoreCase("B-")) bNeg++;
                else if (bg.equalsIgnoreCase("AB+")) abPos++;
                else if (bg.equalsIgnoreCase("AB-")) abNeg++;
                else if (bg.equalsIgnoreCase("O+")) oPos++;
                else if (bg.equalsIgnoreCase("O-")) oNeg++;
            }

            statList.clear();
            statList.add(new DashboardStatAdapter.StatItem("Total", total));
            statList.add(new DashboardStatAdapter.StatItem("A+", aPos));
            statList.add(new DashboardStatAdapter.StatItem("B+", bPos));
            statList.add(new DashboardStatAdapter.StatItem("O+", oPos));
            statList.add(new DashboardStatAdapter.StatItem("AB+", abPos));
            statList.add(new DashboardStatAdapter.StatItem("A-", aNeg));
            statList.add(new DashboardStatAdapter.StatItem("B-", bNeg));
            statList.add(new DashboardStatAdapter.StatItem("O-", oNeg));
            statList.add(new DashboardStatAdapter.StatItem("AB-", abNeg));
            statsAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isEligible(String lastDonationDate) {
        return isNever(lastDonationDate) || isOldEnough(lastDonationDate);
    }

    private boolean isNever(String lastDonationDate) {
        return lastDonationDate == null || lastDonationDate.isEmpty() ||
                lastDonationDate.equalsIgnoreCase("Never") ||
                lastDonationDate.equalsIgnoreCase("null") ||
                lastDonationDate.equals("0000-00-00");
    }

    private boolean isOldEnough(String lastDonationDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date lastDate = sdf.parse(lastDonationDate);
            if (lastDate == null) return true;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -3);
            Date threeMonthsAgo = cal.getTime();

            return lastDate.before(threeMonthsAgo) || lastDate.equals(threeMonthsAgo);

        } catch (ParseException e) {
            return true;
        }
    }
}
