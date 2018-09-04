package com.android.gudana.hify.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.gudana.chatapp.utils.FileOpen;
import com.android.gudana.hify.models.MultipleImage;
import com.android.gudana.hify.ui.activities.notification.ImagePreviewSave;
import com.android.gudana.hify.ui.views.HifyImageView;
import com.android.gudana.R;
import com.android.gudana.hify.utils.database.fileDownloader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import id.zelory.compressor.Compressor;

import static com.mikepenz.iconics.Iconics.TAG;


public class PostPhotosAdapter extends PagerAdapter {


    private ArrayList<MultipleImage> IMAGES;
    private boolean local;
    private LayoutInflater inflater;
    private Context context;
    private File compressedFile;
    private Activity activity;
    private boolean video_file;
    private String URL_VIDEO;
    private PostsAdapter.ViewHolder holder;

    public PostPhotosAdapter(Context context, Activity activity, ArrayList<MultipleImage> IMAGES, boolean local , boolean video_file , String url_video , final PostsAdapter.ViewHolder holder) {
        this.context = context;
        this.IMAGES =IMAGES;
        this.local=local;
        this.activity=activity;
        this.video_file= video_file;
        this.URL_VIDEO = url_video;
        this.holder = holder;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        final View imageLayout = inflater.inflate(R.layout.hi_item_viewpager_image, view, false);

        assert imageLayout!=null;
        HifyImageView imageView = imageLayout.findViewById(R.id.image);
        //BlurImageView background_image = imageLayout.findViewById(R.id.background_image);

        if(!local) {

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(video_file== true){
                        // Toast.makeText(context, "Video    Video  ", Toast.LENGTH_SHORT).show();
                        // download and open the  open the file
                        // FileOpen.openVideoFile(URL_VIDEO);
                        //holder.VideoIndicator.setVisibility(View.VISIBLE);
                        fileDownloader.Fileloader(context , URL_VIDEO);

                    }else{

                        Intent intent=new Intent(context,ImagePreviewSave.class)
                                .putExtra("uri","")
                                .putExtra("sender_name","Posts")
                                .putExtra("url",IMAGES.get(position).getUrl());
                        context.startActivity(intent);

                    }
                }
            });

            /*Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.placeholder))
                    .load(IMAGES.get(position).getUrl())
                    .into(background_image);

            background_image.setBlur(20);*/

            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.placeholder))
                    .load(IMAGES.get(position).getUrl())
                    .into(imageView);

        }else{

            try {
                compressedFile= new Compressor(context).setCompressFormat(Bitmap.CompressFormat.PNG).setQuality(75).setMaxHeight(350).compressToFile(new File(IMAGES.get(position).getLocal_path()));
                imageView.setImageURI(Uri.fromFile(compressedFile));
               // background_image.setImageURI(Uri.fromFile(compressedFile));
               // background_image.setBlur(20);

                /*imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                        options.setCompressionQuality(100);
                        options.setShowCropGrid(true);

                        UCrop.of(Uri.fromFile(compressedFile), Uri.fromFile(new File(context.getCacheDir(), String.format("hify_user_post_%s.png", random()))))
                                .withAspectRatio(1, 1)
                                .withOptions(options)
                                .start(activity);

                    }
                });*/

            } catch (IOException e) {
                e.printStackTrace();
            }




        }

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @NonNull
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }



}
