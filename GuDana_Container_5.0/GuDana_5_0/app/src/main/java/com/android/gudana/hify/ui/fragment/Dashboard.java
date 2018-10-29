package com.android.gudana.hify.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.android.gudana.hify.adapters.PostsAdapter;
import com.android.gudana.hify.models.Post;
import com.android.gudana.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.tylersuehr.esr.EmptyStateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.android.gudana.hify.ui.activities.MainActivity_GuDDana.loadpost_firstime;
import static com.android.gudana.hify.ui.activities.MainActivity_GuDDana.mode_public;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.TextToSpeechCallback;
import net.gotev.speech.ui.SpeechProgressView;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by amsavarthan on 29/3/18.
 */

public class Dashboard extends Fragment
        implements AAH_FabulousFragment.Callbacks,
        AAH_FabulousFragment.AnimationListener,
        SpeechDelegate {
    private String TAG = Dashboard.class.getName();

    List<Post> mPostsList;
    Query mQuery;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EmptyStateRecyclerView mPostsRecyclerView;
    PostsAdapter mAdapter;
    View view_layout;
    private List<String> mFriendIdList=new ArrayList<>();
    ChatShortcutFragment dialogFrag ;
    FriendUpdateShortcutFragment dialog_friends;


    // speech  ....
    private ImageView speak;
    private SpeechProgressView progress;
    private LinearLayout linearLayout;



    private Context context;
    public static ViewPager mViewPager;
    public LinearLayout SearchAppbar;


    private BoomMenuButton bmb ;
    private ImageView speak_seach;
    private EditText search_toolbar;
    private EditText search_principal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_layout = inflater.inflate(R.layout.fragment_card_main, container, false);


        // initCollapsingToolbar();
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view_layout.findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) view_layout.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        context = Dashboard.this.getContext();

        // linear Layour for app bar
        SearchAppbar = (LinearLayout) view_layout.findViewById(R.id.linearLayout_toolbar);
        final FloatingActionButton fab = (FloatingActionButton) view_layout.findViewById(R.id.fab);
        dialogFrag = ChatShortcutFragment.newInstance();
        dialogFrag.WindowsTitel = "Chats ShortCut";
        dialogFrag.setParentFab(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFrag.setCallbacks(Dashboard.this);
                dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());
            }
        });


        // friends Story  ...s
        final FloatingActionButton fab_friend = (FloatingActionButton) view_layout.findViewById(R.id.fab_friend_story);
        dialog_friends = FriendUpdateShortcutFragment.newInstance();
        dialog_friends.WindowsTitel = "Friends Last Story";
        dialog_friends.setParentFab(fab);
        fab_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_friends.setCallbacks(Dashboard.this);
                dialog_friends.show(getActivity().getSupportFragmentManager(), dialog_friends.getTag());
            }
        });

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //collapsingToolbar.setTitle(getString(R.string.gud_services));
                    SearchAppbar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    SearchAppbar.setVisibility(View.GONE);
                    //collapsingToolbar.setTitle("GuDFeed");
                    isShow = false;
                }
            }
        });

        // create Button  floating     ...

        // instaciate bmb button  ...
        try{

            //bmb.setDraggable(true);


            // boom menu    ...
            bmb = (BoomMenuButton) view_layout.findViewById(R.id.bmb_dash);
            assert bmb != null;
            bmb.setButtonEnum(ButtonEnum.Ham);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);
            bmb.setBoomEnum(BoomEnum.values()[7]); // random  boom
            bmb.setUse3DTransformAnimation(true);
            //bmb.setDraggable(true);
            bmb.setDuration(600);



            bmb.clearBuilders();

            // first
            HamButton.Builder builder_0_doc = new HamButton.Builder()
                    .normalImageRes(R.mipmap.ic_chat)
                    .normalText("Chats Shortcut")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            /*
                            try {
                                showDiag();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            */

                            dialogFrag.setCallbacks(Dashboard.this);
                            dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());


                            //  Toasty.info(context, "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();
                        }
                    });

            bmb.addBuilder(builder_0_doc);


            // first
            HamButton.Builder builder_0_video = new HamButton.Builder()
                    .normalImageRes(R.mipmap.ic_listener_tamtam)
                    .normalText("Friends Update")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

                            dialog_friends.setCallbacks(Dashboard.this);
                            dialog_friends.show(getActivity().getSupportFragmentManager(), dialog_friends.getTag());
                            // Toasty.info(context, "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();

                        }
                    });

            bmb.addBuilder(builder_0_video);


            // first
            HamButton.Builder search = new HamButton.Builder()
                    .normalImageRes(R.mipmap.ic_search)
                    .normalText("Search or Listen a custom Service")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

                            Toasty.info(context,
                                    "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();

                        }
                    });

            bmb.addBuilder(search);

        }catch(Exception ex){

            ex.printStackTrace();
        }
        // Init voice search    ...

        Speech.init(context, getActivity().getPackageName());


        search_toolbar = (EditText) view_layout.findViewById(R.id.search_toolbar);
        search_principal = (EditText) view_layout.findViewById(R.id.search_principal);


        progress = (SpeechProgressView) view_layout.findViewById(R.id.progress);

        speak_seach = (ImageView) view_layout.findViewById(R.id.voice_search);
        speak_seach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });

        speak = (ImageView) view_layout.findViewById(R.id.recordVoiceButton);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
        // speak.setOnClickListener(view -> onSpeakClick());

        int[] colors = {
                ContextCompat.getColor(context, android.R.color.black),
                ContextCompat.getColor(context, android.R.color.darker_gray),
                ContextCompat.getColor(context, android.R.color.black),
                ContextCompat.getColor(context, android.R.color.holo_orange_dark),
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
        };
        progress.setColors(colors);

        linearLayout = (LinearLayout) view_layout.findViewById(R.id.linearLayout_search);

        askPermission();

        // onRecordAudioPermissionGranted();


        return view_layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mPostsList = new ArrayList<>();
        mAdapter = new PostsAdapter(mPostsList, view.getContext(),getActivity());
        mPostsRecyclerView = view.findViewById(R.id.posts_recyclerview_new);
        mPostsRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mPostsRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        mPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPostsRecyclerView.setHasFixedSize(true);
        mPostsRecyclerView.setAdapter(mAdapter);

        /*
        mPostsRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new ImageTextStateDisplay(view.getContext(),R.mipmap.no_posts,"No posts found","Add some friends to see their posts."));

         */

        /*
        mPostsRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                new TextStateDisplay(view.getContext(),"Sorry for inconvenience","Something went wrong :("));

         */

        /*

        */
        if(mode_public == true){
            if (loadpost_firstime ==false ){ // to load all  post one tine once ....
                getAllPost();
                loadpost_firstime = true;
            }
            // and  make once update the next time  ...
            updatePostRealtime();

        }else{
            // or start  a  private mode  .... to  seel once the post of your friend
            if (loadpost_firstime ==false ){ // to load all  post one tine once ....
                loadpost_firstime = true;
                getPosts();
                getYourOwnPost();

                // to test the update without refresch  ()
                updateYourPostRealtime();
                updateFriendsPostRealtime();
            }

        }

    }

    /// ########### private Mode when you will see once the post of your friends ....

    private void  updateYourPostRealtime(){
        FirebaseFirestore.getInstance().collection("Posts")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for(DocumentChange doc:snapshots.getDocumentChanges()){
                            Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                            mPostsList.add(0,post);
                            mAdapter.notifyDataSetChanged();
                        }

                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    private void getYourOwnPost() {

        FirebaseFirestore.getInstance().collection("Posts")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        if(!querySnapshot.isEmpty()){

                            for(DocumentChange doc:querySnapshot.getDocumentChanges()){
                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                mPostsList.add(post);
                                mAdapter.notifyDataSetChanged();
                            }


                        }else{
                            mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error",e.getMessage());
                    }
                });

    }

    private void  updateFriendsPostRealtime(){

        mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        if (!snapshots.isEmpty()) {

                            for (final DocumentChange doc : snapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    mFirestore.collection("Users")
                                            .document(currentUser.getUid())
                                            .collection("Friends")
                                            .get()

                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {

                                                    for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {

                                                        if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                                            if(documentChange.getDocument().getId().equals(doc.getDocument().get("userId"))){

                                                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                                                mPostsList.add(0,post);
                                                                mAdapter.notifyDataSetChanged();

                                                            }

                                                        }

                                                    }


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                                                    Log.w("Error", "listen:error", e);
                                                }
                                            });

                                }

                            }

                        }

                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public void getPosts() {


        mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (final DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    mFirestore.collection("Users")
                                            .document(currentUser.getUid())
                                            .collection("Friends")
                                            .get()

                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {

                                                        for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {

                                                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                                                if(documentChange.getDocument().getId().equals(doc.getDocument().get("userId"))){

                                                                    Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                                                    mPostsList.add(post);
                                                                    mAdapter.notifyDataSetChanged();

                                                                }

                                                            }

                                                        }


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                                                    Log.w("Error", "listen:error", e);
                                                }
                                            });

                                }

                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        Log.w("Error", "listen:error", e);
                    }
                });

    }

    //7777 ####public mode when you will see all the  post on the network   ...  ...

    private void getAllPost() {

        FirebaseFirestore.getInstance().collection("Posts")
                //.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        if(!querySnapshot.isEmpty()){

                            for(DocumentChange doc:querySnapshot.getDocumentChanges()){
                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                mPostsList.add(post);
                                mAdapter.notifyDataSetChanged();
                            }


                        }else{
                           // mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error",e.getMessage());
                    }
                });

    }

    private void  updatePostRealtime(){
        FirebaseFirestore.getInstance().collection("Posts")
                //.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for(DocumentChange doc:snapshots.getDocumentChanges()){
                            Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                            mPostsList.add(0,post); // always on the top of the list
                            mAdapter.notifyDataSetChanged();
                        }

                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    // reveal methode  ..

    private void showDiag() throws InterruptedException {

        final View dialogView = View.inflate(context,R.layout.dialog_voice_record,null);

        final Dialog dialog = new Dialog(context,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);



        Thread.sleep(5);
        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (bmb.getX() + (bmb.getWidth()/2));
        int cy = (int) (bmb.getY())+ bmb.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }

     // Overiede Methode for   fabulos   Fragment

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());
        }

        if (dialog_friends.isAdded()) {
            dialog_friends.dismiss();
            dialog_friends.show(getActivity().getSupportFragmentManager(), dialog_friends.getTag());
        }

    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");

    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");

    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");

    }


    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
        if (result.toString().equalsIgnoreCase("swiped_down")) {
            //do something or nothing
        } else {
            //handle result
        }
    }


    private void askPermission() {

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CALL_PHONE


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(context, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }

    private void onButtonClick() {
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            onRecordAudioPermissionGranted();
        }
    }


    private void onRecordAudioPermissionGranted() {
        speak.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(progress, Dashboard.this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    private void onSpeakClick() {
        if (search_principal.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, R.string.input_something, Toast.LENGTH_LONG).show();
            return;
        }

        Speech.getInstance().say(search_principal.getText().toString().trim(), new TextToSpeechCallback() {
            @Override
            public void onStart() {
                Toast.makeText(context, "TTS onStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted() {
                Toast.makeText(context, "TTS onCompleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(context, "TTS onError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            Speech.getInstance().shutdown();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
        //Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value +  "dB");
    }

    @Override
    public void onSpeechResult(String result) {

        speak.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        search_principal.setText(result);

        if (result.isEmpty()) {
            Speech.getInstance().say(getString(R.string.repeat));

        } else {
            Speech.getInstance().say(result);
        }
    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        search_principal.setText("");
        for (String partial : results) {
            search_principal.append(partial + " ");
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(context);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }



}
