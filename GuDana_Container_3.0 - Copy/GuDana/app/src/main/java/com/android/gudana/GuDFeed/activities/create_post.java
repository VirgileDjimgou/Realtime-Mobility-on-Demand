package com.android.gudana.GuDFeed.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.gudana.GuDFeed.AbActivity;
import com.android.gudana.GuDFeed.adapters.SimpleAdapter;
import com.android.gudana.MainActivity_with_Drawer;
import com.google.firebase.database.FirebaseDatabase;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.CameraVideoPicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.callbacks.MediaPickerCallback;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.android.gudana.GuDFeed.adapters.MediaResultsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.gudana.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class create_post extends AbActivity implements VideoPickerCallback , ImagePickerCallback , MediaPickerCallback {

    private ListView lvResults;

    private Button Post;

    private ImageView video_taker, image_taker;

    private String pickerPath;

    private ImageView btEmoji;
    private EmojIconActions emojIcon;
    private EmojiconEditText messageEditText;
    private ListView recyclerView;
    private MediaResultsAdapter adapter = null;
    private List<ChosenFile> files_to_post = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_picker_activity);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setTitle(R.string.create_post);
        getSupportActionBar().setSubtitle("Create your storytelling");
        messageEditText = (EmojiconEditText) findViewById(R.id.editTextMessage);
        recyclerView = findViewById(R.id.lvResults);


        // Will handle typing feature, 0 means no typing, 1 typing, 2 deleting and 3 thinking (5+ sec delay)

        // Checking if root layout changed to detect soft keyboard

        final LinearLayout root = findViewById(R.id.root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousHeight = root.getRootView().getHeight() - root.getHeight() - recyclerView.getHeight();

            @Override
            public void onGlobalLayout() {
                int height = root.getRootView().getHeight() - root.getHeight() - recyclerView.getHeight();

                if (previousHeight != height) {
                    if (previousHeight > height) {
                        previousHeight = height;
                    } else if (previousHeight < height) {
                        // recyclerView.scrollToPosition(messagesList.size() - 1);

                        previousHeight = height;
                    }
                }
            }
        });


        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this, root, messageEditText, btEmoji);
        emojIcon.ShowEmojIcon();

        lvResults = (ListView) findViewById(R.id.lvResults);
        video_taker = (ImageView) findViewById(R.id.take_video);
        video_taker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_video();
                // pickVideoSingle();
            }
        });

        image_taker = (ImageView) findViewById(R.id.take_picture);
        image_taker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post video  ...
                dialog_Images();

                // takeVideo();
            }
        });

        Post = (Button) findViewById(R.id.post_button);
        Post.setEnabled(false);
        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post .... on firebase  ...
                // takeVideo();
            }
        });


        // detect all kind of problem
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectAll()// or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        //
        initRecyclerView();
    }



    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SimpleAdapter adapter = new SimpleAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setCount(10);
    }


    public void dialog_Images() {

        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.purple)
                .setButtonsColorRes(R.color.black)
                .setIcon(R.mipmap.ic_picture)
                .setTitle("Video Choice")
                .setMessage(" Options to select a video ")
                .setPositiveButton("use Camera", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture();
                    }
                })
                .setNegativeButton("from gallery (Single Image)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickImageSingle();
                    }
                })
                .setNeutralButton("from gallery (Multiple Images)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickImageMultiple();
                    }
                })
                .show();
    }

    public void dialog_video() {

        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.purple)
                .setButtonsColorRes(R.color.black)
                .setIcon(R.mipmap.ic_video)
                .setTitle("Video Choice")
                .setMessage(" Options to select a video ")
                .setPositiveButton("use Camera", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeVideo();
                        // Toast.makeText(create_post.this, "positive clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("from gallery ", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickVideoSingle();
                        //Toast.makeText(create_post.this, "positive clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                MainActivity_with_Drawer.tabLayout.getTabAt(3);
                MainActivity_with_Drawer.mViewPager.setCurrentItem(3);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /// ############################ Vide
    private VideoPicker videoPicker;

    private void pickVideoSingle() {
        videoPicker = new VideoPicker(this);
        videoPicker.shouldGenerateMetadata(true);
        videoPicker.shouldGeneratePreviewImages(true);
        videoPicker.setVideoPickerCallback(this);
        videoPicker.pickVideo();
    }


    private CameraVideoPicker cameraPicker_video;

    private void takeVideo() {
        cameraPicker_video = new CameraVideoPicker(this);
        cameraPicker_video.shouldGenerateMetadata(true);
        cameraPicker_video.setCacheLocation(CacheLocation.INTERNAL_APP_DIR);
        cameraPicker_video.shouldGeneratePreviewImages(true);
        Bundle extras = new Bundle();
        // For capturing Low quality videos; Default is 1: HIGH
        extras.putInt(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        // Set the duration of the video
        extras.putInt(MediaStore.EXTRA_DURATION_LIMIT, 60);
        cameraPicker_video.setExtras(extras);
        cameraPicker_video.setVideoPickerCallback(this);
        pickerPath = cameraPicker_video.pickVideo();
    }


    ////////////// ##########   Images ...

    private ImagePicker imagePicker;

    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.setDebugglable(true);
        imagePicker.setFolderName("Random");
        imagePicker.setRequestId(1234);
        imagePicker.ensureMaxSize(500, 500);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extras.CAMERA_FACING", 1);
        imagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_PUBLIC_DIR);
        imagePicker.pickImage();
    }

    public void pickImageMultiple() {
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(this);
        imagePicker.allowMultiple();
        imagePicker.pickImage();
    }

    private CameraImagePicker cameraPicker;

    public void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.setDebugglable(true);
        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
        cameraPicker.setImagePickerCallback(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        pickerPath = cameraPicker.pickImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_VIDEO_DEVICE) {
                if (videoPicker == null) {
                    videoPicker = new VideoPicker(this);
                    videoPicker.setVideoPickerCallback(this);
                }
                videoPicker.submit(data);
            } else if (requestCode == Picker.PICK_VIDEO_CAMERA) {
                if (cameraPicker_video == null) {
                    cameraPicker_video = new CameraVideoPicker(this, pickerPath);
                }
                cameraPicker_video.submit(data);
            }

            // images
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(this);
                }
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                }
                cameraPicker.submit(data);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraVideoPicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // path value to be able to re-intialize CameraVideoPicker
        if (savedInstanceState.containsKey("picker_path")) {
            pickerPath = savedInstanceState.getString("picker_path");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onVideosChosen(List<ChosenVideo> files) {
        if (files_to_post.size() < 10) {

            files_to_post.addAll(files);

            MediaResultsAdapter adapter = new MediaResultsAdapter(files_to_post, this);
            this.adapter = this.adapter;
            lvResults.setAdapter(adapter);

        } else {

            Toasty.warning(create_post.this,
                    "you can not  post more than  10 Items ! ", Toast.LENGTH_LONG, true).show();

        }


    }

    @Override
    public void onMediaChosen(List<ChosenImage> images, List<ChosenVideo> videos) {
        List<ChosenFile> files = new ArrayList<>();
        if (images != null) {
            files.addAll(images);
        }
        if (videos != null) {
            files.addAll(videos);
        }
        MediaResultsAdapter adapter = new MediaResultsAdapter(files, this);
        lvResults.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(create_post.this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to close the Post Section ? ");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                //Intent intent = new Intent(Intent.ACTION_MAIN);
                //intent.addCategory(Intent.CATEGORY_HOME);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    // added

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        if (files_to_post.size() < 10) {

            files_to_post.addAll(images);
            this.adapter = new MediaResultsAdapter(files_to_post, this);
            lvResults.setAdapter(adapter);
        } else {

            Toasty.warning(create_post.this,
                    "you can not  post more than  10 Items ! ", Toast.LENGTH_LONG, true).show();

        }

    }

}
