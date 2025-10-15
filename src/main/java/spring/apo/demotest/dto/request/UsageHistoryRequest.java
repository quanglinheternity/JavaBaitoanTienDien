package spring.apo.demotest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageHistoryRequest {

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "DATE_INVALID_FORMAT")
    private String date; // định dạng "yyyy-MM-dd"

    @NotNull(message = "KWH_REQUIRED")
    @Positive(message = "KWH_MUST_BE_GREATER_THAN_ZERO")
    private Integer kwh;

    private String userID;
}
