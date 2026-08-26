package com.theeztech.communityservicedashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class EducationActivity extends AppCompatActivity {

    private RecyclerView recyclerTutors;
    private TutorDisplayAdapter tutorAdapter;
    private List<Tutor> tutorList;
    private static final String URL_GET_TUTORS = "https://csd.theeztech.xyz/api/get_tutors.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education);

        setupToolbar();
        setupExamResults();
        setupEventActions();
        setupSkillCourses();
        setupTutorList();

        fetchTutors();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTutorList() {
        recyclerTutors = findViewById(R.id.recyclerTutors);
        tutorList = new ArrayList<>();
        tutorAdapter = new TutorDisplayAdapter(this, tutorList);
        recyclerTutors.setLayoutManager(new LinearLayoutManager(this));
        recyclerTutors.setAdapter(tutorAdapter);
    }

    private void fetchTutors() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_TUTORS,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            JSONArray array = json.getJSONArray("tutors");
                            tutorList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                tutorList.add(new Tutor(
                                        obj.optString("id"),
                                        obj.optString("name"),
                                        obj.optString("phone"),
                                        obj.optString("education"),
                                        obj.optString("subjects"),
                                        obj.optString("expected_salary"),
                                        obj.optString("address"),
                                        obj.optString("experience"),
                                        "approved"
                                ));
                            }
                            tutorAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {}
                },
                error -> {});

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void setupExamResults() {
        findViewById(R.id.btnSSC).setOnClickListener(v -> openUrl("http://www.educationboardresults.gov.bd/"));
        findViewById(R.id.btnHSC).setOnClickListener(v -> openUrl("http://www.educationboardresults.gov.bd/"));
        findViewById(R.id.btnNU).setOnClickListener(v -> openUrl("http://results.nu.ac.bd/"));
        findViewById(R.id.btnHonours).setOnClickListener(v -> openUrl("http://results.nu.ac.bd/"));
    }

    private void setupEventActions() {
        findViewById(R.id.btnSeminar).setOnClickListener(v -> 
            Toast.makeText(this, "No active Seminars at the moment", Toast.LENGTH_SHORT).show());
            
        findViewById(R.id.btnWorkshop).setOnClickListener(v -> 
            Toast.makeText(this, "Upcoming Workshops will be listed here", Toast.LENGTH_SHORT).show());
            
        findViewById(R.id.btnTraining).setOnClickListener(v -> 
            Toast.makeText(this, "Training programs listing coming soon", Toast.LENGTH_SHORT).show());
            
        findViewById(R.id.btnCompetition).setOnClickListener(v -> 
            Toast.makeText(this, "Educational Competitions coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupSkillCourses() {
        findViewById(R.id.btnWebDev).setOnClickListener(v -> openUrl("https://www.w3schools.com/"));
        findViewById(R.id.btnAndroidDev).setOnClickListener(v -> openUrl("https://developer.android.com/"));
        findViewById(R.id.btnGraphicDesign).setOnClickListener(v -> openUrl("https://www.canva.com/"));
        findViewById(R.id.btnVideoEditing).setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=video+editing+tutorial"));
        findViewById(R.id.btnMsOffice).setOnClickListener(v -> openUrl("https://support.microsoft.com/en-us/office"));
        findViewById(R.id.btnCvWriting).setOnClickListener(v -> openUrl("https://www.overleaf.com/gallery/tagged/cv"));
        findViewById(R.id.btnInterviewPrep).setOnClickListener(v -> openUrl("https://www.google.com/search?q=common+interview+questions+and+answers"));
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }
}
