package com.theeztech.communityservicedashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etPhone, etPassword;
    Button btnLogin;
    TextView registration;
    RelativeLayout loadingLayout;

    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_NAME = "user_session";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NAME = "name";
    public static final String KEY_BLOOD = "blood_group";
    public static final String KEY_LOGGED_IN = "isLoggedIn";

    String URL = "https://csd.theeztech.xyz/api/login.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(KEY_LOGGED_IN, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        registration = findViewById(R.id.registration);
        loadingLayout = findViewById(R.id.loadingLayout);

        btnLogin.setOnClickListener(v -> loginUser());
        registration.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter phone and password", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingLayout.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.optString("status").equals("success")){

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_LOGGED_IN, true);

                            if (obj.has("user")) {
                                JSONObject user = obj.getJSONObject("user");
                                editor.putString(KEY_USER_ID, user.optString("id"));
                                editor.putString(KEY_PHONE, user.optString("phone"));
                                editor.putString(KEY_NAME, user.optString("name", "User"));
                                editor.putString(KEY_BLOOD, user.optString("blood_group", "Unknown"));
                            } else {
                                // Fallback if user object is missing
                                editor.putString(KEY_PHONE, phone);
                            }

                            editor.apply();

                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, obj.optString("message", "Login failed"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingLayout.setVisibility(View.GONE);
                    Toast.makeText(this, NetworkUtils.getVolleyError(error), Toast.LENGTH_SHORT).show();
                }
        ){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}