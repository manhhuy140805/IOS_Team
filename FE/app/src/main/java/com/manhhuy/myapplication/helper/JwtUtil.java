package com.manhhuy.myapplication.helper;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class để decode JWT token và lấy thông tin từ token
 * JWT token có format: header.payload.signature
 * Payload chứa các claims (thông tin) được encode base64
 */
public class JwtUtil {
    
    private static final String TAG = "JwtUtil";
    
    /**
     * Decode JWT token và trả về payload dưới dạng JSONObject
     * @param token JWT token string
     * @return JSONObject chứa các claims, null nếu lỗi
     */
    public static JSONObject decodePayload(String token) {
        try {
            // JWT token có format: header.payload.signature
            String[] parts = token.split("\\.");
            
            if (parts.length != 3) {
                Log.e(TAG, "Invalid JWT token format");
                return null;
            }
            
            // Lấy payload (phần thứ 2)
            String payload = parts[1];
            
            // Decode base64 (URL_SAFE vì JWT dùng base64url)
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedString = new String(decodedBytes, "UTF-8");
            
            // Parse thành JSONObject
            return new JSONObject(decodedString);
            
        } catch (Exception e) {
            Log.e(TAG, "Error decoding JWT token", e);
            return null;
        }
    }
    
    /**
     * Lấy userId từ token
     * @param token JWT token
     * @return userId (Integer), null nếu không tìm thấy
     */
    public static Integer getUserId(String token) {
        JSONObject payload = decodePayload(token);
        if (payload == null) return null;
        
        try {
            // Backend lưu userId trong claim "userId"
            if (payload.has("userId")) {
                // Handle both Long and Integer types
                Object userIdObj = payload.get("userId");
                if (userIdObj instanceof Integer) {
                    return (Integer) userIdObj;
                } else if (userIdObj instanceof Long) {
                    return ((Long) userIdObj).intValue();
                } else {
                    // Try to parse as int
                    return Integer.parseInt(userIdObj.toString());
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error getting userId from token", e);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing userId as integer", e);
        }
        return null;
    }
    
    /**
     * Lấy email (subject) từ token
     * @param token JWT token
     * @return email string, null nếu không tìm thấy
     */
    public static String getEmail(String token) {
        JSONObject payload = decodePayload(token);
        if (payload == null) return null;
        
        try {
            // Backend lưu email trong claim "sub" (subject)
            if (payload.has("sub")) {
                return payload.getString("sub");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error getting email from token", e);
        }
        return null;
    }
    
    /**
     * Lấy role từ token
     * @param token JWT token
     * @return role string (VD: "ROLE_ADMIN" hoặc "ROLE_USER"), null nếu không tìm thấy
     */
    public static String getRole(String token) {
        JSONObject payload = decodePayload(token);
        if (payload == null) return null;
        
        try {
            // Backend lưu role trong claim "scope" với format "ROLE_ADMIN" hoặc "ROLE_USER"
            if (payload.has("scope")) {
                String scope = payload.getString("scope");
                return scope; // Trả về "ROLE_ADMIN" hoặc "ROLE_USER"
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error getting role from token", e);
        }
        return null;
    }
    
    /**
     * Lấy role ngắn gọn (không có prefix ROLE_)
     * @param token JWT token
     * @return "ADMIN" hoặc "USER", null nếu không tìm thấy
     */
    public static String getRoleSimple(String token) {
        String role = getRole(token);
        if (role != null && role.startsWith("ROLE_")) {
            return role.substring(5); // Bỏ "ROLE_" prefix
        }
        return role;
    }
    
    /**
     * Kiểm tra token có phải là admin không
     * @param token JWT token
     * @return true nếu là admin, false nếu không
     */
    public static boolean isAdmin(String token) {
        String role = getRole(token);
        return "ROLE_ADMIN".equals(role);
    }
    
    /**
     * Kiểm tra token có hết hạn chưa
     * @param token JWT token
     * @return true nếu đã hết hạn, false nếu còn hiệu lực
     */
    public static boolean isExpired(String token) {
        JSONObject payload = decodePayload(token);
        if (payload == null) return true;
        
        try {
            if (payload.has("exp")) {
                long expirationTime = payload.getLong("exp"); // Unix timestamp (seconds)
                long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
                return currentTime >= expirationTime;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error checking token expiration", e);
        }
        return true; // Nếu không có exp claim, coi như hết hạn
    }
}
