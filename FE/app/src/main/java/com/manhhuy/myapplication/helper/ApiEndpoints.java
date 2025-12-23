package com.manhhuy.myapplication.helper;

import com.manhhuy.myapplication.helper.request.ChangePasswordRequest;
import com.manhhuy.myapplication.helper.request.ClaimRewardRequest;
import com.manhhuy.myapplication.helper.request.EventRegistrationRequest;
import com.manhhuy.myapplication.helper.request.EventRequest;
import com.manhhuy.myapplication.helper.request.LoginRequest;
import com.manhhuy.myapplication.helper.request.RegisterRequest;
import com.manhhuy.myapplication.helper.request.RewardRequest;
import com.manhhuy.myapplication.helper.request.SendNotificationRequest;
import com.manhhuy.myapplication.helper.request.UpdateUserRequest;
import com.manhhuy.myapplication.helper.response.ClaimRewardResponse;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.LoginResponse;
import com.manhhuy.myapplication.helper.response.MyRewardResponse;
import com.manhhuy.myapplication.helper.response.NotificationResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.RewardResponse;
import com.manhhuy.myapplication.helper.response.RewardTypeResponse;
import com.manhhuy.myapplication.helper.response.UserNotificationResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;
import com.manhhuy.myapplication.helper.response.UserRewardResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndpoints {

        // ========== Auth APIs ==========

        @POST("auth/register")
        Call<RestResponse<UserResponse>> register(@Body RegisterRequest request);

        @POST("auth/login")
        Call<RestResponse<LoginResponse>> login(@Body LoginRequest request);

        // ========== User APIs ==========

        @GET("users/me")
        Call<RestResponse<UserResponse>> getCurrentUser();

        @GET("users")
        Call<RestResponse<List<UserResponse>>> getAllUsers();

        @GET("users/{id}")
        Call<RestResponse<UserResponse>> getUserById(@Path("id") Integer id);

        @PUT("users/{id}")
        Call<RestResponse<UserResponse>> updateUser(@Path("id") Integer id, @Body UpdateUserRequest request);

        @PUT("users/me")
        Call<RestResponse<UserResponse>> updateCurrentUser(@Body UpdateUserRequest request);

        @PUT("users/me/change-password")
        Call<RestResponse<Void>> changePassword(@Body ChangePasswordRequest request);

        @DELETE("users/{id}")
        Call<RestResponse<Void>> deleteUser(@Path("id") Integer id);

        @POST("users/{id}/promote-to-admin")
        Call<RestResponse<UserResponse>> promoteToAdmin(@Path("id") Integer id);

        // ========== Event Type APIs ==========

        @GET("event-types")
        Call<RestResponse<List<EventTypeResponse>>> getEventTypes();

        // ========== Reward Type APIs ==========

        @GET("reward-types")
        Call<RestResponse<List<RewardTypeResponse>>> getRewardTypes();

        // ========== Reward APIs ==========

        @GET("rewards")
        Call<RestResponse<PageResponse<RewardResponse>>> getAllRewards(
                        @Query("rewardTypeId") Integer rewardTypeId,
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection);

        @GET("rewards/{id}")
        Call<RestResponse<RewardResponse>> getRewardById(@Path("id") Integer id);

        @POST("rewards")
        Call<RestResponse<RewardResponse>> createReward(@Body RewardRequest request);

        @PUT("rewards/{id}")
        Call<RestResponse<RewardResponse>> updateReward(
                        @Path("id") Integer id,
                        @Body RewardRequest rewardRequest);

        @PATCH("rewards/{id}/status")
        Call<RestResponse<RewardResponse>> updateRewardStatus(
                        @Path("id") Integer id,
                        @Query("status") String status);

        @DELETE("rewards/{id}")
        Call<Void> deleteReward(@Path("id") Integer id);

        // ========== Event APIs ==========

        @GET("events")
        Call<RestResponse<PageResponse<EventResponse>>> getAllEvents(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection,
                        @Query("title") String title,
                        @Query("location") String location,
                        @Query("status") String status,
                        @Query("eventTypeId") Integer eventTypeId,
                        @Query("startDateFrom") String startDateFrom,
                        @Query("startDateTo") String startDateTo,
                        @Query("hasCertificate") Boolean hasCertificate,
                        @Query("hasReward") Boolean hasReward);

        @GET("events/{id}")
        Call<RestResponse<EventResponse>> getEventById(@Path("id") Integer id);

        @GET("events/type/{eventTypeId}")
        Call<RestResponse<PageResponse<EventResponse>>> getEventsByType(
                        @Path("eventTypeId") Integer eventTypeId,
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection);

        @GET("events/search")
        Call<RestResponse<PageResponse<EventResponse>>> searchEvents(
                        @Query("query") String query,
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection);

        // AI-powered search
        @POST("events/ai-search")
        Call<RestResponse<com.manhhuy.myapplication.helper.response.AiSearchResponse>> aiSearchEvents(
                        @Body com.manhhuy.myapplication.helper.request.AiSearchRequest request);

        @GET("events/my-events")
        Call<RestResponse<PageResponse<EventResponse>>> getMyEvents(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection);

        // Organization Events - lấy events mà organization đã tạo
        @GET("events/organization/me")
        Call<RestResponse<PageResponse<EventResponse>>> getOrganizationEvents(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection);

        @GET("events/user/{userId}")
        Call<PageResponse<EventResponse>> getEventsByUserId(
                        @Path("userId") Integer userId,
                        @Query("page") int page,
                        @Query("size") int size);

        @POST("events")
        Call<EventResponse> createEvent(@Body EventRequest request);

        @PUT("events/{id}")
        Call<EventResponse> updateEvent(@Path("id") Integer id, @Body EventRequest request);

        @PUT("events/{id}/status")
        Call<RestResponse<EventResponse>> updateEventStatus(@Path("id") Integer id, @Query("status") String status);

        @DELETE("events/{id}")
        Call<Void> deleteEvent(@Path("id") Integer id);

        // ========== Event Registration APIs ==========

        @POST("event-registrations/register")
        Call<EventRegistrationResponse> registerForEvent(@Body EventRegistrationRequest request);

        @GET("event-registrations/event/{eventId}")
        Call<RestResponse<PageResponse<EventRegistrationResponse>>> getEventRegistrations(
                        @Path("eventId") Integer eventId,
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("status") String status);

        @GET("event-registrations/organization/registrations")
        Call<RestResponse<PageResponse<EventRegistrationResponse>>> getMyEventsRegistrations(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("status") String status);

        @GET("event-registrations/my-registrations")
        Call<RestResponse<PageResponse<EventRegistrationResponse>>> getMyRegistrations(
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("status") String status);

        @DELETE("event-registrations/{registrationId}")
        Call<Void> cancelRegistration(@Path("registrationId") Integer registrationId);

        @PUT("event-registrations/{registrationId}/status")
        Call<RestResponse<EventRegistrationResponse>> updateRegistrationStatus(
                        @Path("registrationId") Integer registrationId,
                        @Query("status") String status);

        @PUT("event-registrations/{registrationId}/check-in")
        Call<EventRegistrationResponse> checkInUser(@Path("registrationId") Integer registrationId);

        // ========== Notification APIs ==========

        @GET("notifications/{id}")
        Call<RestResponse<NotificationResponse>> getNotificationById(
                        @Path("id") Integer id,
                        @Query("userId") Integer userId);

        @GET("notifications/user/{userId}")
        Call<RestResponse<List<UserNotificationResponse>>> getUserNotifications(@Path("userId") Integer userId);

        @GET("notifications/user/{userId}/unread")
        Call<RestResponse<List<UserNotificationResponse>>> getUnreadNotifications(@Path("userId") Integer userId);

        @GET("notifications/user/{userId}/read")
        Call<RestResponse<List<UserNotificationResponse>>> getReadNotifications(@Path("userId") Integer userId);

        @PUT("notifications/user/{userId}/read-all")
        Call<RestResponse<Void>> markAllAsRead(@Path("userId") Integer userId);

        @DELETE("notifications/user/{userId}/notification/{notificationId}")
        Call<RestResponse<Void>> deleteUserNotification(
                        @Path("userId") Integer userId,
                        @Path("notificationId") Integer notificationId);

        @POST("notifications/send")
        Call<RestResponse<Map<String, Object>>> sendNotification(@Body SendNotificationRequest request);

        @Multipart
        @POST("notifications/send")
        Call<RestResponse<Map<String, Object>>> sendNotificationMultipart(
                @Part("eventId") okhttp3.RequestBody eventId,
                @Part("title") okhttp3.RequestBody title,
                @Part("content") okhttp3.RequestBody content,
                @Part("recipientType") okhttp3.RequestBody recipientType,
                @Part MultipartBody.Part file
        );

        // ========== User Reward APIs ==========

        /**
         * Đổi thưởng - claim reward
         * User ID được lấy từ token bởi backend
         */
        @POST("user-rewards/claim")
        Call<ClaimRewardResponse> claimReward(@Body ClaimRewardRequest request);

        /**
         * Lấy danh sách phần thưởng đã đổi của user hiện tại
         */
        @GET("user-rewards/my-rewards")
        Call<RestResponse<PageResponse<MyRewardResponse>>> getMyRewards(
                        @Query("page") int page,
                        @Query("size") int size);

        /**
         * Lấy danh sách yêu cầu đổi thưởng đang chờ duyệt (Admin)
         */
        @GET("user-rewards/pending")
        Call<RestResponse<PageResponse<UserRewardResponse>>> getPendingRewards(
                        @Query("page") int page,
                        @Query("size") int size);

        /**
         * Cập nhật trạng thái yêu cầu đổi thưởng (Admin)
         */
        @PUT("user-rewards/{id}/status")
        Call<RestResponse<UserRewardResponse>> updateUserRewardStatus(
                        @Path("id") Integer id,
                        @Query("status") String status);

        // ========== Image Upload APIs ==========

        @Multipart
        @POST("images/upload")
        Call<Map<String, Object>> uploadImage(@Part MultipartBody.Part file);

        // ========== OTP APIs ==========

        @POST("otp/send")
        Call<RestResponse<String>> sendOTP(@Body com.manhhuy.myapplication.helper.request.SendOTPRequest request);

        @POST("otp/verify")
        Call<RestResponse<Boolean>> verifyOTP(@Body com.manhhuy.myapplication.helper.request.VerifyOTPRequest request);

        @POST("otp/reset-password")
        Call<RestResponse<Void>> resetPassword(@Body com.manhhuy.myapplication.helper.request.ResetPasswordRequest request);
}
