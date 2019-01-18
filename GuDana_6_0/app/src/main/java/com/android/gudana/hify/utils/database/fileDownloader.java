package com.android.gudana.hify.utils.database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;

import static com.mikepenz.iconics.Iconics.TAG;

public class fileDownloader {
    private String path_to_ressource = "";

    public String getPath_to_ressource() {
        return path_to_ressource;
    }

    public void setPath_to_ressource(String path_to_ressource) {
        this.path_to_ressource = path_to_ressource;
    }

    public fileDownloader() {
    }

    public  void Fileloader(final Context context , String URL_file , final GifImageView LoadingView  , final ImageView readyToPlay){
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
                            LoadingView.setVisibility(View.GONE);
                            readyToPlay.setVisibility(View.VISIBLE);
                            readyToPlay.setEnabled(true);
                            path_to_ressource = response.getDownloadedFile().getPath();


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

    public  void FileLoading_and_Caching_document(final Context context , String URL_file , final GifImageView LoadingView  , final ImageView readyToPlay){
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
                            LoadingView.setVisibility(View.GONE);
                            readyToPlay.setVisibility(View.VISIBLE);
                            readyToPlay.setEnabled(true);
                            path_to_ressource = response.getDownloadedFile().getPath();


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


    public  void FileLoading_and_Caching_voice(final Context context , String URL_file , final GifImageView LoadingView  , final ImageView readyToPlay){
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
                            LoadingView.setVisibility(View.GONE);
                            readyToPlay.setVisibility(View.VISIBLE);
                            readyToPlay.setEnabled(true);
                            path_to_ressource = response.getDownloadedFile().getPath();


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
