package com.android.gudana.GuDFeed;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.gudana.R;
// import com.android.gudana.linphone.CallOutgoingActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import java.io.File;

/**
 * Created by kbibek on 3/3/16.
 */
public class ImagePreviewActivity extends AbActivity {
    private ImageView ivImageGlide;
    private String uri;
    private String mimeType;

    private ChosenImage image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        uri = getIntent().getExtras().getString("uri");
        mimeType = getIntent().getExtras().getString("mimetype");
        image = getIntent().getExtras().getParcelable("chosen");

        ivImageGlide = (ImageView) findViewById(R.id.ivImageGlide);

        ivImageGlide.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayImage();
            }
        }, 500);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayImage() {
        int width = ivImageGlide.getWidth();
        int height = ivImageGlide.getHeight();
        Log.d(getClass().getSimpleName(), "displayImage: " + width + " x " + height);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_image);


        Glide.with(this)
                .load(Uri.fromFile(new File(uri)))
                .apply(requestOptions)
                .into(ivImageGlide);
    }
}
