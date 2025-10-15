package spring.apo.demotest.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = {TierRangeValidator.class, TierConfigUpdateValidator.class}) // validator sẽ implement logic
@Target({ElementType.TYPE}) // dùng ở class (DTO)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTierRange {
    String message() default "TIER_CONFIG_MAX_VALUE_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
