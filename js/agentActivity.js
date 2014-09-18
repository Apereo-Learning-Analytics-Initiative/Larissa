// [authority, relation, agent, activity, verb, registration]
// relation: 1= none related, 2= agent_related, 3= activity_related, 4= both related

// use javascriptcompressor.com before pasting in design doc
// don't use double quotes for strings due to the code being inserted in a JSON value.

var map = function(doc) {

	function addIfNotExists(string, array) {
		if (array.indexOf(string) === -1) {
			array[array.length] = string;
		}
	}

	function toIFI(agentOrGroup) {
		if (agentOrGroup.mbox) {
			return {
				mbox : agentOrGroup.mbox
			};
		}
		if (agentOrGroup.mbox_sha1sum) {
			return {
				mbox_sha1sum : agentOrGroup.mbox_sha1sum
			};
		}
		if (agentOrGroup.openID) {
			return {
				openID : agentOrGroup.openID
			};
		}
		if (agentOrGroup.account) {
			return {
				account : agentOrGroup.account
			};
		}
		throw 'no IFI for '.JSON.stringify(agentOrGroup);
	}

	function addAgentsFrom(object, array) {
		if (object.objectType === 'Agent') {
			addIfNotExists(JSON.stringify(toIFI(object)), array);
		} else if (object.objectType === 'Group') {
			for ( var i in object.member) {
				addIfNotExists(JSON.stringify(toIFI(object.member[i])), array);
			}
		}
	}

	function getAgents(doc, related) {
		var result = [];
		addAgentsFrom(doc.actor, result);
		addAgentsFrom(doc.object, result);
		if (related) {
			if (doc.authority) {
				addAgentsFrom(doc.authority, result);
			}
			if (doc.context) {
				if (doc.context.instructor) {
					addAgentsFrom(doc.context.instructor, result);
				}
				if (doc.context.team) {
					addAgentsFrom(doc.context.team, result);
				}
			}
		}
		return result;
	}

	function addActivities(activities, array) {
		for ( var i in activities) {
			addIfNotExists(activities[i].id, array);
		}
	}

	function addActivitiesFromContextActivities(contextActivities, array) {
		if (contextActivities.parent) {
			addActivities(contextActivities.parent, array);
		}
		if (contextActivities.grouping) {
			addActivities(contextActivities.grouping, array);
		}
		if (contextActivities.category) {
			addActivities(contextActivities.category, array);
		}
		if (contextActivities.other) {
			addActivities(contextActivities.other, array);
		}
	}

	function getActivities(doc, related) {
		var result = [];
		if (doc.object.objectType === 'Activity') {
			result[result.length] = doc.object.id;
		}
		if (related) {
			if (doc.context && doc.context.contextActivities) {
				addActivitiesFromContextActivities(
						doc.context.contextActivities, result);
			}
			if (doc.object.objectType === 'SubStatement') {
				var subResult = getActivities(doc.object, true);
				for ( var i in subResult) {
					addIfNotExists(subResult[i], result);
				}
			}
		}
		return result;
	}

	if (doc.type === 'VOIDED') {
		return;
	}
	
	var agents = getAgents(doc, false);
	var related_agents = getAgents(doc, true);

	var activities = getActivities(doc, false);
	var related_activities = getActivities(doc, true);

	var registration = (doc.context && doc.context.registration) ? doc.context.registration
			: null;

	var auth = doc.authority;
	var verb = doc.verb.id;
	var stored = doc.stored;

	function doEmit(level, agent, activity) {
		if (agent !== null) {
			agent = JSON.parse(agent);
		}
		emit([ auth, level, agent, activity, null, null, stored ]);
		emit([ 'ALL', level, agent, activity, null, null, stored ]);
		emit([ auth, level, agent, activity, verb, null, stored ]);
		emit([ 'ALL', level, agent, activity, verb, null, stored ]);
		if (registration) {
			emit([ auth, level, agent, activity, null, registration, stored ]);
			emit([ 'ALL', level, agent, activity, null, registration, stored ]);
			emit([ auth, level, agent, activity, verb, registration, stored ]);
			emit([ 'ALL', level, agent, activity, verb, registration, stored ]);
		}
	}

	for ( var i in agents) {
		doEmit(1, agents[i], null);
		for ( var j in activities) {
			doEmit(1, agents[i], activities[j]);
		}
		doEmit(3, agents[i], null);
		for ( var j in related_activities) {
			doEmit(3, agents[i], activities[j]);
		}
	}
	for ( var i in related_agents) {
		doEmit(2, related_agents[i], null);
		for ( var j in activities) {
			doEmit(2, related_agents[i], activities[j]);
		}
		doEmit(4, related_agents[i], null);
		for ( var j in related_activities) {
			doEmit(4, related_agents[i], related_activities[j]);
		}
	}
	for ( var i in activities) {
		doEmit(1, null, activities[i]);
		doEmit(2, null, activities[i]);
	}
	for ( var i in related_activities) {
		doEmit(3, null, related_activities[i]);
		doEmit(4, null, related_activities[i]);
	}
};

var allRows = [];

function emit(array) {
	allRows[allRows.length] = JSON.stringify(array);

}

var doc = {
	"id" : "a-5",
	"stored" : "2014-04-23T15:23:19.154Z",
	"actor" : {
		"objectType" : "Agent",
		"mbox" : "bla"
	},
	"verb" : {
		"id" : "http://uva.nl/doing"
	},
	"object" : {
		"objectType" : "Agent",
		"mbox" : "bla2"
	},
	"context" : {
		"contextActivities" : {
			"other" : [ {
				"id" : "safetydance"
			} ]
		}
	},
	"authority" : {
		"objectType" : "Agent",
		"mbox" : "bla3"
	}
};

var doc2 = {
	"_id" : "aap",
	"_rev" : "1-217c6bd6ca0083ae1c5b296809b4a5f1",
	"id" : "aap",
	"actor" : {
		"objectType" : "Agent",
		"name" : "Keith Metellus",
		"mbox" : "mailto:Keith.Metellus@exampleonly.com"
	},
	"verb" : {
		"id" : "http://adlnet.gov/expapi/verbs/asked",
		"display" : {
			"en-US" : "asked"
		}
	},
	"object" : {
		"objectType" : "Activity",
		"id" : "act:adlnet.gov/course",
		"definition" : {
			"name" : {
				"en-US" : "course"
			},
			"description" : {
				"en-US" : "course"
			},
			"type" : "type:media"
		}
	},
	"context" : {
		"registration" : "ae2128e2-2109-45f3-af23-b27f18a64c1c",
		"contextActivities" : {
			"grouping" : [ {
				"objectType" : "Activity",
				"id" : "act:adlnet.gov/cont2"
			} ]
		}
	},
	"stored" : "2014-05-26T10:28:20.308Z"
};
map(doc);
allRows.sort();
console.log(allRows);
console.log(allRows.length);
