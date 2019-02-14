package com.android.gudana.chat.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.model.Model_message_chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Model_message_chat> dataSet;
    Context mContext;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;
    private  ChatActivity ParentActivity;
    private  String username;


    LinearLayout.LayoutParams detailsLinearLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        CardView cardView;

        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

    public int getCount() {
        return dataSet.size();
    }

    public Object getItem(int position) {
        return dataSet.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    public void addItems(final ArrayList<Model_message_chat> items) {
        dataSet.addAll(items);
        notifyDataSetChanged();
    }


    public int addItem(final Model_message_chat item) {
        dataSet.add(item);
        notifyDataSetChanged();
        return dataSet.size() - 1;
    }

    public int addItem_and_uploadtask(final ChatActivity ParentActivity, final Model_message_chat item) {

        this.ParentActivity = ParentActivity;

        dataSet.add(item);
        notifyDataSetChanged();
        try{

            Thread.sleep(10);

            // new UploadFileToServer_chat(file, Config.FILE_UPLOAD_URL,"Doc",processin).execute();

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return dataSet.size() - 1;

    }



    public int getViewTypeCount() {
        return 2;
    }


    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.image = (ImageView) itemView.findViewById(R.id.background);

        }

    }

    public static class AudioTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView fab_image;

        public AudioTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.fab_image = (ImageView) itemView.findViewById(R.id.fab_image);

        }
    }

    public MultiViewTypeAdapter(ArrayList<Model_message_chat> data, Context context) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case Model_message_chat.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_text_type, parent, false);
                return new TextTypeViewHolder(view);
            case Model_message_chat.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_type, parent, false);
                return new ImageTypeViewHolder(view);
            case Model_message_chat.AUDIO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_audio_type, parent, false);
                return new AudioTypeViewHolder(view);
        }
        return null;


    }


    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return Model_message_chat.TEXT_TYPE;
            case 1:
                return Model_message_chat.IMAGE_TYPE;
            case 2:
                return Model_message_chat.AUDIO_TYPE;
            default:
                return -1;
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        Model_message_chat object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case Model_message_chat.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).txtType.setText(object.text);

                    break;
                case Model_message_chat.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).txtType.setText(object.text);
                    ((ImageTypeViewHolder) holder).image.setImageResource(object.data);
                    break;
                case Model_message_chat.AUDIO_TYPE:

                    ((AudioTypeViewHolder) holder).txtType.setText(object.text);


                    ((AudioTypeViewHolder) holder).fab_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (fabStateVolume) {
                                if (mPlayer.isPlaying()) {
                                    mPlayer.stop();

                                }
                                ((AudioTypeViewHolder) holder).fab_image.setImageResource(R.drawable.volume);
                                fabStateVolume = false;

                            } else {
                                mPlayer = MediaPlayer.create(mContext, R.raw.hify_sound);
                                mPlayer.setLooping(true);
                                mPlayer.start();
                                ((AudioTypeViewHolder) holder).fab_image.setImageResource(R.drawable.mute);
                                fabStateVolume = true;

                            }
                        }
                    });


                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
