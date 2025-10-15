package spring.apo.demotest.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import spring.apo.demotest.validation.ValidTierRange;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidTierRange(message = "TIER_CONFIG_MAX_VALUE_INVALID")
public class TierConfigCreateRequest {

    @NotBlank(message = "TIER_CONFIG_NAME_NOT_FOUND") // tên không được để trống
    @Size(min = 1, max = 50, message = "TIER_CONFIG_NAME_INVALID_SIZE") // giới hạn độ dài
    private String tierName;

    @NotNull(message = "TIER_CONFIG_MIN_VALUE_REQUIRED")
    @Min(value = 0, message = "TIER_CONFIG_MIN_VALUE_INVALID")
    private Integer minValue;

    @NotNull(message = "TIER_CONFIG_MAX_VALUE_REQUIRED")
    @Min(value = 1, message = "TIER_CONFIG_MAX_VALUE_INVALID")
    private Integer maxValue;

    @NotNull(message = "TIER_CONFIG_PRICE_REQUIRED")
    @DecimalMin(value = "0.0", inclusive = false, message = "TIER_CONFIG_PRICE_INVALID")
    private BigDecimal price;
}
