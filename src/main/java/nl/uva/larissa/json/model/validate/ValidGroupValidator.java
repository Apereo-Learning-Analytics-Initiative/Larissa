package nl.uva.larissa.json.model.validate;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.uva.larissa.json.model.Agent;
import nl.uva.larissa.json.model.Group;

public class ValidGroupValidator implements
		ConstraintValidator<ValidGroup, Group> {

	@Override
	public void initialize(ValidGroup constraintAnnotation) {
	}

	@Override
	public boolean isValid(Group group, ConstraintValidatorContext context) {
		if (group.getIdentifier() == null) {
			List<Agent> members = group.getMember();
			return members != null && members.size() > 0;
		}
		// not anonymous
		return true;
	}

}
