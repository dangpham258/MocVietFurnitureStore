package mocviet.dto.customer;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.regex.Pattern;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumber.PhoneNumberValidator.class)
@Documented
public @interface PhoneNumber {
    String message() default "Số điện thoại chỉ được chứa số";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
        private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]+$");

        @Override
        public void initialize(PhoneNumber constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.trim().isEmpty()) {
                return true; // Let @NotNull handle null/empty values
            }
            
            return PHONE_PATTERN.matcher(value.trim()).matches();
        }
    }
}
