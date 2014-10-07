// [authority, verb, registration, stored]

// before putting in statement_design.json:
// apply javascriptcompressor.com
// NB don't use double quotes for strings due to the code being inserted in a JSON value.

var map = function(doc) {
	if (doc.type === 'VOIDED') {
		return;
	}

	// var diff = require('views/lib/sets').diff;
	// var sum = require('views/lib/sets').sum;

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

	var auth = doc.authority;
	var verb = doc.verb.id;
	var registration = doc.context ? doc.context.registration : null;
	var stored = doc.stored;

	var auths = [ 'ALL', auth ];

	for (var i = 0; i < auths.length; i++) {
		emit([ auths[i], null, null, stored ]);
		emit([ auths[i], verb, null, stored ]);
		if (registration) {
			emit([ auths[i], null, registration, stored ]);
			emit([ auths[i], verb, registration, stored ]);
		}
	}

	function emitReferrers(doc, rootVerb, rootRegistration, parentChainVerbs,
			parentChainRegistrations) {
		if (!doc.referrers) {
			return;
		}
		for (var i = 0; i < doc.referrers.length; i++) {
			var ref = doc.referrers[i];
			var refAuths = [ 'ALL', ref.authority ];
			var refVerb = ref.verb.id;
			var refRegistration = ref.context ? ref.context.registration : null;
			var refStored = ref.stored;

			var referrerId = {
				_id : ref.id
			};

			parentChainVerbs = sum(parentChainVerbs, [ refVerb ]);
			if (refRegistration) {
				parentChainRegistrations = sum(parentChainRegistrations,
						[ refRegistration ]);
			}

			var ittRegistrations = rootRegistration ? sum(
					parentChainRegistrations, [ rootRegistration ])
					: parentChainRegistrations;
			if (refVerb !== rootVerb) {
				for (var j = 0; j < refAuths.length; j++) {
					for (var k = 0; k < ittRegistrations.length; k++) {
						emit([ refAuths[j], rootVerb, ittRegistrations[k],
								refStored ], referrerId);
					}
					emit([ refAuths[j], rootVerb, null, refStored ], referrerId);
				}
			}
			var ittVerbs = diff(parentChainVerbs, [ rootVerb ]);
			if (rootRegistration && refRegistration !== rootRegistration) {
				for (var j = 0; j < refAuths.length; j++) {
					for (var k = 0; k < ittVerbs.length; k++) {
						emit([ refAuths[j], ittVerbs[k], rootRegistration,
								refStored ], referrerId)
					}
					emit([ refAuths[j], null, rootRegistration, refStored ],
							referrerId);
				}
			}
			emitReferrers(ref, rootVerb, rootRegistration, parentChainVerbs,
					parentChainRegistrations);
		}

	}
	emitReferrers(doc, verb, registration, [], []);
};

var allRows = [];

function emit(array, value) {
	allRows[allRows.length] = JSON.stringify(array) + ','
			+ (value ? JSON.stringify(value) : 'null');
}

var doc = {
	"type" : "PLAIN",
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
	"authority" : {
		"objectType" : "Agent",
		"mbox" : "bla3"
	}
};
map(doc);
var expected = 4;
console.log("test one "
		+ (allRows.length === expected ? "was succesful" : "failed; expected "
				+ expected + " rows but was " + allRows.length + "\n"));
if (expected !== allRows.length) {
	console.log(allRows);
}
var doc2 = {
	"referrers" : [ {
		"referrers" : [ {
			"id" : "X",
			"authority" : {
				"objectType" : "Agent",
				"mbox" : "X@example.com"
			},
			"verb" : {
				"id" : "Zverb"
			},
			"stored" : "2012-04-23T15:23:19.154Z"
		} ],
		"id" : "Y",
		"authority" : {
			"objectType" : "Agent",
			"mbox" : "Y@example.com"
		},
		"verb" : {
			"id" : "Yverb"
		},
		"context" : {
			"registration" : "Yreg"
		},
		"stored" : "2013-04-23T15:23:19.154Z"
	} ],
	"type" : "PLAIN",
	"id" : "Z",
	"stored" : "2014-04-23T15:23:19.154Z",
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
		"registration" : "Zreg"
	},
	"authority" : {
		"objectType" : "Agent",
		"mbox" : "Z@example.com"
	}
};

allRows = [];
map(doc2);
expected = 22;
console.log("test two "
		+ (allRows.length === expected ? "was succesful" : "failed; expected "
				+ expected + " rows but was " + allRows.length + "\n"));
if (expected !== allRows.length) {
	allRows.sort();
	console.log(allRows);
}
