package com.android.gudana.chat.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.MoD.MoD_Live_Location_receiver_Activity;
import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.activities.UserProfileActivity;
import com.android.gudana.chat.utilities.Utility;
import com.android.gudana.hify.ui.activities.notification.ImagePreviewSave;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class MessageAdapter extends BaseAdapter {
    private static final String TAG = "adapters/MessageAdapter";

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_BROADCAST = 1;

    private static final int MSG_MENU_COPY_TEXT = 0;
    private static final int MSG_MENU_VIEW_DETAILS = 1;
    private static final int MSG_MENU_VIEW_PROFILE = 2;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    private final String username;
    private Pattern pattern_url ;
    private final Context context;
    private final ArrayList<Object> mArrayList = new ArrayList<>();
    private LayoutInflater inflater;
    private ViewGroup parent_local;
    private View convertView_local;

    LinearLayout.LayoutParams detailsLinearLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );

    private int type = TYPE_MESSAGE;

    public MessageAdapter(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public int addItem(final Object item) {
        mArrayList.add(item);
        notifyDataSetChanged();
        return mArrayList.size() - 1;
    }

    public int moveItemToEndOfList(int index) {
        mArrayList.add(mArrayList.remove(index));
        return mArrayList.size() - 1;
    }

    public void addItems(final ArrayList<Object> items) {
        mArrayList.addAll(items);
        notifyDataSetChanged();
    }

    // TODO: fix for broadcastItem
    public int getFirstID() {
        if(getCount() == 0) return -1;
        return ((MessageItem) mArrayList.get(0)).getID();
    }

    // TODO: fix for broadcastItem
    public int getLastID() {
        if(getCount() == 0) return -1;
        return ((MessageItem) mArrayList.get(getCount() - 1)).getID();
    }

    public void prependItems(final ArrayList<Object> items) {
        if(getCount() > 0) {
            mArrayList.addAll(0, items);
        } else {
            addItems(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(mArrayList.get(position) instanceof MessageItem) {
            type = TYPE_MESSAGE;
        } else if(mArrayList.get(position) instanceof BroadcastItem) {
            type = TYPE_BROADCAST;
        }

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater= LayoutInflater.from(context);
        pattern_url = Pattern.compile(URL_REGEX);
        parent_local = parent;
        convertView_local = convertView;

        switch (getItemViewType(position)) {
            case TYPE_BROADCAST:
                BroadcastViewHolder broadcastViewHolder;
                if(convertView == null) {
                    convertView = inflater.inflate(R.layout.chat_listview_broadcast, parent, false);

                    broadcastViewHolder = new BroadcastViewHolder();
                    broadcastViewHolder.broadcastMsg = (TextView) convertView.findViewById(R.id.broadcast_msg);

                    convertView.setTag(broadcastViewHolder);
                } else {
                    broadcastViewHolder = (BroadcastViewHolder) convertView.getTag();
                }

                BroadcastItem broadcastItem = (BroadcastItem) getItem(position);
                broadcastViewHolder.broadcastMsg.setText(broadcastItem.getMessage());
                break;
            case TYPE_MESSAGE:
                // split the message and swithch ..
                convertView = TextMessageLayout(position);

                break;
            default:
                break;
        }

        return convertView;
    }

    public static class MessageItem {
        private int id;
        private final int user_id;
        private final String username;
        private final String message;
        private String datetime_utc;
        private boolean on_server;

        public MessageItem(int id, int user_id, String username, String message, String datetime_utc) {
            this.on_server = true;
            this.id = id;
            this.user_id = user_id;
            this.username = username;
            this.message = message;
            this.datetime_utc = datetime_utc;
        }

        public MessageItem(int user_id, String username, String message) {
            this.on_server = false;
            this.user_id = user_id;
            this.username = username;
            this.message = message;
        }

        public int getID() {
            return id;
        }

        public int getUserID() {
            return user_id;
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }

        public String getDateTimeUTC() {
            return datetime_utc;
        }

        public void savedToServer(int id, String datetime_utc) {
            this.on_server = true;
            this.id = id;
            this.datetime_utc = datetime_utc;
        }

        @Override
        public String toString() {
            return username + ": " + message;
        }
    }

    public static class BroadcastItem {
        private final String message;

        public BroadcastItem(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class MessageViewHolder {
        // image layout
        public LinearLayout layout_image;
        public ImageView ImageContainer;

        // map layout
        public  LinearLayout layout_map;

        // Live Location Linear layou
        public LinearLayout layout_live_Location;
        public Button Stop_Sharing;

        // doc layout
        public LinearLayout layout_doc;

        // voice message
        public  LinearLayout layout_voice_chat;
        public ImageView ButtonPlayStop;

        // message layout
        public LinearLayout message;
        public TextView detailsText;
        public TextView messageText;
        public Drawable bg;

        // Images message ...
        public ImageView Images_message;
    }

    public static class BroadcastViewHolder {
        public TextView broadcastMsg;
    }

    // Images  messages Layout

    public View TextMessageLayout(int position ){
        try{

            final MessageItem msg_item = (MessageItem) getItem(position);
            // chehck type of message   ..


            final MessageViewHolder messageViewHolder;
            if(convertView_local == null) {
                convertView_local = inflater.inflate(R.layout.chat_listview_messages, parent_local, false);

                messageViewHolder = new MessageViewHolder();
                messageViewHolder.detailsText = (TextView) convertView_local.findViewById(R.id.details_display);
                messageViewHolder.messageText = (TextView) convertView_local.findViewById(R.id.message_display);
                messageViewHolder.message = (LinearLayout) convertView_local.findViewById(R.id.message);
                messageViewHolder.bg = messageViewHolder.messageText.getBackground();

                // imageviey w  layout
                messageViewHolder.layout_image = (LinearLayout) convertView_local.findViewById(R.id.layout_image);
                messageViewHolder.ImageContainer = (ImageView) convertView_local.findViewById(R.id.image_message);

                // Layout map
                messageViewHolder.layout_map = (LinearLayout) convertView_local.findViewById(R.id.layout_map);

                // layout doc
                messageViewHolder.layout_doc = (LinearLayout) convertView_local.findViewById(R.id.layout_doc);

                // Voice message
                messageViewHolder.layout_voice_chat = (LinearLayout) convertView_local.findViewById(R.id.layout_voice_chat);
                messageViewHolder.ButtonPlayStop =  (ImageView) convertView_local.findViewById(R.id.ButtonPlayStop);

                // live location
                messageViewHolder.layout_live_Location = (LinearLayout) convertView_local.findViewById(R.id.layout_live_loc);
                messageViewHolder.Stop_Sharing = (Button) convertView_local.findViewById(R.id.stop_sharing);



                convertView_local.setTag(messageViewHolder);
            } else {
                messageViewHolder = (MessageViewHolder) convertView_local.getTag();
            }

            final String[] type_of_message = msg_item.getMessage().split(ChatActivity.splitter_pattern_message);
            if(type_of_message !=null && type_of_message[0] != null && type_of_message.length >1 ){

                if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_Text)){
                    messageViewHolder.layout_image.setVisibility(View.GONE);
                    messageViewHolder.layout_doc.setVisibility(View.GONE);
                    messageViewHolder.layout_map.setVisibility(View.GONE);
                    messageViewHolder.layout_voice_chat.setVisibility(View.GONE);
                    messageViewHolder.layout_live_Location.setVisibility(View.GONE);

                }else  if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_image)){
                    messageViewHolder.layout_image.setVisibility(View.VISIBLE);
                    messageViewHolder.layout_map.setVisibility(View.GONE);
                    messageViewHolder.layout_doc.setVisibility(View.GONE);
                    messageViewHolder.layout_voice_chat.setVisibility(View.GONE);
                    messageViewHolder.layout_live_Location.setVisibility(View.GONE);


                    messageViewHolder.layout_image.setOnClickListener(new View.OnClickListener()
                                                               {
                                                                   @Override
                                                                   public void onClick(View view)
                                                                   {

                                                                       ArrayList<String> ImagesList = new ArrayList<>();
                                                                       ImagesList.add(type_of_message[1]);

                                                                       Intent intent=new Intent(context,ImagePreviewSave.class)
                                                                               .putExtra("uri","")
                                                                               .putExtra("sender_name","Gudana_Image_User")
                                                                               .putExtra("url",type_of_message[1])                                                                               .putStringArrayListExtra("Images",ImagesList)
                                                                               .putStringArrayListExtra("Images",ImagesList);


                                                                       context.startActivity(intent);

                                                                   }
                                                               });

                    Picasso.with(context)
                            .load(type_of_message[1]) // not from  firebase .... direct local because i have already the uri of imag
                            .fit()
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(messageViewHolder.ImageContainer, new Callback()
                            {
                                @Override
                                public void onSuccess()
                                {
                                    //messageLoadingRight.setVisibility(View.GONE);
                                    //progressView.setIndeterminate(false);
                                    //progressView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError()
                                {
                                    Picasso.with(context)
                                            .load(type_of_message[1])
                                            .fit()
                                            .into(messageViewHolder.ImageContainer, new Callback()
                                            {
                                                @Override
                                                public void onSuccess()
                                                {
                                                    //messageLoadingRight.setVisibility(View.GONE);
                                                    //progressView.setIndeterminate(false);
                                                    //progressView.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError()
                                                {
                                                    messageViewHolder.messageText.setText("Error: could not load picture.");
                                                }
                                            });
                                }
                            });


                }else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_voice)){
                    messageViewHolder.layout_voice_chat.setVisibility(View.VISIBLE);

                    messageViewHolder.layout_map.setVisibility(View.GONE);
                    messageViewHolder.layout_doc.setVisibility(View.GONE);
                    messageViewHolder.layout_image.setVisibility(View.GONE);
                    messageViewHolder.layout_live_Location.setVisibility(View.GONE);
                    messageViewHolder.layout_voice_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatActivity.showDiag_voice(context, type_of_message[1]);

                        }
                    });

                    messageViewHolder.ButtonPlayStop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatActivity.showDiag_voice(context, type_of_message[1]);
                        }
                    });

                }else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_map)){
                    messageViewHolder.layout_map.setVisibility(View.VISIBLE);
                    messageViewHolder.layout_doc.setVisibility(View.GONE);
                    messageViewHolder.layout_image.setVisibility(View.GONE);
                    messageViewHolder.layout_voice_chat.setVisibility(View.GONE);
                    messageViewHolder.layout_live_Location.setVisibility(View.GONE);

                    messageViewHolder.layout_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatActivity.showDiag_gps_menu(context , ChatActivity.Startposition_Custom_dialog  ,type_of_message[1].toString());

                        }
                    });

                }else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_live_location)){

                    messageViewHolder.layout_live_Location.setVisibility(View.VISIBLE);
                    messageViewHolder.layout_map.setVisibility(View.GONE);
                    messageViewHolder.layout_doc.setVisibility(View.GONE);
                    messageViewHolder.layout_image.setVisibility(View.GONE);
                    messageViewHolder.layout_voice_chat.setVisibility(View.GONE);


                    messageViewHolder.Stop_Sharing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toasty.warning(context, "Stop Live Location ist not implemented  yet  ...", Toast.LENGTH_SHORT).show();

                        }
                    });

                    messageViewHolder.layout_live_Location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent LiveLocationReceiver = new Intent(context , MoD_Live_Location_receiver_Activity.class);
                            LiveLocationReceiver.putExtra("data",type_of_message[1]);
                            context.startActivity(LiveLocationReceiver);
                            // ChatActivity.showDiag_gps_menu(context , ChatActivity.Startposition_Custom_dialog  ,type_of_message[1].toString());
                        }
                    });

                }
                else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_Doc)){

                    messageViewHolder.layout_doc.setVisibility(View.VISIBLE);
                    messageViewHolder.layout_map.setVisibility(View.GONE);
                    messageViewHolder.layout_image.setVisibility(View.GONE);
                    messageViewHolder.layout_voice_chat.setVisibility(View.GONE);
                    messageViewHolder.layout_live_Location.setVisibility(View.GONE);

                    messageViewHolder.layout_doc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String filename = "";
                            try {
                                ChatActivity.download_and_open_document(context, type_of_message[1].toString(), filename);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }

            }


            // need to reset listener (expensive but necessary to fetch correct user ID for user profile)
            messageViewHolder.message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder messageChoice = new AlertDialog.Builder(context);

                    messageChoice
                            .setTitle("Message options")
                            .setItems(R.array.message_dialog_choice_list, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case MSG_MENU_COPY_TEXT:
                                            ClipboardManager clipMan = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clip = ClipData.newPlainText("chat message", messageViewHolder.messageText.getText().toString());
                                            clipMan.setPrimaryClip(clip);

                                            Toast.makeText(context, "Message copied to clipboard.", Toast.LENGTH_LONG).show();
                                            break;
                                        case MSG_MENU_VIEW_DETAILS:
                                            AlertDialog.Builder detailsDialog = new AlertDialog.Builder(context);
                                            StringBuilder messageStr = new StringBuilder();

                                            messageStr.append("Type: Message");
                                            messageStr.append("\n");
                                            messageStr.append("From: ");
                                            messageStr.append(msg_item.getUsername());
                                            messageStr.append("\n");
                                            messageStr.append("Sent: ");
                                            if(msg_item.on_server) {
                                                messageStr.append(
                                                        new SimpleDateFormat("d MMMM yyyy h:mm a")
                                                                .format(Utility.parseDateAsUTC(msg_item.getDateTimeUTC()))
                                                                .replace("AM", "am")
                                                                .replace("PM", "pm")
                                                );
                                            } else {
                                                messageStr.append("Sending...");
                                            }

                                            detailsDialog
                                                    .setTitle("Message details")
                                                    .setMessage(messageStr.toString())
                                                    .show();
                                            break;
                                        case MSG_MENU_VIEW_PROFILE:
                                            Intent intent = new Intent(context, UserProfileActivity.class);
                                            intent.putExtra("user_id", msg_item.user_id);
                                            context.startActivity(intent);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });

                    messageChoice.show();
                    return true;
                }
            });


            try{
                String[] separated = msg_item.getMessage().split(ChatActivity.splitter_pattern_message);
                if(separated[0].equalsIgnoreCase(ChatActivity.Type_Text) && separated != null && separated.length > 1 &&   separated[1] != null){
                    messageViewHolder.messageText.setText(separated[1].toString());
                }else{
                    // messageViewHolder.messageText.setText("invalid message ");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }



            //messageViewHolder.messageText.setText(msg_item.getMessage());
            // add all links if message contains them
            Linkify.addLinks(messageViewHolder.messageText, Linkify.ALL);
            Matcher m = pattern_url.matcher(msg_item.getMessage().toString());//replace with string to compare
            if(m.find()) {

                int color = ContextCompat.getColor(context, R.color.blue);
                messageViewHolder.messageText.setTextColor(color);
            }

            if(username.equals(msg_item.getUsername())) {
                messageViewHolder.message.setGravity(Gravity.END);
                detailsLinearLayoutParams.setMargins(0, 0, 32, 32);
                messageViewHolder.detailsText.setLayoutParams(detailsLinearLayoutParams);
                if (messageViewHolder.bg instanceof ShapeDrawable) {
                    ((ShapeDrawable) messageViewHolder.bg).getPaint().setColor(Color.parseColor("#731CFF"));
                    messageViewHolder.detailsText.setTextColor(context.getResources().getColor( R.color.black));
                    messageViewHolder.messageText.setTextColor(context.getResources().getColor( R.color.white));
                } else if (messageViewHolder.bg instanceof GradientDrawable) {
                    ((GradientDrawable) messageViewHolder.bg).setColor(Color.parseColor("#731CFF"));
                    messageViewHolder.detailsText.setTextColor(context.getResources().getColor( R.color.black));
                    messageViewHolder.messageText.setTextColor(context.getResources().getColor( R.color.white));
                }
            } else {
                messageViewHolder.message.setGravity(Gravity.START);
                detailsLinearLayoutParams.setMargins(32, 0, 0, 32);
                messageViewHolder.detailsText.setLayoutParams(detailsLinearLayoutParams);  // initial color "#FFDDDDDD"
                if (messageViewHolder.bg instanceof ShapeDrawable) {
                    ((ShapeDrawable) messageViewHolder.bg).getPaint().setColor(Color.parseColor("#FFFFFF"));

                    messageViewHolder.detailsText.setTextColor(context.getResources().getColor( R.color.black));
                    messageViewHolder.messageText.setTextColor(context.getResources().getColor( R.color.black));
                } else if (messageViewHolder.bg instanceof GradientDrawable) {
                    ((GradientDrawable) messageViewHolder.bg).setColor(Color.parseColor("#FFFFFF"));
                    messageViewHolder.detailsText.setTextColor(context.getResources().getColor( R.color.black));
                    messageViewHolder.messageText.setTextColor(context.getResources().getColor( R.color.black));
                }
            }

            StringBuilder details = new StringBuilder();

            if(!username.equals(msg_item.getUsername())) {
                details.append(msg_item.getUsername());
                details.append(" - ");
            }

            if(msg_item.on_server) {
                details.append(Utility.getAbbreviatedDateTime(Utility.parseDateAsUTC(msg_item.getDateTimeUTC())));
            }

            messageViewHolder.detailsText.setText(details.toString());

            messageViewHolder.detailsText.setVisibility(View.VISIBLE);

            if(!msg_item.on_server) {
                messageViewHolder.detailsText.setText("Sending");
            }
            else if(position + 1 < mArrayList.size()) {
                Object next_msg_item = getItem(position + 1);
                if (next_msg_item instanceof MessageItem) {
                    if (msg_item.getUsername().equals(((MessageItem) next_msg_item).getUsername())) {
                        messageViewHolder.detailsText.setVisibility(View.GONE);
                    }
                }
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }

        return convertView_local;
    }



}
