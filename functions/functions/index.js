const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
//
exports.addMessage = functions.https.onCall((data, context) => {
	const text = data.text;
	const uid = context.auth.uid;
	return "Message received";
});

// method that is going to be attempting to add a friend
//
exports.addFriend = functions.https.onCall((data, context) => {
	// do something
	var admin = require("firebase-admin");
	admin.initializeApp();
	var db = admin.firestore();

	const text = data.text;
	console.log(text);

	var ref = db.collection('Friends').doc(text);
	const doc = ref.get();

	if(!doc.exists){
		return "user not found";
	} else {
		return "user found"
	}
});
