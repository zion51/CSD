package com.theeztech.communityservicedashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class AdminControlPanelActivity extends AppCompatActivity {

    private MaterialCardView cardApproveHealthcare, cardApproveBusiness, cardApproveTutor, cardManageUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        super.setContentView(R.layout.activity_admin_control_panel);

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
        cardApproveHealthcare = findViewById(R.id.cardApproveHealthcare);
        cardApproveBusiness = findViewById(R.id.cardApproveBusiness);
        cardApproveTutor = findViewById(R.id.cardApproveTutor);
        cardManageUsers = findViewById(R.id.cardManageUsers);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        cardApproveHealthcare.setOnClickListener(v -> {
            startActivity(new Intent(this, ApproveHealthcareActivity.class));
        });

        cardApproveBusiness.setOnClickListener(v -> {
            startActivity(new Intent(this, ApproveBusinessActivity.class));
        });

        cardApproveTutor.setOnClickListener(v -> {
            startActivity(new Intent(this, ApproveTutorActivity.class));
        });

        cardManageUsers.setOnClickListener(v -> {
            Toast.makeText(this, "User Management system coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}
