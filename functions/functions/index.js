const functions = require("firebase-functions");
var admin = require("firebase-admin");


	if (!admin.apps.length) {
		admin.initializeApp();
	}else {
		admin.app();
	}
exports.addMessage = functions.https.onCall((data, context) => {
	const text = data.text;
	const uid = context.auth.uid;
	return "Message received";
});

exports.updateUserLocationTrigger = functions.firestore
	.document('locations/{locations}')
	.onWrite((snapshot, change) => {
		const data = snapshot.after.data();
		const name = data.lat;

		console.log("Data for lat: " + name);

		

	return;
});

// method that is going to be attempting to add a friend
//
exports.friendExists = functions.https.onCall((data, context) => {
	// do something
	var admin = require("firebase-admin");
	if (!admin.apps.length) {
		admin.initializeApp();
	}else {
		admin.app();
	}

	var db = admin.firestore();
	//db.settings({host:"127.0.0.1:8080",ssl:false});

	const text = data.text;
	console.log(text);

//	var ref = db.collection("friends").doc(text);

	return db.collection("friends").doc(text).get().then((doc) => {
		if (doc.exists) {
			console.log("success");
			return "user found";
		}else {
			console.log("fail");
			return "user not found";
		}
	});

});


exports.getFriends = functions.https.onCall((data, context) => {
	var admin = require("firebase-admin");
	console.log("getting friends")
	;if (!admin.apps.length) {
		admin.initializeApp();
	}else {
		admin.app();
	}

	// email of the current user
	const username = data.text;
	console.log("email: ", username);
	var db = admin.firestore();

        return db.collection("friends").doc(username).get().then((doc) => {
		if (doc.exists) {
			console.log("Data:", doc.data());
			return doc.data();
		}else {
			console.log("fail");
			return "user not found";
		}
	});

});

exports.sendFriendRequest = functions.https.onCall((data, context) => {
	var admin = require("firebase-admin");
	if(!admin.apps.length){
		admin.initializeApp();
	}else {
		admin.app();
	}

	const useremail = data.email;
	const pendingemail = data.friend;

	console.log("user email: ", useremail)
	console.log("pending email ", pendingemail)
	var db = admin.firestore();

	const ref = db.collection("friends").doc(pendingemail);

	const add = ref.update({
		pending: admin.firestore.FieldValue.arrayUnion(useremail)
	});
});

exports.rejectFriendRequest = functions.https.onCall((data, context) => {
	var admin = require("firebase-admin");
	if(!admin.apps.length){
		admin.initializeApp();
	}else {
		admin.app();
	}

	const useremail = data.userEmail;
	const pendingemail = data.pendingEmail;

	console.log("user email ", useremail);
	console.log("pending email", pendingemail);
	var db = admin.firestore();

	const ref = db.collection("friends").doc(useremail);

	const remove = ref.update({
		pending: admin.firestore.FieldValue.arrayRemove(pendingemail)
	});

});

function acceptFriendRequest(){};


exports.acceptFriendRequest = functions.https.onCall((data, context) => {
	var admin = require("firebase-admin");

	if(!admin.apps.length){
		admin.initializeApp();
	}else {
		admin.app();
	}

	const userEmail = data.userEmail;
	const pendingEmail = data.pendingEmail;

	var db = admin.firestore();

	const userRef = db.collection("friends").doc(userEmail);
	const friendRef = db.collection("friends").doc(pendingEmail);

	const userAccept = userRef.update({
		friends: admin.firestore.FieldValue.arrayUnion(pendingEmail)
	});

	const friendAccept = friendRef.update({
		friends: admin.firestore.FieldValue.arrayUnion(userEmail),
		pending: admin.firestore.FieldValue.arrayRemove(userEmail)

	});



});
exports.stopTrackingRider = functions.https.onCall((data, context) => {
	var admin = require("firebase-admin");

	if(!admin.length){
		admin.initializeApp();
	}else{
		admin.app();
	}

	var db = admin.firestore();
	const user = data.email;
	const locationRef = db.collection("locations").doc(user);

	console.log("attempting to remove users last location");
	const removeData = locationRef.update({
		lat: admin.firestore.FieldValue.arrayUnion("0"),
		longtitude: admin.firestore.FieldValue.arrayUnion("0")
	});

});



exports.updateUserLocation = functions.https.onCall((data, context) => {


	const user = data.user;
	const lat = data.lat; 
	const longtitude = data.longtitude;
 
	var db = admin.firestore();
	
	const locationRef = db.collection("locations").doc(user);
	
	console.log("User LONG: " + longtitude);
	console.log("User LAT: " + lat);

	const sendData = locationRef.update({
		lat: admin.firestore.FieldValue.arrayUnion(lat),
		longtitude: admin.firestore.FieldValue.arrayUnion(longtitude)
	})

	return;
});



exports.populateDB = functions.https.onCall((data, context) => {
	var admin = require("firebase-admin");

	if(!admin.apps.length){
		admin.initializeApp();

	} else{
		admin.app();
	}

	// method that is going to be populating the friends db FRIENDS db
	var db = admin.firestore();
	const testfriend = db.collection("friends").doc("test@test.com");
	const jackfriend = db.collection("friends").doc("jackpincombe@hotmail.com"); 
	const court = db.collection("friends").doc("court@gmail.com");
	const abdaa = db.collection("friends").doc("a@b.com");

	const data1 = {friends:["added@test.com"],pending:["pending@pending.com"]};
	const data2 = {friends:[],pending:[]};
	const data3 = {friends:[],pending:[]};

	const da = testfriend.set(data1);
	const ddd = jackfriend.set(data2);
	const sfdafga = court.set(data3);
	const friendsf = abdaa.set(data3);

	// where we are going to be populating where we are adding the coords LOCATIONS DB
	const locationRef = db.collection("locations").doc("test@test.com");
	const f1 = db.collection("locations").doc("jackpincombe@hotmail.com");
	const f2 = db.collection("locations").doc("emailtotest@test.com");
	const f3 = db.collection("locations").doc("a@b.com");

	const a = {
		longtitude: "0",
		lat: "0"
	};

	const ab = {
		longtitude: "0",
		lat: "0"
	};


	const abc = {
		longtitude: "0",
		lat: "0"
	};

	const asdfasdf = locationRef.set(a);
	const testtest = f1.set(ab);
	const rewq = f2.set(abc);
	const bcad = f3.set(abc);
});


exports.triggermethod1 = functions.firestore.document('/locations/').onWrite(test => {
	console.log("HITTING THE TRIGGER METHOD");
});


