package nl.uva.larissa.json.model.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRIHelper;

public class MailtoIRIValidator implements ConstraintValidator<MailToIRI, IRI> {

	@Override
	public void initialize(MailToIRI constraintAnnotation) {
	}

	@Override
	public boolean isValid(IRI iri, ConstraintValidatorContext context) {
		return iri == null || IRIHelper.isMailtoUri(iri);
	}

}
