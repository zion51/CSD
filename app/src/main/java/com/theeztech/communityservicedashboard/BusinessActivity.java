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

public class BusinessActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerCategory;
    private RecyclerView recyclerBusinesses;
    private BusinessAdapter adapter;
    private List<Business> businessList;
    private LinearProgressIndicator progressBar;
    private LinearLayout emptyState;
    private MaterialToolbar toolbar;

    private static final String URL_GET_BUSINESSES = "https://csd.theeztech.xyz/api/get_businesses.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business);

        initViews();
        setupRecyclerView();
        setupSpinner();

        fetchBusinesses("");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerCategory = findViewById(R.id.spinnerCategory);
        recyclerBusinesses = findViewById(R.id.recyclerBusinesses);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        businessList = new ArrayList<>();
        adapter = new BusinessAdapter(this, businessList);
        recyclerBusinesses.setLayoutManager(new LinearLayoutManager(this));
        recyclerBusinesses.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] categories = {"All", "Shop", "Restaurant", "Agriculture", "Electronics", "Hotel", 
                               "Hospital/Clinic", "Pharmacy", "Beauty Salon", "Hardware", 
                               "Clothing", "Mobile & Computer", "Education", "Other"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(catAdapter);

        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selected = categories[position];
            if (selected.equals("All")) {
                fetchBusinesses("");
            } else {
                fetchBusinesses(selected);
            }
        });
    }

    private void fetchBusinesses(String categoryFilter) {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        String url = URL_GET_BUSINESSES;
        if (!categoryFilter.isEmpty()) {
            url += "?category=" + categoryFilter;
        }

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    businessList.clear();
                    try {
                        JSONArray array;
                        String trimmed = response.trim();
                        if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            array = obj.optJSONArray("data");
                            if (array == null) array = obj.optJSONArray("businesses");
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
                                businessList.add(new Business(
                                        obj.optString("id"),
                                        obj.optString("business_name"),
                                        obj.optString("owner_name"),
                                        obj.optString("mobile"),
                                        obj.optString("address"),
                                        obj.optString("category"),
                                        obj.optString("description")
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();

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
                    Toast.makeText(this, "Businesses: " + NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                    if (businessList.isEmpty()) emptyState.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
