package com.theeztech.communityservicedashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfilePhone, tvDetailPhone, tvDetailBlood, tvAdminBadge;
    private TextView navHeaderName, navHeaderPhone;
    private MaterialSwitch switchDonor;
    private LinearLayout layoutDonationDate;
    private TextInputEditText etDonationDate;
    private MaterialButton btnUpdateDonation;
    private MaterialCardView cardManageHealthcare, cardManageBusiness, cardAdminPanel;
    private MaterialButton btnManageHealthcare, btnManageBusiness, btnAdminPanel;

    private static final String ADMIN_PHONE = "01751425450";

    private SharedPreferences sharedPreferences;
    private String userPhone = "";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnSettings;

    private static final String URL_GET_USER = "https://csd.theeztech.xyz/api/get_user.php";
    private static final String URL_UPDATE_DONOR = "https://csd.theeztech.xyz/api/update_donor_status.php";
    private static final String URL_UPDATE_DONATION_DATE = "https://csd.theeztech.xyz/api/update_donation_date.php";
    private static final String URL_CHECK_HEALTHCARE = "https://csd.theeztech.xyz/api/check_healthcare_status.php";
    private static final String URL_GET_MY_BUSINESS = "https://csd.theeztech.xyz/api/get_my_business.php";

    private boolean isSwitchInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userPhone = sharedPreferences.getString(MainActivity.KEY_PHONE, "").trim();

        initViews();
        loadLocalData();
        setupDrawer();
        setupBottomNav();
        setupListeners();

        if (!userPhone.isEmpty()) {
            fetchUserProfileFromServer(userPhone);
            checkHealthcareStatus();
            checkBusinessStatus();
            checkAdminStatus();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailBlood = findViewById(R.id.tvDetailBlood);
        tvAdminBadge = findViewById(R.id.tvAdminBadge);
        switchDonor = findViewById(R.id.switchDonor);
        layoutDonationDate = findViewById(R.id.layoutDonationDate);
        etDonationDate = findViewById(R.id.etDonationDate);
        btnUpdateDonation = findViewById(R.id.btnUpdateDonation);
        cardManageHealthcare = findViewById(R.id.cardManageHealthcare);
        btnManageHealthcare = findViewById(R.id.btnManageHealthcare);
        cardManageBusiness = findViewById(R.id.cardManageBusiness);
        btnManageBusiness = findViewById(R.id.btnManageBusiness);
        cardAdminPanel = findViewById(R.id.cardAdminPanel);
        btnAdminPanel = findViewById(R.id.btnAdminPanel);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnSettings = findViewById(R.id.btnSettings);

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            navHeaderName = headerView.findViewById(R.id.navHeaderName);
            navHeaderPhone = headerView.findViewById(R.id.navHeaderPhone);
        }
    }

    private void loadLocalData() {
        String name = sharedPreferences.getString(MainActivity.KEY_NAME, "User");
        String phone = sharedPreferences.getString(MainActivity.KEY_PHONE, "");
        String blood = sharedPreferences.getString(MainActivity.KEY_BLOOD, "Unknown");
        updateUI(name, phone, blood);
    }

    private void updateUI(String name, String phone, String blood) {
        if (tvProfileName != null) tvProfileName.setText(name);
        if (tvProfilePhone != null) tvProfilePhone.setText(phone);
        if (tvDetailPhone != null) tvDetailPhone.setText(getString(R.string.phone_label, phone));
        if (tvDetailBlood != null) tvDetailBlood.setText(getString(R.string.blood_group_label, blood));
        if (navHeaderName != null) navHeaderName.setText(name);
        if (navHeaderPhone != null) navHeaderPhone.setText(phone);
    }

    private void setupDrawer() {
        btnSettings.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_registration_transport) showRegisterTransportDialog();
            else if (itemId == R.id.nav_register_healthcare) showRegisterHealthcareDialog();
            else if (itemId == R.id.nav_register_business) showRegisterBusinessDialog();
            else if (itemId == R.id.nav_become_tutor) showRegisterTutorDialog();
            else if (itemId == R.id.nav_admin_panel) startActivity(new Intent(this, AdminControlPanelActivity.class));
            else if (itemId == R.id.nav_logout) logoutUser();
            
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) return true;
            Intent intent = null;
            if (itemId == R.id.nav_home) intent = new Intent(this, HomeActivity.class);
            else if (itemId == R.id.nav_services) intent = new Intent(this, ServicesActivity.class);

            
            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void showRegisterTransportDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_transport, null);
        AutoCompleteTextView spinnerVehicleType = dialogView.findViewById(R.id.spinnerVehicleType);
        TextInputEditText etVehicleNumber = dialogView.findViewById(R.id.etVehicleNumber);
        TextInputEditText etDriverName = dialogView.findViewById(R.id.etDriverName);
        TextInputEditText etContactNumber = dialogView.findViewById(R.id.etContactNumber);

        if (!userPhone.isEmpty()) {
            if (etContactNumber != null) etContactNumber.setText(userPhone);
            String userName = sharedPreferences.getString(MainActivity.KEY_NAME, "");
            if (etDriverName != null && !userName.isEmpty()) etDriverName.setText(userName);
        }

        String[] vehicleTypes = {"Ambulance", "Car", "Microbus", "Pickup", "Rickshaw", "Truck"};
        spinnerVehicleType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehicleTypes));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Register Transport").setView(dialogView).setNegativeButton("Cancel", null).setPositiveButton("Register", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String vehicleType = spinnerVehicleType.getText().toString().trim();
            String vehicleNumber = etVehicleNumber.getText() != null ? etVehicleNumber.getText().toString().trim() : "";
            String driverName = etDriverName.getText() != null ? etDriverName.getText().toString().trim() : "";
            String contactNumber = etContactNumber.getText() != null ? etContactNumber.getText().toString().trim() : "";

            if (vehicleType.isEmpty()) { spinnerVehicleType.setError("Required"); return; }
            if (vehicleNumber.isEmpty()) { etVehicleNumber.setError("Required"); return; }
            if (driverName.isEmpty()) { etDriverName.setError("Required"); return; }
            if (contactNumber.isEmpty()) { etContactNumber.setError("Required"); return; }

            registerTransport(vehicleType, vehicleNumber, driverName, contactNumber, dialog);
        });
    }

    private void registerTransport(String type, String num, String name, String contact, AlertDialog dialog) {
        String url = "https://csd.theeztech.xyz/api/register_transport.php";
        String idStr = sharedPreferences.getString(MainActivity.KEY_USER_ID, userPhone);
        ProgressDialog pd = new ProgressDialog(this); pd.setMessage("Registering..."); pd.show();

        StringRequest sr = new StringRequest(Request.Method.POST, url, response -> {
            pd.dismiss();
            try { 
                if (new JSONObject(response).optBoolean("success")) {
                    new MaterialAlertDialogBuilder(ProfileActivity.this)
                            .setTitle("Registration Successful")
                            .setMessage("আপনার রেজিস্ট্রেশন সফল হয়েছে। আপনার ইউজার ফোন নম্বর (" + userPhone + ") দিয়ে 'Business Registration' লিখে আমাদের টেলিগ্রামে একটি মেসেজ দিন।")
                            .setPositiveButton("Telegram", (d, w) -> {
                                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/csd_kaharole"));
                                startActivity(it);
                            })
                            .setNegativeButton("OK", (d, w) -> dialog.dismiss())
                            .show();
                } 
            } catch (Exception e) {}
        }, error -> { pd.dismiss(); Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show(); }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>(); p.put("user_id", idStr); p.put("vehicle_type", type); p.put("vehicle_number", num); p.put("driver_name", name); p.put("contact_number", contact); return p;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    private void showRegisterHealthcareDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_healthcare, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etHealthcareName);
        AutoCompleteTextView spinnerType = dialogView.findViewById(R.id.spinnerHealthcareType);
        TextInputEditText etContact = dialogView.findViewById(R.id.etContactNumber);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etAddress);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);

        if (etContact != null && !userPhone.isEmpty()) etContact.setText(userPhone);
        String[] types = {"Hospital", "Clinic", "Diagnostic Center", "Pharmacy", "Doctor's Chamber"};
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Register Healthcare").setView(dialogView).setNegativeButton("Cancel", null).setPositiveButton("Register", null);
        AlertDialog dialog = builder.create(); dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String type = spinnerType.getText().toString().trim();
            String contact = etContact.getText() != null ? etContact.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
            String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

            if (name.isEmpty() || type.isEmpty() || contact.isEmpty()) { Toast.makeText(this, "Required fields missing", Toast.LENGTH_SHORT).show(); return; }
            submitHealthcareRegistration(name, type, contact, email, address, description, dialog);
        });
    }

    private void submitHealthcareRegistration(String name, String type, String contact, String email, String address, String description, AlertDialog dialog) {
        String url = "https://csd.theeztech.xyz/api/register_healthcare.php";
        ProgressDialog pd = new ProgressDialog(this); pd.setMessage("Registering..."); pd.show();
        StringRequest sr = new StringRequest(Request.Method.POST, url, response -> {
            pd.dismiss();
            try { 
                if ("success".equals(new JSONObject(response).optString("status"))) {
                    new MaterialAlertDialogBuilder(ProfileActivity.this)
                            .setTitle("Registration Successful")
                            .setMessage("আপনার রেজিস্ট্রেশন সফল হয়েছে। আপনার ইউজার ফোন নম্বর (" + userPhone + ") দিয়ে 'Healthcare Registration' লিখে আমাদের টেলিগ্রামে একটি মেসেজ দিন।")
                            .setPositiveButton("Telegram", (d, w) -> {
                                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/csd_kaharole"));
                                startActivity(it);
                            })
                            .setNegativeButton("OK", (d, w) -> dialog.dismiss())
                            .show();
                } 
            } catch (Exception e) {}
        }, error -> { pd.dismiss(); Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show(); }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>(); p.put("healthcare_name", name); p.put("healthcare_type", type); p.put("contact_number", contact); p.put("email", email); p.put("address", address); p.put("description", description); p.put("owner_phone", userPhone); return p;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    private void showRegisterBusinessDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_business, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etBusinessName);
        TextInputEditText etOwner = dialogView.findViewById(R.id.etBusinessOwner);
        TextInputEditText etMobile = dialogView.findViewById(R.id.etBusinessMobile);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerBusinessCategory);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etBusinessAddress);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etBusinessDesc);

        if (!userPhone.isEmpty()) {
            if (etMobile != null) etMobile.setText(userPhone);
            String userName = sharedPreferences.getString(MainActivity.KEY_NAME, "");
            if (etOwner != null && !userName.isEmpty()) etOwner.setText(userName);
        }

        String[] categories = {"Shop", "Restaurant", "Agriculture", "Electronics", "Hotel", "Hospital/Clinic", "Pharmacy", "Beauty Salon", "Hardware", "Clothing", "Mobile & Computer", "Education", "Other"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Business Registration").setView(dialogView).setNegativeButton("Cancel", null).setPositiveButton("Register", null);
        AlertDialog dialog = builder.create(); dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String owner = etOwner.getText() != null ? etOwner.getText().toString().trim() : "";
            String mobile = etMobile.getText() != null ? etMobile.getText().toString().trim() : "";
            String category = spinnerCategory.getText().toString().trim();
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

            if (name.isEmpty() || owner.isEmpty() || mobile.isEmpty() || category.isEmpty() || address.isEmpty()) { Toast.makeText(this, "Fields missing", Toast.LENGTH_SHORT).show(); return; }
            submitBusinessRegistration(name, owner, mobile, category, address, desc, dialog);
        });
    }

    private void submitBusinessRegistration(String name, String owner, String mobile, String category, String address, String desc, AlertDialog dialog) {
        String url = "https://csd.theeztech.xyz/api/add_business.php";
        ProgressDialog pd = new ProgressDialog(this); pd.setMessage("Registering..."); pd.show();
        StringRequest sr = new StringRequest(Request.Method.POST, url, response -> {
            pd.dismiss();
            try { 
                if (new JSONObject(response).optBoolean("success")) {
                    new MaterialAlertDialogBuilder(ProfileActivity.this)
                            .setTitle("Registration Successful")
                            .setMessage("আপনার রেজিস্ট্রেশন সফল হয়েছে। আপনার ইউজার ফোন নম্বর (" + userPhone + ") দিয়ে 'Business Registration' লিখে আমাদের টেলিগ্রামে একটি মেসেজ দিন।")
                            .setPositiveButton("Telegram", (d, w) -> {
                                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/csd_kaharole"));
                                startActivity(it);
                            })
                            .setNegativeButton("OK", (d, w) -> dialog.dismiss())
                            .show();
                } 
            } catch (Exception e) {}
        }, error -> { pd.dismiss(); Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show(); }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>(); p.put("user_phone", userPhone); p.put("business_name", name); p.put("owner_name", owner); p.put("mobile", mobile); p.put("category", category); p.put("address", address); p.put("description", desc); return p;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    private void showRegisterTutorDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_tutor, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etTutorName);
        TextInputEditText etEdu = dialogView.findViewById(R.id.etTutorEducation);
        TextInputEditText etSubjects = dialogView.findViewById(R.id.etTutorSubjects);
        TextInputEditText etSalary = dialogView.findViewById(R.id.etTutorSalary);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etTutorAddress);
        TextInputEditText etExp = dialogView.findViewById(R.id.etTutorExperience);

        String userName = sharedPreferences.getString(MainActivity.KEY_NAME, "");
        if (etName != null && !userName.isEmpty()) etName.setText(userName);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Become a Tutor").setView(dialogView).setNegativeButton("Cancel", null).setPositiveButton("Register", null);
        AlertDialog dialog = builder.create(); dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String edu = etEdu.getText() != null ? etEdu.getText().toString().trim() : "";
            String subjects = etSubjects.getText() != null ? etSubjects.getText().toString().trim() : "";
            String salary = etSalary.getText() != null ? etSalary.getText().toString().trim() : "";
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
            String experience = etExp.getText() != null ? etExp.getText().toString().trim() : "";

            if (name.isEmpty() || edu.isEmpty() || subjects.isEmpty()) { Toast.makeText(this, "Required fields missing", Toast.LENGTH_SHORT).show(); return; }
            submitTutorRegistration(name, edu, subjects, salary, address, experience, dialog);
        });
    }

    private void submitTutorRegistration(String name, String edu, String subjects, String salary, String address, String exp, AlertDialog dialog) {
        String url = "https://csd.theeztech.xyz/api/register_tutor.php";
        ProgressDialog pd = new ProgressDialog(this); pd.setMessage("Registering..."); pd.show();
        StringRequest sr = new StringRequest(Request.Method.POST, url, response -> {
            pd.dismiss();
            try { 
                if (new JSONObject(response).optBoolean("success")) {
                    new MaterialAlertDialogBuilder(ProfileActivity.this)
                            .setTitle("Registration Successful")
                            .setMessage("আপনার রেজিস্ট্রেশন সফল হয়েছে। আপনার ইউজার ফোন নম্বর (" + userPhone + ") দিয়ে 'Tutor Registration' লিখে আমাদের টেলিগ্রামে একটি মেসেজ দিন।")
                            .setPositiveButton("Telegram", (d, w) -> {
                                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/csd_kaharole"));
                                startActivity(it);
                            })
                            .setNegativeButton("OK", (d, w) -> dialog.dismiss())
                            .show();
                } 
            } catch (Exception e) {}
        }, error -> { pd.dismiss(); Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show(); }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>(); p.put("user_phone", userPhone); p.put("name", name); p.put("education", edu); p.put("subjects", subjects); p.put("expected_salary", salary); p.put("address", address); p.put("experience", exp); return p;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    private void logoutUser() {
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent); finish();
    }

    private void setupListeners() {
        btnManageHealthcare.setOnClickListener(v -> startActivity(new Intent(this, ManageHealthcareActivity.class)));
        btnManageBusiness.setOnClickListener(v -> startActivity(new Intent(this, ManageBusinessActivity.class)));
        btnAdminPanel.setOnClickListener(v -> startActivity(new Intent(this, AdminControlPanelActivity.class)));
        etDonationDate.setOnClickListener(v -> showDatePicker());
        btnUpdateDonation.setOnClickListener(v -> {
            String date = etDonationDate.getText() != null ? etDonationDate.getText().toString().trim() : "";
            if (!date.isEmpty()) updateDonationDate(date);
        });
        switchDonor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSwitchInitialized) return;
            if (isChecked) showDonorConditionsDialog();
            else updateDonorStatus(0);
        });
    }

    private void showDonorConditionsDialog() {
        new MaterialAlertDialogBuilder(this).setTitle(R.string.donor_requirements_title).setMessage(R.string.donor_requirements_message).setPositiveButton(R.string.i_agree, (dialog, which) -> updateDonorStatus(1)).setNegativeButton(R.string.cancel, (dialog, which) -> { isSwitchInitialized = false; switchDonor.setChecked(false); isSwitchInitialized = true; }).setCancelable(false).show();
    }

    private void revertDonorSwitch(int failedStatus) {
        isSwitchInitialized = false; switchDonor.setChecked(failedStatus != 1); isSwitchInitialized = true;
    }

    private void checkHealthcareStatus() {
        String url = URL_CHECK_HEALTHCARE + "?phone=" + userPhone;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try { JSONObject obj = new JSONObject(response); if ("approved".equalsIgnoreCase(obj.optString("status"))) { String id = obj.optString("id", ""); if (!id.isEmpty()) sharedPreferences.edit().putString("healthcare_id", id).apply(); cardManageHealthcare.setVisibility(View.VISIBLE); } } catch (Exception e) {}
        }, error -> {});
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void checkBusinessStatus() {
        String url = URL_GET_MY_BUSINESS + "?user_phone=" + userPhone;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try { JSONObject obj = new JSONObject(response); String status = obj.optString("status", ""); String id = obj.optString("id", ""); if (obj.has("business")) { JSONObject b = obj.getJSONObject("business"); status = b.optString("status", status); id = b.optString("id", id); } if ("approved".equalsIgnoreCase(status)) { if (!id.isEmpty()) sharedPreferences.edit().putString("business_id", id).apply(); cardManageBusiness.setVisibility(View.VISIBLE); } } catch (Exception e) {}
        }, error -> {});
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void checkAdminStatus() {
        boolean isAdmin = ADMIN_PHONE.equals(userPhone);
        cardAdminPanel.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        tvAdminBadge.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        navigationView.getMenu().findItem(R.id.nav_admin_panel).setVisible(isAdmin);
    }

    private void fetchUserProfileFromServer(String phone) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_USER, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if ("success".equals(obj.optString("status"))) {
                    String name = obj.optString("name", "User"); String blood = obj.optString("blood_group", "Unknown"); int isDonor = obj.optInt("is_donor", 0); String lastDate = obj.optString("last_donation_date", ""); String userId = obj.optString("id", "");
                    SharedPreferences.Editor editor = sharedPreferences.edit(); editor.putString(MainActivity.KEY_NAME, name); editor.putString(MainActivity.KEY_BLOOD, blood); editor.putString(MainActivity.KEY_USER_ID, userId); editor.apply();
                    updateUI(name, phone, blood); isSwitchInitialized = false; switchDonor.setChecked(isDonor == 1); layoutDonationDate.setVisibility(isDonor == 1 ? View.VISIBLE : View.GONE); etDonationDate.setText(lastDate); isSwitchInitialized = true;
                }
            } catch (Exception e) {}
        }, error -> {}) { @Override protected Map<String, String> getParams() { Map<String, String> params = new HashMap<>(); params.put("phone", phone); return params; } };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateDonorStatus(int status) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_DONOR, response -> {
            try { if ("success".equals(new JSONObject(response).optString("status"))) { layoutDonationDate.setVisibility(status == 1 ? View.VISIBLE : View.GONE); } else revertDonorSwitch(status); } catch (Exception e) { revertDonorSwitch(status); }
        }, error -> revertDonorSwitch(status)) { @Override protected Map<String, String> getParams() { Map<String, String> params = new HashMap<>(); params.put("phone", userPhone); params.put("is_donor", String.valueOf(status)); return params; } };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateDonationDate(String date) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_DONATION_DATE, response -> {
            try { if ("success".equals(new JSONObject(response).optString("status"))) Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show(); } catch (Exception e) {}
        }, error -> {}) { @Override protected Map<String, String> getParams() { Map<String, String> params = new HashMap<>(); params.put("phone", userPhone); params.put("last_donation_date", date); return params; } };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (v, y, m, d) -> { etDonationDate.setText(String.format(Locale.US, "%d-%02d-%02d", y, m + 1, d)); }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
