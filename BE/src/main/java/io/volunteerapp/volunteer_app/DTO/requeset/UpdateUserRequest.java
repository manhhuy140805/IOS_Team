package io.volunteerapp.volunteer_app.DTO.requeset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String fullName;
    private String phone;
    private String role;
    private String status;
    private String avatarUrl;
    private String address;
}
