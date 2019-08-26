'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


	//FOR NOTIFICATION = "New Menu"
	exports.sendNotification = functions.database.ref('/Meals/{pushID}/').onCreate((snapmenu, context) => {
		 
		const mealData = snapmenu.val();
		let food_stall = mealData.Foodstall_Name;
		const payLoad = {
        notification:{
            title: 'LFSA',
            body: '' + food_stall +' '+'has added a new menu. Check it out!',
            sound: "default"
        },
		data: {
            chosenFoodStall: food_stall, 
			status: 'meals'
        }
		
		
    };
    return admin.messaging().sendToTopic("users", payLoad);

			});
	
	//FOR NOTIFICATION = "BULK ORDER"
	exports.sendNotificationBulkOrder = functions.database.ref('/BulkOrder/{pushID}/').onUpdate((change, context) => {
		
		
		const bulkOrder = change.after.val();
		
		let food_stall = bulkOrder.Foodstall_Name;
		let token_id = bulkOrder.Token_ID;
		let order_status = bulkOrder.Order_Status;
		console.log('NAME: ', fuck);
		console.log('STATUS: ', order_status);
		const payLoad = {
        notification:{
            title: 'LFSA',
            body: 'BULK ORDER STATUS: ' + order_status ,
            sound: "default"
        },
		data: {
            chosenFoodStall : food_stall,
			status : 'forOrder'
        }
		
		
    };
    return admin.messaging().sendToDevice(token_id, payLoad);

	});
	
	//FOR NOTIFICATION = Order Table (accepted, declined, food is being cooked, ready for pick-up)
	exports.sendNotificationOrders = functions.database.ref('/Orders/{pushID}/').onUpdate((change, context) => {
		
		
		const bulkOrder = change.after.val();
			
		let food_stall = bulkOrder.Foodstall_Name;
		let token_id = bulkOrder.Token_ID;
		let order_status = bulkOrder.Order_Status;
		console.log('STATUS: ', order_status);
		const payLoad = {
        notification:{
            title: 'LFSA',
            body: 'ORDER STATUS: ' + order_status ,
            sound: "default"
        },
		data: {
            chosenFoodStall : food_stall,
			status : order_status
        }
		
		
    };
    return admin.messaging().sendToDevice(token_id, payLoad);

	});

	
