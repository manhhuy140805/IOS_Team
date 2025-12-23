package io.volunteerapp.volunteer_app.DTO.requeset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchRequest {

    // Sở thích, thói quen của người dùng
    private String interests;

    // Địa điểm mong muốn
    private String location;

    // Query tìm kiếm tự do (optional)
    private String query;
}
