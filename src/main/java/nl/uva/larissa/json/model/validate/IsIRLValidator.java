package nl.uva.larissa.json.model.validate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.abdera.i18n.iri.IRI;

public class IsIRLValidator implements ConstraintValidator<IsIRL, IRI> {

	@Override
	public void initialize(IsIRL constraintAnnotation) {
	}

	@Override
	public boolean isValid(IRI iri, ConstraintValidatorContext context) {
		if (iri != null) {
			try {
				iri.toURL();
			} catch (MalformedURLException | URISyntaxException e) {
				return false;
			}
		}
		return true;
	}
}
