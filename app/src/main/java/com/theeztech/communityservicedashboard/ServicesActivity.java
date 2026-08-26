package com.theeztech.communityservicedashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class ServicesActivity extends AppCompatActivity {

    MaterialCardView cardHealth, cardRegistration, cardDoctor, cardTransports, 
                     cardBlood, cardAgri, cardEmergency, cardUpazila, 
                     cardBusiness, cardEducation, cardJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_services);

        initCards();
        setupClickListeners();
        setupBottomNav();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initCards() {
        cardHealth = findViewById(R.id.cardHealth);
        cardRegistration = findViewById(R.id.cardRegistration);
        cardDoctor = findViewById(R.id.cardDoctor);
        cardTransports = findViewById(R.id.cardTransports);
        cardBlood = findViewById(R.id.cardBlood);
        cardAgri = findViewById(R.id.cardAgri);
        cardEmergency = findViewById(R.id.cardEmergency);
        cardUpazila = findViewById(R.id.cardUpazila);
        cardBusiness = findViewById(R.id.cardBusiness);
        cardEducation = findViewById(R.id.cardEducation);
        cardJobs = findViewById(R.id.cardJobs);
    }

    private void setupClickListeners() {
        cardHealth.setOnClickListener(v -> startActivity(new Intent(this, HealthcareActivity.class)));
        
        cardRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bdris.gov.bd/"));
            startActivity(intent);
        });

        cardDoctor.setOnClickListener(v -> {
            startActivity(new Intent(this, AllDoctorsActivity.class));
        });

        cardTransports.setOnClickListener(v -> startActivity(new Intent(this, TransportActivity.class)));
        
        cardBlood.setOnClickListener(v -> startActivity(new Intent(this, BloodActivity.class)));
        
        cardAgri.setOnClickListener(v -> startActivity(new Intent(this, AgricultureActivity.class)));
        
        cardEmergency.setOnClickListener(v -> startActivity(new Intent(this, EmergencyActivity.class)));

        cardUpazila.setOnClickListener(v -> {
            startActivity(new Intent(this, UpazilaActivity.class));
        });

        cardBusiness.setOnClickListener(v -> startActivity(new Intent(this, BusinessActivity.class)));

        cardEducation.setOnClickListener(v -> startActivity(new Intent(this, EducationActivity.class)));
        cardJobs.setOnClickListener(v -> startActivity(new Intent(this, JobsActivity.class)));
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_services);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_services) {
                return true;

            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
