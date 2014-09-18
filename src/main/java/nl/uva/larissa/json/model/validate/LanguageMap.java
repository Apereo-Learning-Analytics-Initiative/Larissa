package nl.uva.larissa.json.model.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { LanguageMapValidator.class })
public @interface LanguageMap {
	String message() default "language map contains invalid key(s)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
