package com.android.gudana.hify.utils;

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

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://35.243.225.95/AndroidFileUpload/fileUpload.php";

    public static final String IMAGES_UPLOAD_URL = "http://35.243.225.95/AndroidFileUpload/Images_fileUpload.php";

    public static final String VIDEOS_UPLOAD_URL = "http://35.243.225.95/AndroidFileUpload/Videos_fileUpload.php";


    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
}
