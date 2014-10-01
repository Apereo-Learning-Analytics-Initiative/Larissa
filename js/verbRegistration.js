// [authority, verb, registration, stored]

// use javascriptcompressor.com before pasting in design doc
// don't use double quotes for strings due to the code being inserted in a JSON value.

var map = function(doc) {

	if (doc.type === 'VOIDED') {
		return;
	}

	var auth = doc.authority;
	var verb = doc.verb.id;
	var registration = doc.context ? doc.context.registration : null;
	var stored = doc.stored;

	emit([ auth, null, null, stored ]);
	emit([ 'ALL', null, null, stored ]);
	emit([ auth, verb, null, stored ]);
	emit([ 'ALL', verb, null, stored ]);
	if (registration) {
		emit([ auth, null, registration, stored ]);
		emit([ 'ALL', null, registration, stored ]);
		emit([ auth, verb, registration, stored ]);
		emit([ 'ALL', verb, registration, stored ]);
	}

	for (i in doc.referrers) {
		var ref = doc.referrers[i];
		var refdoc = {
			_id : ref.id
		};
		var refAuth = ref.authority;
		var refVerb = ref.verb.id;
		var refRegistration = ref.context ? ref.context.registration : null;
		var refStored = ref.stored;

		if (refVerb !== verb) {
			emit([ refAuth, verb, null, refStored ], refdoc);
			emit([ 'ALL', verb, null, refStored ], refdoc);
			if (refRegistration) {
				emit([ refAuth, verb, refRegistration, refStored ], refdoc);
				emit([ 'ALL', verb, refRegistration, refStored ], refdoc);
			}
		}
		if (registration && refRegistration !== registration) {
			emit([ refAuth, null, registration, refStored ], refdoc);
			emit([ 'ALL', null, registration, refStored ], refdoc);
			emit([ refAuth, refVerb, registration, refStored ], refdoc);
			emit([ 'ALL', refVerb, registration, refStored ], refdoc);
			if (refVerb !== verb) {
				emit([ refAuth, verb, registration, refStored ], refdoc);
				emit([ 'ALL', verb, registration, refStored ], refdoc);
			}
		}

	}
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
	}, {
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
