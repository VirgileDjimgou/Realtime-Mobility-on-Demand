package com.android.gudana.hify.utils.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.gudana.chatapp.utils.FileOpen;
import com.android.gudana.video_player.FullscreenActivity;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.mikepenz.iconics.Iconics.TAG;

public class fileDownloader {

    public static void Fileloader(final Context context , String URL_file){
        //Asynchronously load file as generic file
        FileLoader.with(context)
                //.load("https://firebasestorage.googleapis.com/v0/b/gudana-cloud-technology.appspot.com/o/post_video%2F%232GbnW-3null?alt=media&token=d11d6cc2-0068-43b2-a39c-575cba04ea15")
                .load(URL_file)

                .fromDirectory("Gudana_dir_files", FileLoader.DIR_EXTERNAL_PRIVATE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        // Glide.with(MainActivity.this).load(response.getBody()).into(iv);
                        // open video in cache
                        try {
                            Uri myUri = Uri.parse(response.getDownloadedFile().getPath());

                           //  FileOpen.openVideoFile(context , myUri);
                            try {
                                // FileOpen.openVideoFile(context, Uri.fromFile(new File(video.getOriginalPath())) );
                                Intent intent=new Intent(context,FullscreenActivity.class)
                                        .putExtra("uri",Uri.fromFile(new File(response.getDownloadedFile().getPath())).toString());
                                FullscreenActivity.LinkVideo =  response.getDownloadedFile().getPath();
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Log.d(TAG, "onError: " + t.getMessage());
                    }
                });
    }

}
