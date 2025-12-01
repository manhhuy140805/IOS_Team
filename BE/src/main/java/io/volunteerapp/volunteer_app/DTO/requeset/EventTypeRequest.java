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
public class EventTypeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
