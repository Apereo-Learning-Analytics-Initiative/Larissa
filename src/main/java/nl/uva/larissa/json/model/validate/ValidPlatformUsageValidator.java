package nl.uva.larissa.json.model.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.uva.larissa.json.model.Activity;
import nl.uva.larissa.json.model.Context;
import nl.uva.larissa.json.model.Statement;

public class ValidPlatformUsageValidator implements
		ConstraintValidator<ValidPlatformUsage, Statement> {

	@Override
	public void initialize(ValidPlatformUsage constraintAnnotation) {
	}

	@Override
	public boolean isValid(Statement statement,
			ConstraintValidatorContext context) {
		Context statementContext = statement.getContext();
		if (statementContext == null) {
			return true;
		}
		return statementContext.getPlatform() == null
				|| statement.getStatementObject() instanceof Activity;
	}
}
