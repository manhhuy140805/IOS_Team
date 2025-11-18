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
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";
    
    private static Retrofit retrofit = null;
    private static Context appContext;
    
    /**
     * Khởi tạo context cho ApiConfig
     */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }
    
    /**
     * Lấy Retrofit instance với authentication
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();
                        
                        // Thêm Authorization header nếu có token
                        String token = getToken();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }
                        
                        requestBuilder.method(original.method(), original.body());
                        return chain.proceed(requestBuilder.build());
                    })
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
    private static String getToken() {
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
}
