package spring.apo.demotest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import spring.apo.demotest.dto.request.TierConfigUpdateRequest;

public class TierConfigUpdateValidator implements ConstraintValidator<ValidTierRange, TierConfigUpdateRequest> {

    @Override
    public boolean isValid(TierConfigUpdateRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;
        if (request.getMinValue() == null || request.getMaxValue() == null) return true;

        if (request.getMaxValue() <= request.getMinValue()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("TIER_CONFIG_MAX_VALUE_INVALID")
                   .addPropertyNode("maxValue")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}
    