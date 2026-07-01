package com.parcellocker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelRequest {

    @NotNull(message = "Width is required")
    @Min(value = 1, message = "Width must be at least 1 cm")
    private Integer width;

    @NotNull(message = "Height is required")
    @Min(value = 1, message = "Height must be at least 1 cm")
    private Integer height;

    @NotNull(message = "Depth is required")
    @Min(value = 1, message = "Depth must be at least 1 cm")
    private Integer depth;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;
}
