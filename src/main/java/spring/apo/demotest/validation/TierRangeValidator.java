package spring.apo.demotest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import spring.apo.demotest.dto.request.TierConfigCreateRequest;

public class TierRangeValidator implements ConstraintValidator<ValidTierRange, TierConfigCreateRequest> {

    @Override
    public boolean isValid(TierConfigCreateRequest request, ConstraintValidatorContext context) {
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
