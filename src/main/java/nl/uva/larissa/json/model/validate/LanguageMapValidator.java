package nl.uva.larissa.json.model.validate;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;

public class LanguageMapValidator implements
		ConstraintValidator<LanguageMap, Map<String, String>> {

	@Override
	public void initialize(LanguageMap constraintAnnotation) {
	}

	@Override
	public boolean isValid(Map<String, String> map,
			ConstraintValidatorContext context) {
		if (map == null) {
			return true;
		}
		try { 
			for (String key : map.keySet()) {
				LangTag.parse(key);
			}
		} catch (LangTagException e) {
			return false;
		}
		return true;
	}
}
