package com.android.gudana.hify.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.hify.adapters.viewFriends.RecyclerViewTouchHelper;
import com.android.gudana.hify.adapters.viewFriends.ViewFriendAdapter;
import com.android.gudana.hify.models.ViewFriends;
import com.android.gudana.hify.utils.AnimationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

public class FriendlistScrollingActivity extends AppCompatActivity {


    private List<ViewFriends> usersList;
    private ViewFriendAdapter usersAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private EmptyStateRecyclerView mRecyclerView;
    private Button creat_group;
    private EditText GroupName;
    private CircleImageView profile_image;
    private List<String> usersList_group = new ArrayList<>();;
    public ProgressDialog mDialog;
    public  static String pushId_group_room = "";
    public StorageReference storageReference;
    private static final int PICK_IMAGE =100 ;

    public Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist_scrolling);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Create New Group");
        mDialog = new ProgressDialog(this);


        // Button Create group    ...

        profile_image=findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilepic();
            }
        });


        creat_group = (Button) findViewById(R.id.creategroup__button);
         creat_group.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // chech  if the User  haat enter a group name   and the  if the are selected  more than one users ...
                 if(usersAdapter.getUsersList_group().size() > 0 && GroupName.getText().length() > 0 && imageUri!=null){

                     // Toasty.info(FriendlistScrollingActivity.this, "your new group is being registered", Toast.LENGTH_SHORT).show();
                     newgroupRegistration();

                     // Start the registration process ...

                 }else{

                     if(imageUri==null){
                         AnimationUtil.shakeView(profile_image, FriendlistScrollingActivity.this);
                         //Toast.makeText(RegisterActivity.this, "We recommend you to set a profile picture", Toast.LENGTH_SHORT).show();
                         mDialog.dismiss();

                         new LovelyInfoDialog(FriendlistScrollingActivity.this)
                                 .setTopColorRes(R.color.error)
                                 .setIcon(R.mipmap.ic_infos)
                                 .setTitle("Infos ")
                                 .setMessage("We recommend you to set a profile picture")
                                 .show();
                     }else{

                         new LovelyInfoDialog(FriendlistScrollingActivity.this)
                                 .setTopColorRes(R.color.error)
                                 .setIcon(R.mipmap.ic_infos)
                                 .setTitle("Infos ")
                                 .setMessage("please  select contacts from the list mentioned or  enter a valid group name")
                                 .show();

                         Toasty.warning(FriendlistScrollingActivity.this,
                                 "please  select contacts from the list mentioned or  enter a valid group name", Toast.LENGTH_SHORT).show();
                     }
                     //
                 }
             }
         });


        GroupName = (EditText) findViewById(R.id.group_name);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView =  findViewById(R.id.recyclerView);


        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...we proceed to the creation of your group");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        usersList = new ArrayList<>();
        usersAdapter = new ViewFriendAdapter(usersList, FriendlistScrollingActivity.this, "new_group");
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewTouchHelper(0, ItemTouchHelper.LEFT,
                new RecyclerViewTouchHelper.RecyclerItemTouchHelperListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                if (viewHolder instanceof ViewFriendAdapter.ViewHolder) {
                    usersAdapter.removeItem(viewHolder.getAdapterPosition());
                }
            }
        });

        mRecyclerView.setItemAnimator(new FlipInTopXAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(FriendlistScrollingActivity.this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(FriendlistScrollingActivity.this, DividerItemDecoration.VERTICAL));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(usersAdapter);

        mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(FriendlistScrollingActivity.this,"No friends found","Add some friends to manage them here"));

        mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING,
                new TextStateDisplay(FriendlistScrollingActivity.this,"We found some of your friends","We are getting information of your friends.."));

        mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                new TextStateDisplay(FriendlistScrollingActivity.this,"Sorry for inconvenience","Something went wrong :("));


        startListening();
    }

    public void setProfilepic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE);
    }


    public void startListening() {
        usersList.clear();
        firestore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Friends")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    ViewFriends users = doc.getDocument().toObject(ViewFriends.class);
                                    usersList.add(users);
                                    usersAdapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            Toast.makeText(FriendlistScrollingActivity.this, "No friends found.", Toast.LENGTH_SHORT).show();
                            mRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        Log.w("Error", "listen:error", e);

                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:

                //MainActivity_with_Drawer.tabLayout.getTabAt(3);
                //MainActivity_with_Drawer.mViewPager.setCurrentItem(3);
                //play_sound();
                this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void  newgroupRegistration(){
        // registering new User s
        // Registering user with data he gave us
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Upload picture image group   ...

        mDialog.show();

        try{


            if(firebaseUser != null)
            {

                DatabaseReference Call_Room = FirebaseDatabase.getInstance().getReference().child("Call_room").child(firebaseUser.getUid()).push();
                pushId_group_room = Call_Room.getKey();
                storageReference= FirebaseStorage.getInstance().getReference().child("images");

                final String userUid = firebaseUser.getUid();
                final StorageReference user_profile = storageReference.child(userUid + pushId_group_room+ ".jpg");
                user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    usersList_group = usersAdapter.getUsersList_group();
                                    HashMap<String, String> liste_user_group_id = new HashMap<>();
                                    for (int i = 0; i < usersList_group.size(); i++) {
                                        liste_user_group_id.put("user_id_"+i, usersList_group.get(i).toString());
                                    }

                                    // registering new User s
                                    // Registering user with data he gave us

                                    if(firebaseUser != null)
                                    {
                                        String userid = firebaseUser.getUid();
                                        DatabaseReference Call_Room = FirebaseDatabase.getInstance().getReference().child("Call_room").child(userid).push();
                                        pushId_group_room = Call_Room.getKey();

                                        Map callroom_map = new HashMap();
                                        callroom_map.put("group_id", pushId_group_room);
                                        callroom_map.put("group_name", GroupName.getText().toString());
                                        callroom_map.put("image", uri.toString());
                                        callroom_map.put("Users", liste_user_group_id);
                                        callroom_map.put("timestamp", ServerValue.TIMESTAMP);
                                        // the end of call extremely important    ..

                                        Map callroom_map_messages = new HashMap();
                                        callroom_map_messages.put("group_room/" + pushId_group_room, callroom_map);


                                        try{

                                            FirebaseDatabase.getInstance().getReference().updateChildren(callroom_map_messages, new DatabaseReference.CompletionListener()
                                            {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                                {

                                                    if(databaseError != null)
                                                    {
                                                        mDialog.dismiss();
                                                        Toasty.warning(FriendlistScrollingActivity.this, "Error " +databaseError.toString(), Toast.LENGTH_SHORT).show();

                                                    }else{


                                                        mDialog.dismiss();
                                                        Toasty.warning(FriendlistScrollingActivity.this, "new Group created " , Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(CreateGroupChatActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();




                                                    }
                                                }
                                            });


                                            FirebaseDatabase.getInstance().getReference().child("Users").child(userid).updateChildren(callroom_map_messages).addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {

                                                        mDialog.dismiss();
                                                        FriendlistScrollingActivity.this.finish();

                                                        Toasty.info(FriendlistScrollingActivity.this,
                                                                "Initialisation with Xshaka  Cloud successful ", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {
                                                        mDialog.dismiss();
                                                        Toasty.error(FriendlistScrollingActivity.this,
                                                                "sorry Xshaka  Cloud  is unreachable right now ! .... please try again later ", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                        }catch (Exception ex){
                                            mDialog.dismiss();
                                            ex.printStackTrace();
                                        }



                                        /*
                                        FirebaseDatabase.getInstance().getReference().child("Call_room").setValue(callroom_map_messages).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Toasty.warning(FriendlistScrollingActivity.this, "new group successfully created " +
                                                            "", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    mDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                                        */
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mDialog.dismiss();
                                }
                            });


                        } else {
                            mDialog.dismiss();
                        }
                    }
                });



            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
        //

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE){
            if(resultCode==RESULT_OK){
                imageUri=data.getData();
                // start crop activity
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options.setCompressionQuality(100);
                options.setShowCropGrid(true);

                UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "hify_user_profile_picture.png")))
                        .withAspectRatio(1, 1)
                        .withOptions(options)
                        .start(this);

            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                imageUri = UCrop.getOutput(data);
                profile_image.setImageURI(imageUri);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Log.e("Error", "Crop error:" + UCrop.getError(data).getMessage());
            }
        }


    }

}
