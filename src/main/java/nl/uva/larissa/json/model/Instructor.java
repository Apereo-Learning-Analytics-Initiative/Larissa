package nl.uva.larissa.json.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "objectType", defaultImpl = Agent.class)
@JsonSubTypes({ 
		@Type(value = Agent.class, name = "Agent"),
		@Type(value = Group.class, name = "Group")
})
public interface Instructor {

}
