package spring.apo.demotest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {TierRangeValidator.class, TierConfigUpdateValidator.class}) // validator sẽ implement logic
@Target({ ElementType.TYPE }) // dùng ở class (DTO)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTierRange {
    String message() default "TIER_CONFIG_MAX_VALUE_INVALID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
