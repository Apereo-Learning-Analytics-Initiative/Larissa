package nl.uva.larissa.json.model.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { ValidPlatformUsageValidator.class })
public @interface ValidPlatformUsage {
	String message() default "'platform' may only be used if object is Activity.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
