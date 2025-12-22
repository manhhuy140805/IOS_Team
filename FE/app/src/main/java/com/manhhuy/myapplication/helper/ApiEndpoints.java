package com.manhhuy.myapplication.helper;

import com.manhhuy.myapplication.helper.request.ClaimRewardRequest;
import com.manhhuy.myapplication.helper.request.EventRegistrationRequest;
import com.manhhuy.myapplication.helper.request.EventRequest;
import com.manhhuy.myapplication.helper.request.LoginRequest;
import com.manhhuy.myapplication.helper.request.RegisterRequest;
import com.manhhuy.myapplication.helper.request.UpdateUserRequest;
import com.manhhuy.myapplication.helper.response.ClaimRewardResponse;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.LoginResponse;
import com.manhhuy.myapplication.helper.response.NotificationResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.RewardResponse;
import com.manhhuy.myapplication.helper.response.RewardTypeResponse;
import com.manhhuy.myapplication.helper.response.UserNotificationResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndpoints {

        @POST("auth/register")
        Call<RestResponse<UserResponse>> register(@Body RegisterRequest request);

        @POST("auth/login")
        Call<RestResponse<LoginResponse>> login(@Body LoginRequest request);

        @GET("users/me")
        Call<RestResponse<UserResponse>> getCurrentUser();

        @GET("users")
        Call<RestResponse<List<UserResponse>>> getAllUsers();

        @GET("users/{id}")
        Call<RestResponse<UserResponse>> getUserById(@Path("id") Integer id);

        @PUT("users/{id}")
        Call<RestResponse<UserResponse>> updateUser(@Path("id") Integer id, @Body UpdateUserRequest request);

        @DELETE("users/{id}")
        Call<RestResponse<Void>> deleteUser(@Path("id") Integer id);

        @POST("users/{id}/promote-to-admin")
        Call<RestResponse<UserResponse>> promoteToAdmin(@Path("id") Integer id);

        @GET("event-types")
        Call<RestResponse<List<EventTypeResponse>>> getEventTypes();

        @GET("reward-types")
        Call<RestResponse<List<RewardTypeResponse>>> getRewardTypes();

        @GET("rewards")
        Call<RestResponse<PageResponse<RewardResponse>>> getAllRewards(
                        @Query("rewardTypeId") Integer rewardTypeId,
                        @Query("page") int page,
                        @Query("size") int size,
                        @Query("sortBy") String sortBy,
                        @Query("sortDirection") String sortDirection);

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

        @GET("events/my-events")
        Call<RestResponse<PageResponse<EventResponse>>> getMyEvents(
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

        @DELETE("events/{id}")
        Call<Void> deleteEvent(@Path("id") Integer id);

        @POST("event-registrations/register")
        Call<EventRegistrationResponse> registerForEvent(@Body EventRegistrationRequest request);

        @GET("event-registrations/event/{eventId}")
        Call<RestResponse<PageResponse<EventRegistrationResponse>>> getEventRegistrations(
                        @Path("eventId") Integer eventId,
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
        Call<RestResponse<java.util.Map<String, Object>>> sendNotification(
                        @Body com.manhhuy.myapplication.helper.request.SendNotificationRequest request);

        // ========== User Reward APIs ==========

        /**
         * Đổi thưởng - claim reward
         * User ID được lấy từ token bởi backend
         */
        @POST("user-rewards/claim")
        Call<ClaimRewardResponse> claimReward(@Body ClaimRewardRequest request);

        /**
         * Lấy danh sách phần thưởng đã đổi của user hiện tại
         * Backend trả về RestResponse wrapper
         */
        @GET("user-rewards/my-rewards")
        Call<RestResponse<PageResponse<com.manhhuy.myapplication.helper.response.MyRewardResponse>>> getMyRewards(
                        @Query("page") int page,
                        @Query("size") int size);
}
