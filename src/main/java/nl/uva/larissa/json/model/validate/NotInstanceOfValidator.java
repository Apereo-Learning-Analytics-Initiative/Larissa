package nl.uva.larissa.json.model.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotInstanceOfValidator implements
		ConstraintValidator<NotInstanceOf, Object> {

	NotInstanceOf annotation;

	@Override
	public void initialize(NotInstanceOf constraintAnnotation) {
		annotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		return object == null || !annotation.value().isInstance(object);
	}
}
