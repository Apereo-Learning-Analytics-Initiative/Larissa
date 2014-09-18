package nl.uva.larissa.json.model.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.uva.larissa.json.model.IFI;

public class ValidIFIValidator implements ConstraintValidator<ValidIFI, IFI> {

	@Override
	public void initialize(ValidIFI constraintAnnotation) {
	}

	@Override
	public boolean isValid(IFI ifi, ConstraintValidatorContext context) {
		int ifiCount = 0;
		if (ifi.getAccount() != null) {
			++ifiCount;
		}
		if (ifi.getMbox() != null) {
			++ifiCount;
		}
		if (ifi.getMbox_sha1sum() != null) {
			++ifiCount;
		}
		if (ifi.getOpenID() != null) {
			++ifiCount;
		}
		return ifiCount == 1;
	}

}
