package nl.uva.larissa.json.model.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;

public class LanguageMapKeyValidator implements
		ConstraintValidator<LanguageMapKey, String> {

	@Override
	public void initialize(LanguageMapKey constraintAnnotation) {
	}

	@Override
	public boolean isValid(String string, ConstraintValidatorContext context) {
		if (string == null) {
			return true;
		}
		try {
			LangTag.parse(string);
		} catch (LangTagException e) {
			return false;
		}
		return true;
	}

}
