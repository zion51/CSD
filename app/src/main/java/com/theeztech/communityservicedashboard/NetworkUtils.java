package com.theeztech.communityservicedashboard;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class NetworkUtils {

    public static String getVolleyError(VolleyError error) {
        if (error instanceof TimeoutError) {
            return "Connection timeout. Please check your internet.";
        } else if (error instanceof NoConnectionError) {
            return "No internet connection. Please turn on mobile data or Wi-Fi.";
        } else if (error instanceof AuthFailureError) {
            return "Authentication failure. Please login again.";
        } else if (error instanceof ServerError) {
            return "Server error. Please try again later.";
        } else if (error instanceof NetworkError) {
            return "Network error. Please check your connection.";
        } else if (error instanceof ParseError) {
            return "Data processing error. Please contact support.";
        }
        return "An unknown error occurred. (" + error.toString() + ")";
    }
}
