package com.android.gudana.chatapp.holders;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.chatapp.activities.FullScreenActivity;
import com.android.gudana.chatapp.utils.FileOpen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

public class MessageHolder extends RecyclerView.ViewHolder
{
    private final String TAG = "CA/MessageHolder";

    private View view;
    private Context context;

    // Will handle User, Chat and Chat Typing data

    private DatabaseReference userDatabase, chatSeenDatabase, chatTypingDatabase;
    private ValueEventListener userListener, chatSeenListener, chatTypingListener;

    public MessageHolder(View view, Context context)
    {
        super(view);

        this.view = view;
        this.context = context;
    }

    public void hideBottom()
    {
        final RelativeLayout messageBottom = view.findViewById(R.id.message_relative_bottom);

        messageBottom.setVisibility(View.GONE);
    }

    public void setLastMessage(final String currentUserId, final String from, final String to)
    {
        // If the ca_message is the last ca_message in the list

        final TextView messageSeen = view.findViewById(R.id.message_seen);
        final TextView messageTyping = view.findViewById(R.id.message_typing);

        final RelativeLayout messageBottom = view.findViewById(R.id.message_relative_bottom);

        messageBottom.setVisibility(View.VISIBLE);

        String otherUserId = from;

        if(from.equals(currentUserId))
        {
            otherUserId = to;

            if(chatSeenDatabase != null && chatSeenListener != null)
            {
                chatSeenDatabase.removeEventListener(chatSeenListener);
            }

            // Initialize/Update seen ca_message on the bottom of the ca_message

            chatSeenDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(to).child(currentUserId);
            chatSeenListener = new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    try
                    {
                        if(from.equals(currentUserId) && dataSnapshot.hasChild("seen"))
                        {
                            messageSeen.setVisibility(View.VISIBLE);

                            long seen = (long) dataSnapshot.child("seen").getValue();

                            if(seen == 0)
                            {
                                messageSeen.setText("Sent");
                            }
                            else
                            {
                                messageSeen.setText("Seen at " + new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(seen));
                            }
                        }
                        else
                        {
                            messageSeen.setVisibility(View.INVISIBLE);
                        }
                    }
                    catch(Exception e)
                    {
                        Log.d(TAG, "chatSeenListerner exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Log.d(TAG, "chatSeenListerner failed: " + databaseError.getMessage());
                }
            };
            chatSeenDatabase.addValueEventListener(chatSeenListener);
        }
        else
        {
            messageSeen.setVisibility(View.INVISIBLE);
        }

        if(chatTypingDatabase != null && chatTypingListener != null)
        {
            chatTypingDatabase.removeEventListener(chatTypingListener);
        }

        // Initialize/Update typing status on the bottom

        chatTypingDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(otherUserId).child(currentUserId);
        chatTypingListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    if(dataSnapshot.hasChild("typing"))
                    {
                        int typing = Integer.parseInt(dataSnapshot.child("typing").getValue().toString());

                        messageTyping.setVisibility(View.VISIBLE);

                        if(typing == 1)
                        {
                            messageTyping.setText("Typing...");
                        }
                        else if(typing == 2)
                        {
                            messageTyping.setText("Deleting...");
                        }
                        else if(typing == 3)
                        {
                            messageTyping.setText("Thinking...");
                        }
                        else
                        {
                            messageTyping.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        messageTyping.setVisibility(View.INVISIBLE);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "chatTypingListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "chatTypingListener failed: " + databaseError.getMessage());
            }
        };
        chatTypingDatabase.addValueEventListener(chatTypingListener);
    }

    public void setRightMessage(String userid, final String message, final String filename , long time, String type )
    {
        // If this an upcoming message

        final RelativeLayout messageLayoutLeft = view.findViewById(R.id.message_relative_left);

        final RelativeLayout messageLayoutRight = view.findViewById(R.id.message_relative_right);
        final TextView messageTextRight = view.findViewById(R.id.message_text_right);
        final TextView messageTimeRight = view.findViewById(R.id.message_time_right);
        final ProgressBar progressView = view.findViewById(R.id.progress_load);
        final CircleImageView messageImageRight = view.findViewById(R.id.message_image_right);
        final ImageView messageTextPictureRight = view.findViewById(R.id.message_imagetext_right);
        final TextView messageLoadingRight = view.findViewById(R.id.message_loading_right);

        BoomMenuButton bmb_right= (BoomMenuButton) view.findViewById(R.id.bmb_right);
        RelativeLayout bmb_right_see = (RelativeLayout) view.findViewById(R.id.bmb_right_mask);
        bmb_right_see.setVisibility(view.GONE);
        assert bmb_right != null;
        bmb_right.setButtonEnum(ButtonEnum.TextOutsideCircle);
        bmb_right.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_2);
        bmb_right.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_2);
        bmb_right.setBoomEnum(BoomEnum.values()[4]); // random  boom
        bmb_right.setUse3DTransformAnimation(true);
        bmb_right.setDuration(500);

        try{

            // clear all builders
            bmb_right.clearBuilders();



            TextOutsideCircleButton.Builder option_1 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_map_image)
                    .normalText("turn-by-turn navigation : Drive to this point ")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            //Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                            Open_navi(message);
                        }
                    });

            bmb_right.addBuilder(option_1);



            // first
            TextOutsideCircleButton.Builder option_2 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_map_icon)
                    .normalText("See this point on a map")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            //Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                            Open_map(message);

                        }
                    });

            bmb_right.addBuilder(option_2);



            // first
            TextOutsideCircleButton.Builder option_3 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.street_view_360)
                    .normalText("open  Street View 360  around this point")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Open_Street_View(message);
                            //Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb_right.addBuilder(option_3);



            // first
            TextOutsideCircleButton.Builder option_4 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_doc_round)
                    .normalText("option_4")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                           //  Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb_right.addBuilder(option_4);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        messageLayoutLeft.setVisibility(View.GONE);

        messageLayoutRight.setVisibility(View.VISIBLE);

        if(type.equals("text"))
        {
            messageTextPictureRight.setVisibility(View.GONE);
            messageLoadingRight.setVisibility(View.GONE);
            messageTextRight.setVisibility(View.VISIBLE);
            messageTextRight.setText(message);
            progressView.setIndeterminate(false);
            progressView.setVisibility(View.GONE);
        }

        if(type.equals("voice"))
        {
            messageTextPictureRight.setVisibility(View.VISIBLE);
            messageTextPictureRight.setImageResource(R.mipmap.ic_mic_round);
            messageLoadingRight.setVisibility(View.GONE);
            messageTextRight.setVisibility(View.VISIBLE);
            messageTextRight.setText("Voice message");
            progressView.setIndeterminate(false);
            progressView.setVisibility(View.GONE);

            // add listeners
            messageTextPictureRight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(message);
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            playSound(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
            });

        }



        if(type.equals("doc"))
        {
            messageTextPictureRight.setVisibility(View.VISIBLE);
            messageTextPictureRight.setImageResource(R.mipmap.ic_doc_round);
            messageLoadingRight.setVisibility(View.GONE);
            messageTextRight.setVisibility(View.VISIBLE);
            try{

                    messageTextRight.setText(filename);

            }catch(Exception ex){
                ex.printStackTrace();
                messageTextRight.setText("invalid file !");
            }

            progressView.setIndeterminate(false);
            progressView.setVisibility(View.GONE);
            // add listeners
            messageTextPictureRight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    getMimeType(message);
                    Uri myUri = Uri.parse(message);
                    try {
                        download_and_open_document(message, filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

        }


        if(type.equals("location"))
        {
            bmb_right_see.setVisibility(view.VISIBLE);
            messageTextPictureRight.setVisibility(View.VISIBLE);
            messageTextPictureRight.setImageResource(R.drawable.ic_map_image);
            messageLoadingRight.setVisibility(View.GONE);
            messageTextRight.setVisibility(View.VISIBLE);
            messageTextRight.setText("location : click to open");
            progressView.setIndeterminate(false);
            progressView.setVisibility(View.GONE);

            // add listeners
            messageTextPictureRight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                       // open_location();

                }
            });

        }


        if(type.equals("image")) {
            try{


                // show a picture holder  ....
                messageTextRight.setVisibility(View.GONE);
                messageTextPictureRight.setVisibility(View.VISIBLE);
                messageLoadingRight.setVisibility(View.VISIBLE);
                messageLoadingRight.setText("Loading picture...");

                Log.d("debug message loading" , "images loading");


                Picasso.with(context)
                        .load(message) // not from  firebase .... direct local because i have already the uri of imag
                        .fit()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(messageTextPictureRight, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {
                                messageLoadingRight.setVisibility(View.GONE);
                                progressView.setIndeterminate(false);
                                progressView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError()
                            {
                                Picasso.with(context)
                                        .load(message)
                                        .fit()
                                        .into(messageTextPictureRight, new Callback()
                                        {
                                            @Override
                                            public void onSuccess()
                                            {
                                                messageLoadingRight.setVisibility(View.GONE);
                                                progressView.setIndeterminate(false);
                                                progressView.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError()
                                            {
                                                messageLoadingRight.setText("Error: could not load picture.");
                                            }
                                        });
                            }
                        });

                messageTextPictureRight.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(context, FullScreenActivity.class);
                        intent.putExtra("imageUrl", message);
                        context.startActivity(intent);
                    }
                });

            }catch(Exception ex){

                ex.printStackTrace();
            }
        }

        messageTimeRight.setText(DateUtils.isToday(time) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(time));

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update ca_user image

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot)
            {
                try
                {
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if(!image.equals("default"))
                    {
                        Picasso.with(context)
                                .load(image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user)
                                .into(messageImageRight, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {

                                    }

                                    @Override
                                    public void onError()
                                    {
                                        Picasso.with(context)
                                                .load(image)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .into(messageImageRight);
                                    }
                                });
                    }
                    else
                    {
                        messageImageRight.setImageResource(R.drawable.user);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
            }
        };


        userDatabase.addValueEventListener(userListener);
    }


    public void Open_map(String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]);
        System.out.println(LatLong[1]);


        // Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Uri gmmIntentUri = Uri.parse("geo:"+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }


    }


    private void Open_navi(String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]); // latiudute
        System.out.println(LatLong[1]); // longitude

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }

    }

    private void Open_Street_View(String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]);
        System.out.println(LatLong[1]);

        // Uses a PanoID to show an image from Maroubra beach in Sydney, Australia
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1])+"&cbp=0,30,0,0,-15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }



    }


    private void playSound(Uri uri){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(uri.toString());
        }catch(Exception e){

        }
        mediaPlayer.prepareAsync();
        //You can show progress dialog here untill it prepared to play
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //Now dismis progress dialog, Media palyer will start playing
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // dissmiss progress bar here. It will come here when MediaPlayer
                //  is not able to play file. You can show error message to user
                return false;
            }
        });
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    private void download_and_open_document(String url , String filename) throws IOException {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        //StorageReference  islandRef = storageRef.child("file.txt");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Gudana_directory");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,filename);
        ChatActivity.data_processing.setVisibility(View.VISIBLE);

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());

                Uri myUri = Uri.parse(localFile.toString());
                try {
                    //FileOpen.openFile(context,localFile);
                    /*
                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    // String mimeType = myMime.getMimeTypeFromExtension("pdf" );
                    newIntent.setDataAndType(myUri,getMimeType(localFile.getPath()));
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newIntent);
                    */
                    ChatActivity.data_processing.setVisibility(View.GONE);
                    FileOpen.openFile(context,localFile);
                } catch (Exception e) {
                    ChatActivity.data_processing.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });



        /*
        String filePath = Environment.getExternalStorageDirectory() + "/picture.jpg";
        Uri myUri = Uri.parse(filePath);

        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension("jpg");
        newIntent.setDataAndType(myUri,mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(newIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

        */

    }



    //// set left Images  .......

    public void setLeftMessage(String userid, final String message, final String filename , long time, String type)
    {
        // If this is a sent message

        final RelativeLayout messageLayoutRight = view.findViewById(R.id.message_relative_right);

        final RelativeLayout messageLayoutLeft = view.findViewById(R.id.message_relative_left);
        final TextView messageTextLeft = view.findViewById(R.id.message_text_left);
        final TextView messageTimeLeft = view.findViewById(R.id.message_time_left);
        final CircleImageView messageImageLeft = view.findViewById(R.id.message_image_left);
        final ImageView messageTextPictureLeft = view.findViewById(R.id.message_imagetext_left);
        final TextView messageLoadingLeft = view.findViewById(R.id.message_loading_left);

        BoomMenuButton bmb_left= (BoomMenuButton) view.findViewById(R.id.bmb_left);
        RelativeLayout bmb_left_mask = (RelativeLayout) view.findViewById(R.id.bmb_left_mask);
        bmb_left_mask.setVisibility(View.GONE);

        assert bmb_left != null;
        bmb_left.setButtonEnum(ButtonEnum.TextOutsideCircle);
        bmb_left.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_2);
        bmb_left.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_2);
        bmb_left.setBoomEnum(BoomEnum.values()[5]); // random  boom
        bmb_left.setUse3DTransformAnimation(true);
        bmb_left.setUse3DTransformAnimation(true);
        bmb_left.setDuration(500);

        try{
            bmb_left.clearBuilders();
            // first
            TextOutsideCircleButton.Builder option_1 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_map_image)
                    .normalText("turn-by-turn navigation : Drive to this point ")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Open_navi(message);
                            // Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb_left.addBuilder(option_1);


            // first
            TextOutsideCircleButton.Builder option_2 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_map_icon)
                    .normalText("see this point on a map ")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Open_map(message);
                            //Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb_left.addBuilder(option_2);



            // first
            TextOutsideCircleButton.Builder option_3 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.street_view_360)
                    .normalText("open  Street View 360  around this point")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Open_Street_View(message);
                            //Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb_left.addBuilder(option_3);



            // first
            TextOutsideCircleButton.Builder option_4 = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_doc_round)
                    .normalText("option_4")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                           //  Toast.makeText(context, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb_left.addBuilder(option_4);

        }catch(Exception ex){
            ex.printStackTrace();
        }



        messageLayoutRight.setVisibility(View.GONE);
        messageLayoutLeft.setVisibility(View.VISIBLE);

        if(type.equals("text"))
        {
            messageTextPictureLeft.setVisibility(View.GONE);
            messageLoadingLeft.setVisibility(View.GONE);
            messageTextLeft.setVisibility(View.VISIBLE);
            messageTextLeft.setText(message);
        }

        if(type.equals("voice"))
        {
            messageTextPictureLeft.setVisibility(View.VISIBLE);
            messageTextPictureLeft.setImageResource(R.mipmap.ic_mic_round);
            messageLoadingLeft.setVisibility(View.GONE);
            messageTextLeft.setVisibility(View.VISIBLE);
            messageTextLeft.setText("Voice : click to listen");

            // add listeners
            messageTextPictureLeft.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(message);
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            playSound(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
            });

        }


        if(type.equals("location"))
        {
            messageTextPictureLeft.setVisibility(View.VISIBLE);
            messageTextPictureLeft.setImageResource(R.drawable.ic_map_image);
            messageLoadingLeft.setVisibility(View.GONE);
            messageTextLeft.setVisibility(View.VISIBLE);
            messageTextLeft.setText("Location: click to open");
            bmb_left_mask.setVisibility(View.VISIBLE);


            // add listeners
            messageTextPictureLeft.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    // open_location();

                }
            });

        }


        if(type.equals("doc"))
        {

            messageTextPictureLeft.setVisibility(View.VISIBLE);
            messageTextPictureLeft.setImageResource(R.mipmap.ic_doc_round);
            messageLoadingLeft.setVisibility(View.GONE);
            messageTextLeft.setVisibility(View.VISIBLE);


            try{

                   messageTextLeft.setText(filename);


            }catch(Exception ex){
                ex.printStackTrace();
                messageTextLeft.setText("invalid file");

            }
            // add listeners
            messageTextPictureLeft.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Uri myUri = Uri.parse(message);

                    try {
                        download_and_open_document(message , filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getMimeType(message);


                }
            });

        }



        if(type.equals("image")){
            messageTextLeft.setVisibility(View.GONE);

            messageTextPictureLeft.setVisibility(View.VISIBLE);
            messageLoadingLeft.setVisibility(View.VISIBLE);
            messageLoadingLeft.setText("Loading picture...");

            Picasso.with(context)
                    .load(message)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(messageTextPictureLeft, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            messageLoadingLeft.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(context)
                                    .load(message)
                                    .fit()
                                    .into(messageTextPictureLeft, new Callback()
                                    {
                                        @Override
                                        public void onSuccess()
                                        {
                                            messageLoadingLeft.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError()
                                        {
                                            messageLoadingLeft.setText("Error: could not load picture.");
                                        }
                                    });
                        }
                    });

            messageTextPictureLeft.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, FullScreenActivity.class);
                    intent.putExtra("imageUrl", message);
                    context.startActivity(intent);
                }
            });
        }
        messageTimeLeft.setText(DateUtils.isToday(time) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(time));

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initilize/Update ca_user image

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot)
            {
                try
                {
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if(!image.equals("default"))
                    {
                        Picasso.with(context)
                                .load(image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user)
                                .into(messageImageLeft, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {

                                    }

                                    @Override
                                    public void onError()
                                    {
                                        Picasso.with(context)
                                                .load(image)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .into(messageImageLeft);
                                    }
                                });
                    }
                    else
                    {
                        messageImageLeft.setImageResource(R.drawable.user);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
}
