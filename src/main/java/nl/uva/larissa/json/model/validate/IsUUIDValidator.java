package nl.uva.larissa.json.model.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.uva.larissa.UUIDUtil;

public class IsUUIDValidator implements ConstraintValidator<IsUUID, String> {
	@Override
	public void initialize(IsUUID constraintAnnotation) {
	}

	@Override
	public boolean isValid(String string, ConstraintValidatorContext context) {
		if (string == null) {
			return true;
		}
		return UUIDUtil.isUUID(string);
	}
}
