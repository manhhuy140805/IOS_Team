package io.volunteerapp.volunteer_app.DTO.requeset;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchRequest {

    @NotBlank(message = "Query is required")
    private String query;
}
