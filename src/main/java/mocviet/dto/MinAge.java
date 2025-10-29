package mocviet.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.Period;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinAge.MinAgeValidator.class)
@Documented
public @interface MinAge {
    String message() default "Tuổi phải từ 15 tuổi trở lên";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value() default 15;

    class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {
        private int minAge;

        @Override
        public void initialize(MinAge constraintAnnotation) {
            this.minAge = constraintAnnotation.value();
        }

        @Override
        public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // Let @NotNull handle null values
            }
            
            LocalDate today = LocalDate.now();
            int age = Period.between(value, today).getYears();
            return age >= minAge;
        }
    }
}
