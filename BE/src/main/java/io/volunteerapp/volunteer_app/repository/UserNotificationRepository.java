package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    // Lấy tất cả UserNotification của user
    List<UserNotification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Lấy UserNotification chưa đọc của user
    List<UserNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);

    // Lấy UserNotification đã đọc của user
    List<UserNotification> findByUserIdAndIsReadTrueOrderByCreatedAtDesc(Integer userId);

    // Tìm UserNotification theo userId và notificationId
    @Query("SELECT un FROM UserNotification un WHERE un.user.id = :userId AND un.notification.id = :notificationId")
    Optional<UserNotification> findByUserIdAndNotificationId(@Param("userId") Integer userId, @Param("notificationId") Integer notificationId);

    // Đánh dấu tất cả thông báo của user là đã đọc
    @Modifying
    @Query("UPDATE UserNotification un SET un.isRead = true, un.readAt = CURRENT_TIMESTAMP WHERE un.user.id = :userId AND un.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Integer userId);
}
