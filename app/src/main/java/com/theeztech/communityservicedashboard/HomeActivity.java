package com.theeztech.communityservicedashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome, seemore, tvDonorCount, tvDoctorCount, tvTransportCount;
    SharedPreferences sharedPreferences;
    RelativeLayout loadingLayout;

    private static final String URL_GET_USER = "https://csd.theeztech.xyz/api/get_user.php";
    private static final String URL_GET_STATS = "https://csd.theeztech.xyz/api/get_stats.php";
    private static final String KEY_CACHED_NAME = "cached_name";
    ViewPager2 imageSlider;
    Handler sliderHandler = new Handler(Looper.getMainLooper());
    Runnable sliderRunnable;

    MaterialCardView cardBlood, cardEmergency, cardTransports, cardHealth, cardRegistration, cardJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        loadingLayout = findViewById(R.id.loadingLayout);
        tvDonorCount = findViewById(R.id.tvDonorCount);
        tvDoctorCount = findViewById(R.id.tvDoctorCount);
        tvTransportCount = findViewById(R.id.tvTransportCount);

        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Retrieve the user's name from SharedPreferences
        String phone = sharedPreferences.getString(MainActivity.KEY_PHONE, null);
        String cachedName = sharedPreferences.getString(MainActivity.KEY_NAME, null);
        if (cachedName == null) cachedName = sharedPreferences.getString(KEY_CACHED_NAME, "User");

        tvWelcome.setText(getTimeBasedGreeting() + ", " + cachedName);

        if(phone != null){
            fetchUserName(phone);
        }
        fetchDashboardStats();

        imageSlider = findViewById(R.id.imageSlider);

        // Online image URLs
        List<String> imageList = Arrays.asList(
                "https://amarkaharole.theeztech.xyz/images/kaharole01.png",
                "https://amarkaharole.theeztech.xyz/images/kaharole02.png",
                "https://amarkaharole.theeztech.xyz/images/kaharole03.png"
        );

        SliderAdapter adapter = new SliderAdapter(imageList);
        imageSlider.setAdapter(adapter);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = imageSlider.getCurrentItem();
                int totalItems = adapter.getItemCount();

                if (totalItems > 0) {
                    if (currentItem == totalItems - 1) {
                        imageSlider.setCurrentItem(0);
                    } else {
                        imageSlider.setCurrentItem(currentItem + 1);
                    }
                }

                sliderHandler.postDelayed(this, 3000); // 3 seconds
            }
        };

        sliderHandler.postDelayed(sliderRunnable, 3000);

        seemore= findViewById(R.id.seemore);
        seemore.setOnClickListener(v -> {
                    startActivity(new Intent(this, ServicesActivity.class));
                });

        cardBlood = findViewById(R.id.cardBlood);
        cardBlood.setOnClickListener(v -> {
            startActivity(new Intent(this, BloodActivity.class));
        });

        cardTransports = findViewById(R.id.cardTransports);
        cardTransports.setOnClickListener(v -> {
            startActivity(new Intent(this, TransportActivity.class));
        });

        cardEmergency = findViewById(R.id.cardEmergency);
        cardEmergency.setOnClickListener(v -> {
            startActivity(new Intent(this, EmergencyActivity.class));
        });

        cardHealth = findViewById(R.id.cardHealth);
        cardHealth.setOnClickListener(v -> {
            startActivity(new Intent(this, HealthcareActivity.class));
        });

        cardRegistration = findViewById(R.id.cardRegistration);
        cardRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bdris.gov.bd/"));
            startActivity(intent);
        });

        cardJobs = findViewById(R.id.cardJobs);
        cardJobs.setOnClickListener(v -> {
            startActivity(new Intent(this, JobsActivity.class));
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_services) {
                startActivity(new Intent(this, ServicesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String getTimeBasedGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }

    private void fetchUserName(String phone){
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_USER,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if(obj.getString("status").equals("success")){

                            String name = obj.getString("name");

                            // Save in cache
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(KEY_CACHED_NAME, name);
                            editor.apply();

                            tvWelcome.setText(getTimeBasedGreeting() + ", " + name);

                        } else {
                            loadCachedName();
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                        loadCachedName();
                    }
                },
                error -> {
                    loadCachedName();
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchDashboardStats() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_STATS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        // Changed status check to "success" boolean/string handling based on PHP output
                        if (obj.optBoolean("success") || obj.optString("success").equals("true")) {
                            tvDonorCount.setText(obj.optString("donors", "0") + "+");
                            tvDoctorCount.setText(obj.optString("doctors", "0") + "+");
                            tvTransportCount.setText(obj.optString("transports", "0") + "+");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Fallback to static values if API fails
                    tvDonorCount.setText("500+");
                    tvDoctorCount.setText("50+");
                    tvTransportCount.setText("20+");
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void loadCachedName(){

        String cachedName = sharedPreferences.getString(KEY_CACHED_NAME, "User");
        tvWelcome.setText(getTimeBasedGreeting() + ", " + cachedName);

        Toast.makeText(this, "Offline Mode", Toast.LENGTH_SHORT).show();
    }

}