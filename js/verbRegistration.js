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
		if (ref.verb !== verb) {
			emit([ ref.auth, verb, null, ref.stored ], refdoc);
			emit([ 'ALL', verb, null, ref.stored ], refdoc);
			if (ref.registration) {
				emit([ ref.auth, verb, ref.registration, ref.stored ], refdoc);
				emit([ 'ALL', verb, ref.registration, ref.stored ], refdoc);
			}
		}
		if (registration && ref.registration !== registration) {
			emit([ ref.auth, null, registration, ref.stored ], refdoc);
			emit([ 'ALL', null, registration, ref.stored ], refdoc);
			emit([ ref.auth, ref.verb, registration, ref.stored ], refdoc);
			emit([ 'ALL', ref.verb, registration, ref.stored ], refdoc);
			if (ref.verb !== verb) {
				emit([ ref.auth, verb, registration, ref.stored ], refdoc);
				emit([ 'ALL', verb, registration, ref.stored ], refdoc);
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
		"auth" : {
			"objectType" : "Agent",
			"mbox" : "Y@example.com"
		},
		"verb" : "Yverb",
		"registration" : "Yreg",
		"stored" : "2013-04-23T15:23:19.154Z"
	}, {
		"id" : "X",
		"auth" : {
			"objectType" : "Agent",
			"mbox" : "X@example.com"
		},
		"verb" : "Zverb",
		"stored" : "2012-04-23T15:23:19.154Z"
	} ],
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
