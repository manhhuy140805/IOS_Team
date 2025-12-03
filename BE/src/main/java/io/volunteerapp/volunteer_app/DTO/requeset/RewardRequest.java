package io.volunteerapp.volunteer_app.DTO.requeset;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Type is required")
    private String type; // VOUCHER, GIFT, EXPERIENCE

    private String imageUrl;

    @NotNull(message = "Points required is required")
    @Min(value = 1, message = "Points required must be at least 1")
    private Integer pointsRequired;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;

    private String status;

    private LocalDate expiryDate;

    private Integer providerId;
}
