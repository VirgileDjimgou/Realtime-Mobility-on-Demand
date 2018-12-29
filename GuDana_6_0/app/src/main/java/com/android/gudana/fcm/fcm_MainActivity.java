package com.android.gudana.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.android.gudana.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class fcm_MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int currentNotificationID = 0;
    private EditText etMainNotificationText, etMainNotificationTitle;
    private Button btnMainSendSimpleNotification, btnMainSendExpandLayoutNotification, btnMainSendNotificationActionBtn, btnMainSendMaxPriorityNotification, btnMainSendMinPriorityNotification, btnMainSendCombinedNotification, btnMainClearAllNotification,btnMainCustomNotification;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String notificationTitle;
    private String notificationText;
    private Bitmap icon;
    private int combinedNotificationCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fcm_activity_main);

        etMainNotificationText = (EditText) findViewById(R.id.etMainNotificationText);
        etMainNotificationTitle = (EditText) findViewById(R.id.etMainNotificationTitle);

        btnMainSendSimpleNotification = (Button) findViewById(R.id.btnMainSendSimpleNotification);
        btnMainSendExpandLayoutNotification = (Button) findViewById(R.id.btnMainSendExpandLayoutNotification);
        btnMainSendNotificationActionBtn = (Button) findViewById(R.id.btnMainSendNotificationActionBtn);
        btnMainSendMaxPriorityNotification = (Button) findViewById(R.id.btnMainSendMaxPriorityNotification);
        btnMainSendMinPriorityNotification = (Button) findViewById(R.id.btnMainSendMinPriorityNotification);
        btnMainSendCombinedNotification = (Button) findViewById(R.id.btnMainSendCombinedNotification);
        btnMainClearAllNotification = (Button) findViewById(R.id.btnMainClearAllNotification);
        btnMainCustomNotification= (Button) findViewById(R.id.btnMainCustomNotification);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher);


        btnMainSendSimpleNotification.setOnClickListener(this);
        btnMainSendExpandLayoutNotification.setOnClickListener(this);
        btnMainSendNotificationActionBtn.setOnClickListener(this);
        btnMainSendMaxPriorityNotification.setOnClickListener(this);
        btnMainSendMinPriorityNotification.setOnClickListener(this);
        btnMainSendCombinedNotification.setOnClickListener(this);
        btnMainClearAllNotification.setOnClickListener(this);
        btnMainCustomNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setNotificationData();
        switch (v.getId()) {
            case R.id.btnMainSendSimpleNotification:
                setDataForSimpleNotification();
                break;
            case R.id.btnMainSendExpandLayoutNotification:
                setDataForExpandLayoutNotification();
                break;
            case R.id.btnMainSendNotificationActionBtn:
                setDataForNotificationWithActionButton();
                break;
            case R.id.btnMainSendMaxPriorityNotification:
                setDataForMaxPriorityNotification();
                break;
            case R.id.btnMainSendMinPriorityNotification:
                setDataForMinPriorityNotification();
                break;
            case R.id.btnMainSendCombinedNotification:
                setDataForCombinedNotification();
                break;
            case R.id.btnMainClearAllNotification:
                clearAllNotifications();
            case R.id.btnMainCustomNotification:
                customNotifications();
                break;
        }
    }

    private void setNotificationData() {
        notificationTitle = this.getString(R.string.app_name);
        notificationText = "Lorem Ipsum, dizgi ve baskı endüstrisinde kullanılan mıgır metinlerdir. Lorem Ipsum, adı bilinmeyen bir matbaacının bir hurufat numune kitabı oluşturmak üzere bir yazı galerisini alarak karıştırdığı 1500'lerden beri endüstri standardı sahte metinler olarak kullanılmıştır. Beşyüz yıl boyunca varlığını sürdürmekle kalmamış, aynı zamanda pek değişmeden elektronik dizgiye de sıçramıştır. 1960'larda Lorem Ipsum pasajları da içeren Letraset yapraklarının yayınlanması ile ve yakın zamanda Aldus PageMaker gibi Lorem Ipsum sürümleri içeren masaüstü yayıncılık yazılımları ile popüler olmuştur.";
        if (!etMainNotificationText.getText().toString().equals("")) {
            notificationText = etMainNotificationText.getText().toString();
        }
        if (!etMainNotificationTitle.getText().toString().equals("")) {
            notificationTitle = etMainNotificationTitle.getText().toString();
        }
    }

    private void sendNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, fcm_MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(contentIntent);
        notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        currentNotificationID++;
        int notificationId = currentNotificationID;
        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;


//        if (notificationId >= 4)
//            notificationId = 4;


        notificationManager.notify(notificationId, notification);
       // notificationManager.cancel(currentNotificationID-1);
    }

    private void setDataForSimpleNotification() {
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText);

        sendNotification(notificationBuilder.build());
    }

    private void setDataForExpandLayoutNotification() {
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setContentText(notificationText);

        sendNotification(notificationBuilder.build());
    }

    private void setDataForNotificationWithActionButton() {

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setContentText(notificationText);

        Intent answerIntent = new Intent(this, AnswerReceiveActivity.class);
        answerIntent.setAction("Yes");
        PendingIntent pendingIntentYes = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_up, "Yes", pendingIntentYes);

        answerIntent.setAction("No");
        PendingIntent pendingIntentNo = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo);

/*        answerIntent.setAction("No1");
        PendingIntent pendingIntentNo1 = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo1);

        answerIntent.setAction("No2");
        PendingIntent pendingIntentNo2 = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo2);

        answerIntent.setAction("No3");
        PendingIntent pendingIntentNo3 = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo3);*/

        sendNotification(notificationBuilder.build());
    }

    private void setDataForMaxPriorityNotification() {
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(notificationText);

        sendNotification(notificationBuilder.build());
    }

    private void setDataForMinPriorityNotification() {
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setPriority(Notification.PRIORITY_MIN)
                .setContentText(notificationText);

        sendNotification(notificationBuilder.build());
    }

    private void setDataForCombinedNotification() {
        combinedNotificationCounter++;
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setGroup("group_emails")
                .setGroupSummary(true)
                .setContentText(combinedNotificationCounter + " new messages");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(notificationTitle);
        inboxStyle.setSummaryText("mehulrughani@gmail.com");
        for (int i = 0; i < combinedNotificationCounter; i++) {
            inboxStyle.addLine("This is Test" + i);
        }
        currentNotificationID = 500;
        notificationBuilder.setStyle(inboxStyle);
        sendNotification(notificationBuilder.build());
    }

    private void clearAllNotifications() {
        if (notificationManager != null) {
            currentNotificationID = 0;
            notificationManager.cancelAll();
        }
    }

    private void customNotifications() {

        try{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            Intent intnt = new Intent(getApplicationContext(), fcm_MainActivity.class);
            intnt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Random rr=new Random(1000);

            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), rr.nextInt(1000), intnt,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(intent);
            builder.setTicker(getApplicationContext().getResources().getString(R.string.notification_title));
            builder.setSmallIcon(R.drawable.emptyperson);
            builder.setAutoCancel(true);
            Notification notification = builder.build();
            RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.fcm_notification_fcm);
            final String time = DateFormat.getTimeInstance().format(new Date()).toString();
            final String text = getApplicationContext().getResources().getString(R.string.notificaton_text);
            contentView.setTextViewText(R.id.textView, text);

            notification.contentView = contentView;

                RemoteViews expandedView =
                        new RemoteViews(getApplicationContext().getPackageName(),R.layout.fcm_notification_expanded);
                notification.bigContentView = expandedView;

                try{

                    expandedView.setTextViewText(R.id.tv_alarm_kisi_name,"temp");
                    expandedView.setTextViewText(R.id.tv_alarm_kisi_numara,"numara");


                    expandedView.setTextViewText(R.id.tv_alarm_kisi_sifat,"sifat");

                    expandedView.setTextViewText(R.id.tv_alarm_kacgunsaatkaldi,"kaç gün kaldı");

                    try{

                            expandedView.setImageViewResource(R.id.alarm_profile_image,R.drawable.emptyperson);


                    } catch (Exception e) {

                    }

                    expandedView.setImageViewResource(R.id.iv_alarm_turu,R.drawable.ozel_digergunler);

                    expandedView.setTextViewText(R.id.textViewIsim,"ad aoyad");


                } catch (Exception e) {
                    e.toString();
                }






            NotificationManager nm = (NotificationManager)getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            nm.notify(0, notification);


        }catch(Exception exp){

        }
    }

}
