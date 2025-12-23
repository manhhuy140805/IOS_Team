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

    // Mô tả bản thân, sở thích, địa điểm, thời gian... tất cả trong 1 ô
    // AI sẽ tự phân tích và tìm sự kiện phù hợp
    private String query;
}
