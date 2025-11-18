package com.manhhuy.myapplication.helper;

/**
 * Simplified API Service - chỉ cần 1 method duy nhất
 * Sử dụng: ApiService.api().login(...)
 */
public class ApiService {
    
    private static ApiEndpoints apiInstance;

    public static ApiEndpoints api() {
        if (apiInstance == null) {
            apiInstance = ApiConfig.getClient().create(ApiEndpoints.class);
        }
        return apiInstance;
    }
}
