package com.manhhuy.myapplication.helper;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiConfig {
    
    // Base URL của backend API
    private static final String BASE_URL = "http://103.37.60.236:8888/api/v1/";
    
    private static Retrofit retrofit = null;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();
                        
                        // Thêm Authorization header nếu có token
                        String token = getToken();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }
                        
                        requestBuilder.method(original.method(), original.body());
                        return chain.proceed(requestBuilder.build());})
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Lấy token từ SharedPreferences
     */
    public static String getToken() {
        if (appContext == null) return null;
        SharedPreferences prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("access_token", null);
    }
    
    /**
     * Lưu token vào SharedPreferences
     */
    public static void saveToken(String token) {
        if (appContext == null) return;
        SharedPreferences prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("access_token", token).apply();
    }
    
    /**
     * Xóa token (logout)
     */
    public static void clearToken() {
        if (appContext == null) return;
        SharedPreferences prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("access_token").apply();
    }
    
    /**
     * Kiểm tra xem user đã login chưa
     */
    public static boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }
    
    /**
     * Kiểm tra token có hợp lệ không (tồn tại và chưa hết hạn)
     */
    public static boolean isTokenValid() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return false;
        }
        return !JwtUtil.isExpired(token);
    }
    
    
    public static String getUserRole() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        return JwtUtil.getRoleSimple(token);
    }
    
    public static boolean isVolunteer() {
        return "VOLUNTEER".equalsIgnoreCase(getUserRole());
    }
    

    public static boolean isOrganizer() {
        return "ORGANIZATION".equalsIgnoreCase(getUserRole());
    }
    
    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getUserRole());
    }
    
    /**
     * Lấy userId từ token
     */
    public static Integer getUserId() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        return JwtUtil.getUserId(token);
    }
    
    /**
     * Lấy email từ token
     */
    public static String getUserEmail() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        return JwtUtil.getEmail(token);
    }
    public static String getStatus(){
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        return JwtUtil.getStatus(token);
    }

}
