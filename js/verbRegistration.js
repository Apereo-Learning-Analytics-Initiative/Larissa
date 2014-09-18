// [authority, verb, registration, stored]

// use javascriptcompressor.com before pasting in design doc
// don't use double quotes for strings due to the code being inserted in a JSON value.

var map = function(doc) {

	function doEmit(verb, registration) {
		emit([ doc.auth, verb, registration, doc.stored ]);
		emit([ 'ALL', verb, registration, doc.stored ]);
	}
	if (doc.type === 'VOIDED') {
		return;
	}
	doEmit(null, null);
	doEmit(doc.verb.id, null);

	if (doc.context && doc.context.registration) {
		doEmit(null, doc.context.registration);
		doEmit(doc.verb.id, doc.context.registration);
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
	"authority" : {
		"objectType" : "Agent",
		"mbox" : "bla3"
	}
};
map(doc);
allRows.sort();
console.log(allRows);