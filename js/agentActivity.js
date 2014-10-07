// [authority, relation, agent, activity, verb, registration]
// relation: 1= none related, 2= agent_related, 3= activity_related, 4= both related

// before putting in statement_design.json:
// apply javascriptcompressor.com
// NB don't use double quotes for strings due to the code being inserted in a JSON value.

var map = function(doc) {

	if (doc.type === 'VOIDED') {
		return;
	}

	// var sum = require('views/lib/sets').sum;
	// var diff = require('views/lib/sets').diff;

	function diff(arrA, arrB) {
		var result = [];
		for (var i = 0; i < arrA.length; i++) {
			if (arrB.indexOf(arrA[i]) < 0) {
				result.push(arrA[i]);
			}
		}
		return result;
	}

	function sum(arrA, arrB) {
		var result = arrA.slice();
		for (var i = 0; i < arrB.length; i++) {
			if (result.indexOf(arrB[i]) < 0) {
				result.push(arrB[i]);
			}
		}
		return result;
	}

	function addIfNotExists(string, array) {
		if (array.indexOf(string) < 0) {
			array.push(string);
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

	var agents = getAgents(doc, false);
	var related_agents = getAgents(doc, true);

	var activities = getActivities(doc, false);
	var related_activities = getActivities(doc, true);

	var registration = doc.context ? doc.context.registration : null;

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

	function doRefEmitForLevel(referrerAuths, level, verbs, registrations,
			agentsOnlyInReferee, agentsMinusOnlyInReferee, activities,
			activitiesOnlyInReferee, referrerStored, referrerId) {
		var referrerId = {
			_id : referrerId
		};
		for (var a = 0; a < referrerAuths.length; a++) {
			for (var b = 0; b < verbs.length; b++) {
				for (var c = 0; c < registrations.length; c++) {
					for (var d = 0; d < agentsOnlyInReferee.length; d++) {
						var agentOnlyInReferee = JSON
								.parse(agentsOnlyInReferee[d]);
						for (var e = 0; e < activities.length; e++) {
							emit([ referrerAuths[a], level, agentOnlyInReferee,
									activities[e], verbs[b], registrations[c],
									referrerStored ], referrerId);

						}
						emit([ referrerAuths[a], level, agentOnlyInReferee,
								null, verbs[b], registrations[c],
								referrerStored ], referrerId);
					}
					for (var d = 0; d < activitiesOnlyInReferee.length; d++) {
						for (var e = 0; e < agentsMinusOnlyInReferee.length; e++) {
							var agentOb = JSON
									.parse(agentsMinusOnlyInReferee[e]);
							emit([ referrerAuths[a], level, agentOb,
									activitiesOnlyInReferee[d], verbs[b],
									registrations[c], referrerStored ],
									referrerId);
						}
						emit([ referrerAuths[a], level, null,
								activitiesOnlyInReferee[d], verbs[b],
								registrations[c], referrerStored ], referrerId);

					}
				}
			}
		}
	}

	// parentChain sets do not explicitly contain root sets
	function emitReferrers(doc, rootAgents, rootActivities, parentChainAgents,
			parentChainActivities, allVerbs, allRegistrations) {

		if (!doc.referrers) {
			return;
		}
		for (var i = 0; i < doc.referrers.length; i++) {
			var ref = doc.referrers[i];

			var refAgents = getAgents(ref, false);
			var refRelatedAgents = getAgents(ref, true);
			var refActivities = getActivities(ref, false);
			var refRelatedActivities = getActivities(ref, true);
			var refRegistration = ref.context ? ref.context.registration : null;

			var refAuth = ref.authority;
			var refVerb = ref.verb.id;
			var refStored = ref.stored;

			allVerbs = sum(allVerbs, [ refVerb ]);
			if (refRegistration) {
				allRegistrations = sum(allRegistrations, [ refRegistration ]);
			}

			parentChainAgents = sum(parentChainAgents, refAgents);

			var agentsOnlyInRoot = diff(rootAgents, parentChainAgents);

			var allAgents = sum(parentChainAgents, rootAgents);

			var agentsMinusOnlyInRoot = diff(allAgents, agentsOnlyInRoot);

			parentChainActivities = sum(parentChainActivities, refActivities);

			var allActivities = sum(parentChainActivities, rootActivities);

			var activitiesOnlyInRoot = diff(rootActivities,
					parentChainActivities);

			var refAuths = [ refAuth, 'ALL' ];

			var level = 1; // none related

			doRefEmitForLevel(refAuths, level, allVerbs, allRegistrations,
					agentsOnlyInRoot, agentsMinusOnlyInRoot, allActivities,
					activitiesOnlyInRoot, ref.stored, ref.id);

			// var level = 2; // agent_related
			//
			// doRefEmitForLevel(refAuths, level, allVerbs, allRegistrations,
			// relatedAgentsNotInRef, allRelatedAgentsMinusNotInRef,
			// allActivities, activitiesNotInRef, ref.stored, ref.id);
			//
			// var level = 3; // activity_related
			//
			// doRefEmitForLevel(refAuths, level, allVerbs, allRegistrations,
			// agentsNotInRef, allAgentsMinusNotInRef,
			// allRelatedActivities, relatedActivitesNotInRef, ref.stored,
			// ref.id);
			//
			// var level = 4; // agent_related & activity_related
			//
			// doRefEmitForLevel(refAuths, level, allVerbs, allRegistrations,
			// relatedAgentsNotInRef, allRelatedAgentsMinusNotInRef,
			// allRelatedActivities, relatedActivitesNotInRef, ref.stored,
			// ref.id);
			emitReferrers(ref, rootAgents, rootActivities, parentChainAgents,
					parentChainActivities, allVerbs, allRegistrations);
		}
	}
	emitReferrers(doc, agents, activities, [], [], [ verb, null ],
			(registration ? [ registration, null ] : [ null ]));
};

var allRows = [];

function emit(array, value) {
	allRows[allRows.length] = JSON.stringify(array) + ','
			+ (value ? JSON.stringify(value) : 'null');
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

map(doc);

var expected = 68;

console.log("test one "
		+ (allRows.length === expected ? "was succesful" : "failed; expected "
				+ expected + " rows but was " + allRows.length + "\n"));
if (expected !== allRows.length) {
	allRows.sort();
	console.log(allRows);
}

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

allRows = [];

map(doc2);

expected = 128;

console.log("test two "
		+ (allRows.length === expected ? "was succesful" : "failed; expected "
				+ expected + " rows but was " + allRows.length + "\n"));
if (expected !== allRows.length) {
	allRows.sort();
	console.log(allRows);
}

doc3 = {
	"referrers" : [ {
		"id" : "Y",
		"authority" : {
			"objectType" : "Agent",
			"mbox" : "Y@example.com"
		},
		"actor" : {
			"objectType" : "Agent",
			"mbox" : "Y@example.com"
		},
		"verb" : {
			"id" : "Yverb"
		},
		"object" : {
			"objectType" : "Activity",
			"id" : "Yactivity"
		},
		"context" : {
			"registration" : "Yreg"
		},
		"stored" : "2013"
	} ],
	"id" : "Z",
	"stored" : "2014",
	"actor" : {
		"objectType" : "Agent",
		"mbox" : "Zactor@example.com"
	},
	"verb" : {
		"id" : "Zverb"
	},
	"object" : {
		"objectType" : "Agent",
		"mbox" : "Zobject@example.com"
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
		"mbox" : "Zauthority"
	}
};

allRows = [];

map(doc3);

// for now... (not doing related yet)
expected = 116;

function reportDupes(sortedArray) {
	var results = [];
	for (var i = 0; i < sortedArray.length - 1; i++) {
		if (sortedArray[i + 1] == sortedArray[i]) {
			results.push(sortedArray[i]);
		}
	}
	return results;
}

var dupes = reportDupes(allRows);

if (dupes.length > 0 || allRows.length != expected) {
	if (dupes.length > 0) {
		console.log("test three failed; dupes emited:");
		console.log(dupes);
	} else {
		console.log("test three failed; expected " + expected
				+ " rows but was " + allRows.length);
		allRows.sort();
		console.log(allRows);
	}
} else {
	console.log("test three was succesful");
}

doc4 = {
	"_id" : "6ec8a804-33dd-4d94-b6bf-54e35b5b668b",
	"_rev" : "3-47aa44c7c808ab0ef9cc9503a18841e5",
	"id" : "6ec8a804-33dd-4d94-b6bf-54e35b5b668b",
	"actor" : {
		"objectType" : "Agent",
		"mbox" : "mailto:ben@uva.nl"
	},
	"verb" : {
		"id" : "passed"
	},
	"object" : {
		"objectType" : "Activity",
		"id" : "explosivestraining"
	},
	"context" : {
		"registration" : "59039ad7-e8ed-4dd2-8a07-52f015fa1e08"
	},
	"stored" : "2014-10-01T16:21:32.210Z",
	"authority" : {
		"objectType" : "Agent",
		"mbox" : "test@example.com"
	},
	"type" : "PLAIN",
	"referrers" : [ {
		"id" : "03226b5d-d03e-421d-aeed-20e5be5147d8",
		"actor" : {
			"objectType" : "Agent",
			"mbox" : "mailto:andrew@uva.nl"
		},
		"verb" : {
			"id" : "confirms"
		},
		"object" : {
			"objectType" : "StatementRef",
			"id" : "6ec8a804-33dd-4d94-b6bf-54e35b5b668b"
		},
		"stored" : "2014-10-01T16:21:32.237Z",
		"authority" : {
			"objectType" : "Agent",
			"mbox" : "test@example.com"
		}
	}, {
		"id" : "db71c502-56a3-4701-a020-37bed42cdef0",
		"actor" : {
			"objectType" : "Agent",
			"mbox" : "mailto:tom@uva.nl"
		},
		"verb" : {
			"id" : "mentioned"
		},
		"object" : {
			"objectType" : "StatementRef",
			"id" : "03226b5d-d03e-421d-aeed-20e5be5147d8"
		},
		"stored" : "2014-10-01T16:21:32.276Z",
		"authority" : {
			"objectType" : "Agent",
			"mbox" : "test@example.com"
		}
	} ]
};
allRows = [];
map(doc4);

// for now... not doing related yet
expected = 256;
dupes = reportDupes(allRows);

if (dupes.length > 0 || allRows.length != expected) {
	if (dupes.length > 0) {
		console.log("test four failed; dupes emited:");
		console.log(dupes);
	} else {
		console.log("test four failed; expected " + expected + " rows but was "
				+ allRows.length);
		allRows.sort();
		console.log(allRows);
	}
} else {
	console.log("test four was succesful");
}