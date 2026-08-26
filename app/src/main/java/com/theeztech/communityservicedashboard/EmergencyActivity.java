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
import com.google.android.material.card.MaterialCardView;

public class EmergencyActivity extends AppCompatActivity {

    MaterialCardView card999, cardFire, cardAmbulance, cardWomen, cardGovt, cardRAB, cardGas, cardElectricity, cardACC;
    MaterialButton btnCall999, btnCallFire, btnCallAmb, btnCallWomen, btnCallGovt, btnCallRAB, btnCallGas, btnCallElectricity, btnCallACC;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Cards
        card999 = findViewById(R.id.card999);
        cardFire = findViewById(R.id.cardFire);
        cardAmbulance = findViewById(R.id.cardAmbulance);
        cardWomen = findViewById(R.id.cardWomen);
        cardGovt = findViewById(R.id.cardGovt);
        cardRAB = findViewById(R.id.cardRAB);
        cardGas = findViewById(R.id.cardGas);
        cardElectricity = findViewById(R.id.cardElectricity);
        cardACC = findViewById(R.id.cardACC);

        // Initialize Buttons
        btnCall999 = findViewById(R.id.btnCall999);
        btnCallFire = findViewById(R.id.btnCallFire);
        btnCallAmb = findViewById(R.id.btnCallAmb);
        btnCallWomen = findViewById(R.id.btnCallWomen);
        btnCallGovt = findViewById(R.id.btnCallGovt);
        btnCallRAB = findViewById(R.id.btnCallRAB);
        btnCallGas = findViewById(R.id.btnCallGas);
        btnCallElectricity = findViewById(R.id.btnCallElectricity);
        btnCallACC = findViewById(R.id.btnCallACC);



        // Set Click Listeners for Buttons
        btnCall999.setOnClickListener(v -> makeCall("999"));
        btnCallFire.setOnClickListener(v -> makeCall("102"));
        btnCallAmb.setOnClickListener(v -> makeCall("16263"));
        btnCallWomen.setOnClickListener(v -> makeCall("109"));
        btnCallGovt.setOnClickListener(v -> makeCall("333"));
        btnCallRAB.setOnClickListener(v -> makeCall("01777711099"));
        btnCallGas.setOnClickListener(v -> makeCall("16496"));
        btnCallElectricity.setOnClickListener(v -> makeCall("16120"));
        btnCallACC.setOnClickListener(v -> makeCall("106"));

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