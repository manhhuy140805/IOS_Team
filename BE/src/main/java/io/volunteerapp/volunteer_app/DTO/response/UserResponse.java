package io.volunteerapp.volunteer_app.DTO.response;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;
    private Integer totalPoints;
    private String address;
    
    // New fields for Admin UI
    private Instant createdAt;      // For joinDate/foundedDate
    private Instant updatedAt;      // For lastActive
    private Boolean violation;      // For violation warning
    private Integer activityCount;  // For user: số events đã tham gia, for org: số events đã tạo
    private LocalDate dateOfBirth;  // Date of birth
    private String gender;          // MALE, FEMALE, OTHER
}
