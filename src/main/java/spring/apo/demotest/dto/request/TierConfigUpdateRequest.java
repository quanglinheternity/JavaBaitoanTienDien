package spring.apo.demotest.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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
public class TierConfigUpdateRequest {

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
