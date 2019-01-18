package com.bee.drive.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.drive.R;
import com.bee.drive.Utility.ImageUtils;
import com.bee.drive.activity.MainActivity;
import com.bee.drive.data.FriendDB;
import com.bee.drive.data.GroupDB;
import com.bee.drive.data.SharedPreferenceHelper;
import com.bee.drive.data.StaticConfig;
import com.bee.drive.model.Configuration;
import com.bee.drive.model.User;
import com.bee.drive.other.CircleTransform;
import com.bee.drive.service.ServiceUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserProfileFragment extends Fragment {
    TextView tvUserName;
    ImageView ImageProfil;

    private List<Configuration> listConfig = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserInfoAdapter infoAdapter;

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String PHONE_LABEL = "Phone ";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private static final int PICK_IMAGE = 1994;
    private LovelyProgressDialog waitingDialog;

    private DatabaseReference userDB;
    private User myAccount;
    private Context context;

    private EditText mNameField, mPhoneField, mCarField;

    private Button mBack, mConfirm;


    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCar;
    private String mService;
    private String mProfileImageUrl;

    private Uri resultUri;

    private RadioGroup mRadioGroup;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

    }

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //get Config actuel User ...
            listConfig.clear();
            myAccount = dataSnapshot.getValue(User.class);

            setupArrayListInfo(myAccount);
            if(infoAdapter != null){
                infoAdapter.notifyDataSetChanged();
            }

            if(tvUserName != null){
                tvUserName.setText(myAccount.name);
            }

            // setImageAvatar(context, myAccount.avata);
            // SetImgesProfil();
            SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
            preferenceHelper.saveUserInfo(myAccount);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Remote Database Error
            Log.e(UserProfileFragment.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(StaticConfig.UID);

       //  userDB = FirebaseDatabase.getInstance().getReference().child(MainActivity_App.AppTypeDriver_or_Rider ).child(StaticConfig.UID);
        userDB.addListenerForSingleValueEvent(userListener);
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        context = view.getContext();
        ImageProfil = (ImageView) view.findViewById(R.id.img_avatar);
        ImageProfil.setOnClickListener(onAvatarClick);
        tvUserName = (TextView)view.findViewById(R.id.tv_username);

        SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(context);
        myAccount = prefHelper.getUserInfo();
        setupArrayListInfo(myAccount);
        // setImageAvatar(context, myAccount.avata);
        // SetImgesProfil();
        tvUserName.setText(myAccount.name);

        recyclerView = (RecyclerView)view.findViewById(R.id.info_recycler_view);
        infoAdapter = new UserInfoAdapter(listConfig);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(infoAdapter);

        waitingDialog = new LovelyProgressDialog(context);
        getUserInfo();
        return view;
    }


    private View.OnClickListener onAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(context)
                    .setTitle("Avatar")
                    .setMessage("Are you sure want to change avatar profile?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);

                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            ImageProfil.setImageURI(resultUri);

            Glide.with(getContext()).load(resultUri)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ImageProfil);

        }


        // after that we muss update the profil
        updateProfilImge();
    }


    public void updateProfilImge(){

        try{

            if(resultUri != null) {

                waitingDialog.setCancelable(false)
                        .setTitle("Avatar updating....")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();


                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                } catch (IOException e) {
                    waitingDialog.dismiss();
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // getActivity().finish();

                        waitingDialog.dismiss();

                        Log.d("Update Avatar", "failed");
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setTitle("Failed")
                                .setMessage("Failed to update Profil Image")
                                .show();

                        return;
                    }
                });
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        waitingDialog.dismiss();
                        Map newImage = new HashMap();
                        newImage.put("profileImageUrl", downloadUrl.toString());
                        mDriverDatabase.updateChildren(newImage);

                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setTitle("Success")
                                .setMessage("Update avatar successfully!")
                                .show();

                    }
                });
            }else{
                waitingDialog.dismiss();

                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorAccent)
                        .setTitle("Failed")
                        .setMessage("Please Select a valid Image !")
                        .show();
            }

        }catch(Exception ex){
            waitingDialog.dismiss();
            ex.printStackTrace();
        }
        waitingDialog.dismiss();

    }

    public void setupArrayListInfo(User myAccount){
        listConfig.clear();
        Configuration userNameConfig = new Configuration(USERNAME_LABEL, myAccount.name, R.drawable.ic_account_box);
        listConfig.add(userNameConfig);

        Configuration emailConfig = new Configuration(EMAIL_LABEL, myAccount.email, R.drawable.ic_email);
        listConfig.add(emailConfig);

        Configuration PhoneConfig = new Configuration(PHONE_LABEL, myAccount.phone, R.drawable.ic_phone);
        listConfig.add(PhoneConfig);

        Configuration resetPass = new Configuration(RESETPASS_LABEL, "", R.drawable.ic_restore);
        listConfig.add(resetPass);

        Configuration signout = new Configuration(SIGNOUT_LABEL, "", R.drawable.ic_power_settings);
        listConfig.add(signout);
    }

    private void getUserInfo(){
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    try{

                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        if(map.get("profileImageUrl")!=null){

                            try{

                                mProfileImageUrl = map.get("profileImageUrl").toString();

                                // Glide.with(getContext()).load(mProfileImageUrl).into(ImageProfil);

                                Glide.with(getContext()).load(mProfileImageUrl)
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(getActivity()))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(ImageProfil);




                            }catch (Exception ex ){
                                ex.printStackTrace();
                            }
                        }

                    }catch(Exception ex){
                        ex.printStackTrace();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString() , Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onDestroyView (){
        super.onDestroyView();
    }

    @Override
    public void onDestroy (){
        super.onDestroy();
    }

    public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder>{
        private List<Configuration> profileConfig;

        public UserInfoAdapter(List<Configuration> profileConfig){
            this.profileConfig = profileConfig;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_info_item_layout, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Configuration config = profileConfig.get(position);
            holder.label.setText(config.getLabel());
            holder.value.setText(config.getValue());
            holder.icon.setImageResource(config.getIcon());
            ((RelativeLayout)holder.label.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(config.getLabel().equals(SIGNOUT_LABEL)){
                        FirebaseAuth.getInstance().signOut();
                        FriendDB.getInstance(getContext()).dropDB();
                        GroupDB.getInstance(getContext()).dropDB();
                        ServiceUtils.stopServiceFriendChat(getContext().getApplicationContext(), true);
                        getActivity().finish();
                    }

                    if(config.getLabel().equals(USERNAME_LABEL)){
                        View vewInflater = LayoutInflater.from(context)
                                .inflate(R.layout.dialog_edit_username,  (ViewGroup) getView(), false);
                        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_username);
                        input.setText(myAccount.name);
                        // Alert Builder to save the new Profil User .....
                        new AlertDialog.Builder(context)
                                .setTitle("Edit username")
                                .setView(vewInflater)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @SuppressLint("WrongConstant")
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(input.getText().toString().isEmpty()){
                                            Toast.makeText(getContext(), "you to enter a valid Username .... " , 4000).show();

                                        }else{
                                            String newName = input.getText().toString();
                                            if(!myAccount.name.equals(newName)){
                                                changeUserName(newName);
                                            }

                                        }

                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }

                    if(config.getLabel().equals(PHONE_LABEL)){
                        View vewInflater = LayoutInflater.from(context)
                                .inflate(R.layout.dialog_edit_phone_number,  (ViewGroup) getView(), false);
                        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_phone);
                        input.setText(myAccount.phone);
                        // Alert Builder to save the new Profil User .....
                        new AlertDialog.Builder(context)
                                .setTitle("Edit Phone Number")
                                .setView(vewInflater)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @SuppressLint("WrongConstant")
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(input.getText().toString().isEmpty()){
                                            Toast.makeText(getContext(), "you to enter a valid phone number .... " , 4000).show();

                                        }else{
                                            String newPhone = input.getText().toString();
                                            if(!myAccount.phone.equals(newPhone)){
                                                changePhoneNumber(newPhone);
                                            }

                                        }
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }


                    if(config.getLabel().equals(RESETPASS_LABEL)){
                        new AlertDialog.Builder(context)
                                .setTitle("Password")
                                .setMessage("Are you sure want to reset password?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resetPassword(myAccount.email);
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                }
            });
        }

        private void changePhoneNumber(String newPhone){

            userDB.child("phone").setValue(newPhone);


            myAccount.phone = newPhone;
            SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(context);
            prefHelper.saveUserInfo(myAccount);

            setupArrayListInfo(myAccount);

        }

        private void changeUserName(String newName){
            userDB.child("name").setValue(newName);


            myAccount.name = newName;
            SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(context);
            prefHelper.saveUserInfo(myAccount);

            tvUserName.setText(newName);
            setupArrayListInfo(myAccount);
        }


        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new LovelyInfoDialog(context) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("Password Recovery")
                                    .setMessage("Sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new LovelyInfoDialog(context) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("False")
                                    .setMessage("False to sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return profileConfig.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView label, value;
            public ImageView icon;
            public ViewHolder(View view) {
                super(view);
                label = (TextView)view.findViewById(R.id.tv_title);
                value = (TextView)view.findViewById(R.id.tv_detail);
                icon = (ImageView)view.findViewById(R.id.img_icon);
            }
        }

    }

}
