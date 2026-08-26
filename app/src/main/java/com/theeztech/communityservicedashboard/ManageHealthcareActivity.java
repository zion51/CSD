package com.theeztech.communityservicedashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class ManageHealthcareActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialCardView cardServices, cardDoctors, cardSerials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_healthcare);

        initViews();
        setupToolbar();
        setupListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cardServices = findViewById(R.id.cardServices);
        cardDoctors = findViewById(R.id.cardDoctors);
        cardSerials = findViewById(R.id.cardSerials);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        cardServices.setOnClickListener(v -> {
            startActivity(new Intent(this, ServiceManagementActivity.class));
        });

        cardDoctors.setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorManagementActivity.class));
        });

        cardSerials.setOnClickListener(v -> {
            startActivity(new Intent(this, SerialManagementActivity.class));
        });
    }
}
