package com.theeztech.communityservicedashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class AgricultureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        super.setContentView(R.layout.activity_agriculture);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        MaterialButton btnCallOffice = findViewById(R.id.btnCallOffice);
        MaterialButton btnCallHelpline = findViewById(R.id.btnCallHelpline);
        MaterialButton btnCallOfficer = findViewById(R.id.btnCallOfficer);

        btnCallOffice.setOnClickListener(v -> makeCall("01711000000")); // Replace with actual office number
        btnCallHelpline.setOnClickListener(v -> makeCall("16123"));      // Agriculture Helpline
        btnCallOfficer.setOnClickListener(v -> makeCall("01711111111")); // Replace with actual officer number

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
