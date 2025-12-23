package io.volunteerapp.volunteer_app.DTO.response;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchResponse {

    // Giải thích của AI về các sự kiện được gợi ý
    private String explanation;

    // true nếu tìm thấy kết quả phù hợp, false nếu chỉ là gợi ý liên quan
    private boolean foundMatch;

    // Danh sách sự kiện gợi ý
    private PageResponse<EventResponse> events;
}
