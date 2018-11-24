package com.android.gudana.tindroid;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.gudana.R;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageSaveCallback;

public class Image_viewer extends AppCompatActivity {

    BigImageView bigImageView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bigImageView = (BigImageView) findViewById(R.id.mBigImage);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                bigImageView.setImageSaveCallback(new ImageSaveCallback() {
                    @Override
                    public void onSuccess(String uri) {
                        Toast.makeText(Image_viewer.this,
                                "Success",
                                Toast.LENGTH_SHORT).show();

                        Snackbar.make(view, "Image Saved into Gallery ", Snackbar.LENGTH_LONG)
                                .setAction("Dismmiss", null).show();
                    }

                    @Override
                    public void onFail(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(Image_viewer.this,
                                "Fail",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                bigImageView.saveImageIntoGallery();
            }
        });


        // Initialise Images   ...

        // or load with glide
        BigImageViewer.initialize(GlideImageLoader.with(Image_viewer.this));
        // show Images
        bigImageView.showImage(Uri.parse(url));

    }

}
