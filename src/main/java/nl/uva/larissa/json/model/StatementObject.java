package nl.uva.larissa.json.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "objectType", defaultImpl = Activity.class)
@JsonSubTypes({ 
		@Type(value = Activity.class, name = "Activity"),
		@Type(value = Agent.class, name = "Agent"),
		@Type(value = Group.class, name = "Group"),
		@Type(value = StatementRef.class, name = "StatementRef"),
		@Type(value = SubStatement.class, name = "SubStatement") })
public interface StatementObject {

}