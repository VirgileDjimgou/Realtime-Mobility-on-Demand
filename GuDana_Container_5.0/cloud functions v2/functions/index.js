'use strict'

/**
 * @version 1.1
 * @since   27/02/2018
 */
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

console.log('Init App .... ');

exports.sendNotification = functions.database.ref('/Notifications/{user_id}/{notification_id}').onWrite((change, context) => {
    const user_id = context.params.user_id;
    const notification_id = context.params.notification_id;
	
	console.log('I am a log entry!');
	
	console.log('Testing stuff', context.params.user_id);

    if(!change.after.val())
    {
        return console.log('A Notification has been deleted from the database : ', notification_id);
    }else{
		console.log('data val   ist null  ');

	}
	
			console.log('after date val !');
		

    const fromUser = admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');
		console.log('log Etape 2!');

    return fromUser.then(fromUserResult => 
    {
	    console.log('from User  ...... 3 !');
        const from_user_id = fromUserResult.val().from;
        const type = fromUserResult.val().type;
    
        const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
        const deviceToken = admin.database().ref(`/Users/${user_id}/token`).once('value');

        return Promise.all([userQuery, deviceToken]).then(result =>
        {
            const userName = result[0].val();
            const token_id = result[1].val();
            
            if(type == "request")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "You have a Friend Request",
                        body: `${userName} wants to be your friend!`,
                        icon: "default",
                        click_action : "com.android.gudana.chatapp.chatapp_PROFILE_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${from_user_id}) sent friend request to ${user_id}`);
                });
            }
            else if(type == "message")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "You have a new Message",
                        body: `${userName} messaged you!`,
                        icon: "default",
                        click_action : "com.android.gudana.chatapp.chatapp_CHAT_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${from_user_id}) send a message to ${user_id}`);
                });
            }
			else if(type == "call")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "you have a call",
                        body: `${userName} call you!`,
                        icon: "default",
                        click_action : "com.android.gudana.chatapp.chatapp_CHAT_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${from_user_id}) send a message to ${user_id}`);
                });
            }
            else if(type == "accept")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "You have a new friend",
                        body: `${userName} accepted your request!`,
                        icon: "default",
                        click_action : "com.android.gudana.chatapp.chatapp_PROFILE_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${user_id}) accepted request by ${from_user_id}`);
                });
            }
        });
    });
});