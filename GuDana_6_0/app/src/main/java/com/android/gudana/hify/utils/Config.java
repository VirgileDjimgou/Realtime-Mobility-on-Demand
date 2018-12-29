package com.android.gudana.hify.utils;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by amsavarthan on 10/3/18.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final String ADMIN_CHANNEL_ID = "admin_id";
    public static final String KEY_REPLY = "key_reply";

    public static final String FIREBASE_AUTH_KEY = "your_firebase_auth_key";

    // File upload url (replace the ip with your server address)// 35.237.197.121
    public static final String FILE_UPLOAD_URL = "http://35.237.197.121/AndroidFileUpload/fileUpload.php";

    public static final String IMAGES_UPLOAD_URL = "http://35.237.197.121/AndroidFileUpload/Images_fileUpload.php";

    public static final String VIDEOS_UPLOAD_URL = "http://35.237.197.121/AndroidFileUpload/Videos_fileUpload.php";


    public static final String URL_CHAT_SERVER = "http://35.237.197.121:5000";

    // Directory name to store captured images and videos
    // public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    public static final String  UID_EVENT_LOCATION_LIVE_CHANNEL =  "GUDANA_LIVE_LOCATION"+FirebaseAuth.getInstance().getUid();
}
