package com.android.gudana.hify.ui.activities.post;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.gudana.hify.adapters.UploadListAdapter;
import com.android.gudana.hify.utils.AndroidMultiPartEntity;
import com.android.gudana.hify.utils.AnimationUtil;
import com.android.gudana.R;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.JSONParser;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.nguyenhoanglam.imagepicker.model.Config;
//import com.nguyenhoanglam.imagepicker.model.Image;
//import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.gudana.hify.adapters.UploadListAdapter.uploadedImagesUrl;



public class PostImage extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private EditText mEditText;
    private Map<String, Object> postMap;
    private ProgressDialog mDialog;
    private ArrayList<Image> imagesList;
    private Compressor compressor;
    private LinearLayout empty_holder;
    private List<String> fileNameList;
    private List<String> fileUriList;
    private List<String> fileDoneList;
    public static boolean canUpload=false;

    private UploadListAdapter uploadListAdapter;
    private StorageReference mStorage;
    private RecyclerView mUploadList;
    private RelativeLayout pager_layout;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PostImage.class);
        context.startActivity(intent);
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


    private void hashtag_dialog(){

        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.blue)
                .setIcon(R.mipmap.ic_hashtag)
                .setTitle("Hashtag GuDana")
                .setMessage("Add hashtag  to help people on GuDana Network see your post or your services : use the symbol hashtag  # " +
                        "to start you own or choose GuDana sugested hashtag ")
                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                .setNotShowAgainOptionEnabled(0)
                .setNotShowAgainOptionChecked(true)
                .show();
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_post_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("New Image Post");

        try {
            getSupportActionBar().setTitle("New Image Post");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        postMap = new HashMap<>();

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        pager_layout=findViewById(R.id.pager_layout);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileUriList,fileNameList, fileDoneList);

        //RecyclerView
        mUploadList=findViewById(R.id.recyclerView);
        mUploadList.setItemAnimator(new SlideInUpAnimator());
        mUploadList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);

        mEditText = findViewById(R.id.text);
        empty_holder=findViewById(R.id.empty_holder);

        compressor=new Compressor(this)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.PNG)
                .setMaxHeight(350);

        pager_layout.setVisibility(View.GONE);
        empty_holder.setVisibility(View.VISIBLE);

        mDialog = new ProgressDialog(this);
        mStorage= FirebaseStorage.getInstance().getReference();
        hashtag_dialog();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images

            List<Image> images = ImagePicker.getImages(data);

            //     private ArrayList<Image> imagesList;
            imagesList = (ArrayList) images;

            if(!imagesList.isEmpty()){

                empty_holder.setVisibility(View.GONE);

                pager_layout.setVisibility(View.VISIBLE);
                pager_layout.setAlpha(0.0f);

                pager_layout.animate()
                        .setDuration(300)
                        .alpha(1.0f)
                        .start();


                for(int i=0;i<imagesList.size();i++) {

                    Uri fileUri = Uri.fromFile(new File(imagesList.get(i).getPath()));
                    String fileName = imagesList.get(i).getName();

                    fileNameList.add(fileName);
                    fileUriList.add(fileUri.toString());
                    fileDoneList.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();
                    final int finalI = i;
                    //new UploadFileToServer(imagesList.get(i).getPath(), Config.IMAGES_UPLOAD_URL,finalI).execute();
                    new Compressor(this)
                            .compressToFileAsFlowable(new File(imagesList.get(i).getPath()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(File file) {
                                    //compressedImage = file;
                                    new UploadFileToServer(file, Config.IMAGES_UPLOAD_URL,finalI).execute();

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    throwable.printStackTrace();
                                    //showError(throwable.getMessage());
                                }
                            });

                }

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.hi_menu_image_post, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:

                new MaterialDialog.Builder(this)
                        .title("Add Image(s)")
                        .content("How do you want to add image(s)?")
                        .positiveText("Camera")
                        .negativeText("Gallery")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                startPickImage(false);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                startPickImage(true);
                            }
                        })
                        .show();


                return true;

            case R.id.action_post:

                if (imagesList.isEmpty()) {
                    new MaterialDialog.Builder(this)
                            .title("No image(s) selected")
                            .content("It seems that you haven't selected image(s) for posting, How do you want to insert image(s)?")
                            .positiveText("Camera")
                            .negativeText("Gallery")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    startPickImage(false);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    startPickImage(true);
                                }
                            })
                            .show();
                    return true;
                }

                if (TextUtils.isEmpty(mEditText.getText().toString()) && !imagesList.isEmpty())
                    AnimationUtil.shakeView(mEditText, PostImage.this);
                else
                    uploadPost();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //region Post


    private void startPickImage(boolean gallery) {

        if(gallery) {

            // old  Images Picked nguyenImage picker  removed from gradle because of merging conflict with file provider
            /*
            ImagePicker.with(this)
                    .setToolbarColor("#FFFFFF")
                    .setStatusBarColor("#CCCCCC")
                    .setToolbarTextColor("#212121")
                    .setToolbarIconColor("#212121")
                    .setProgressBarColor("#5093FF")
                    .setBackgroundColor("#FFFFFF")
                    .setCameraOnly(false)
                    .setMultipleMode(true)
                    .setFolderMode(true)
                    .setShowCamera(false)
                    .setFolderTitle("Albums")
                    .setImageTitle("Photos")
                    .setDoneTitle("Done")
                    .setLimitMessage("You have reached selection limit")
                    .setMaxSize(7)
                    .setSavePath("Hify")
                    .setAlwaysShowDoneButton(true)
                    .setKeepScreenOn(true)
                    .start();
                    */

            ImagePicker.create(PostImage.this)
                    .theme(R.style.AppThemeFullScreenImage) // must inherit ef_BaseTheme. please refer to sample
                    .start(); // start image picker activity with request code

        }else{

            /*
            ImagePicker.with(this)
                    .setToolbarColor("#FFFFFF")
                    .setStatusBarColor("#CCCCCC")
                    .setToolbarTextColor("#212121")
                    .setToolbarIconColor("#212121")
                    .setProgressBarColor("#5093FF")
                    .setBackgroundColor("#FFFFFF")
                    .setCameraOnly(true)
                    .setMultipleMode(true)
                    .setDoneTitle("Done")
                    .setLimitMessage("You have reached capture limit")
                    .setMaxSize(7)
                    .setSavePath("Hify")
                    .setKeepScreenOn(true)
                    .start();

                    */

            ImagePicker.cameraOnly().start(PostImage.this); // Could be Activity, Fragment, Support Fragment

        }

    }

    private void uploadPost() {

        mDialog=new ProgressDialog(this);
        mDialog.setMessage("Posting...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                try{
                    mDialog.dismiss();
                    dialog.dismiss();
                    Toasty.info(PostImage.this, "Post sent", Toast.LENGTH_SHORT).show();
                    finish();

                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }
        });
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        if(canUpload) {
            if (!uploadedImagesUrl.isEmpty()) {

                mDialog.show();

                mFirestore.collection("Users").document(mCurrentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        postMap.put("userId", documentSnapshot.getString("id"));
                        postMap.put("username", documentSnapshot.getString("username"));
                        postMap.put("name", documentSnapshot.getString("name"));
                        postMap.put("userimage", documentSnapshot.getString("image"));
                        postMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                        postMap.put("image_count", uploadedImagesUrl.size());
                        try {
                            postMap.put("image_url_0", uploadedImagesUrl.get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_1", uploadedImagesUrl.get(1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_2", uploadedImagesUrl.get(2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_3", uploadedImagesUrl.get(3));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_4", uploadedImagesUrl.get(4));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_5", uploadedImagesUrl.get(5));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_6", uploadedImagesUrl.get(6));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        postMap.put("likes", "0");
                        postMap.put("favourites", "0");
                        postMap.put("description", mEditText.getText().toString());
                        postMap.put("color", "0");
                        postMap.put("is_video_post", false);


                        mFirestore.collection("Posts")
                                .add(postMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        mDialog.dismiss();
                                        Toast.makeText(PostImage.this, "Post sent", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Log.e("Error sending post", e.getMessage());
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Log.e("Error getting user", e.getMessage());
                    }
                });

            } else {
                mDialog.dismiss();
                Toast.makeText(this, "No image has been uploaded, Please wait or try again", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please wait, images are uploading...", Toast.LENGTH_SHORT).show();
        }

    }

    public void onSelectImage(View view) {
        startPickImage(true);
    }

    public void onSelectImageCamera(View view) {
        startPickImage(false);
    }

    //endregion

    //region Ads

    // InterstitialAd interstitialAd;

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private File filePath = null;
        private String Url_Server = null;
        private int finalI ;
        private String url_file_uploaded;
        private JSONParser jsonParser = new JSONParser();


        public UploadFileToServer(File filePath , String Url_Server_to_upload , int finalI) {
            super();
            this.filePath = filePath;
            this.Url_Server = Url_Server_to_upload;
            this.finalI = finalI;
            // do stuff
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            //progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(this.Url_Server);
            //HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);


            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                // File sourceFile = new File(filePath);
                File sourceFile = filePath;

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("PostImagesClasses", "Response from server: " + result);
            JSONObject json_data = null;

            try {
                json_data = new JSONObject(result);
                Boolean error = json_data.getBoolean("error");
                String url_file = json_data.getString("file_path");
                String message = json_data.getString("message");

                if(error == false){

                    this.url_file_uploaded = url_file;
                    fileDoneList.remove(finalI);
                    fileDoneList.add(finalI,"done");
                    uploadedImagesUrl.add(this.url_file_uploaded);
                    uploadListAdapter.notifyDataSetChanged();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            super.onPostExecute(result);
        }

    }


}
