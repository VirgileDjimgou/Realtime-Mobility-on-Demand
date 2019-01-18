package com.android.gudana.hify.ui.activities.friends;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
//import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.fragments.RoomsFragment;
import com.android.gudana.chat.network.JSONParser;
import com.android.gudana.chatapp.models.StaticConfigUser_fromFirebase;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.hify.adapters.PostsAdapter;
import com.android.gudana.hify.models.Post;
import com.android.gudana.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.ImageTextStateDisplay;
import com.tylersuehr.esr.TextStateDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.android.gudana.chat.activities.ChatActivity.FCM_Message_Sender;
import static com.android.gudana.chat.activities.ChatActivity.getDateAndTime;

public class FriendProfile extends AppCompatActivity {

    private String id;


    public static void startActivity(Context context, String id) {

        if (!id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            context.startActivity(new Intent(context, FriendProfile.class).putExtra("f_id", id));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_friend_profile);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Profile");


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        id = getIntent().getStringExtra("f_id");

        final Bundle bundle = new Bundle();
        bundle.putString("id", id);

        Fragment fragment = new AboutFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_profile:
                        Fragment aboutfragment = new AboutFragment();
                        aboutfragment.setArguments(bundle);
                        loadFragment(aboutfragment);
                        break;
                    case R.id.action_posts:
                        Fragment profilefragment = new PostsFragment();
                        profilefragment.setArguments(bundle);
                        loadFragment(profilefragment);
                        break;
                    default:
                        Fragment fragment = new AboutFragment();
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                }
                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    public static class PostsFragment extends Fragment {

        List<Post> postList;
        PostsAdapter mAdapter;
        private EmptyStateRecyclerView mRecyclerView;
        String id;

        public PostsFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.hi_fragment_main, container, false);

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                id = bundle.getString("id");
            } else {
                Toast.makeText(rootView.getContext(), "Error retrieving information.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            postList = new ArrayList<>();
            mAdapter = new PostsAdapter(postList, rootView.getContext(), getActivity());

            mRecyclerView = rootView.findViewById(R.id.recyclerView);

            mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                    new ImageTextStateDisplay(rootView.getContext(), R.mipmap.no_posts, "No posts found", "User hasn't posted any posts yet"));

            mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                    new TextStateDisplay(rootView.getContext(), "Sorry for inconvenience", "Something went wrong :("));

            mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);

            getPosts(id);

            return rootView;
        }

        private void getPosts(String id) {

            FirebaseFirestore.getInstance()
                    .collection("Posts")
                    .whereEqualTo("userId", id)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {

                            if (!querySnapshot.isEmpty()) {

                                for (DocumentChange doc : querySnapshot.getDocumentChanges()) {

                                    Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                    postList.add(post);
                                    mAdapter.notifyDataSetChanged();

                                }


                            } else {
                                mRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                            Log.e("Error", e.getMessage());
                        }
                    });

        }


    }

    public static class AboutFragment extends Fragment {

        private FirebaseFirestore mFirestore;
        private FirebaseUser currentUser;
        private String id, friend_name, friend_email, friend_image, friend_token;
        private TextView name, username, email, location, post, friend, bio, created, req_sent;
        private CircleImageView profile_pic;
        private Button add_friend, remove_friend, accept, decline, send_message;
        private LinearLayout req_layout;
        private View rootView;
        private ProgressDialog mDialog;

        private String currentUserId, otherUserId;
        private Context mContext;
        private String room_uid , type_id;
        private int room_id;


        public AboutFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.hi_frag_about_profile, container, false);

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                id = bundle.getString("id");
                room_id = bundle.getInt("room_id");
            } else {
                Toast.makeText(rootView.getContext(), "Error retrieving information.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }


            send_message = rootView.findViewById(R.id.send_message);
            // send_message.setVisibility(View.GONE);
            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (id.isEmpty()) {
                        Toasty.error(mContext, "Inalid  User ID  ..you cannot start a conversion with this User  ", Toast.LENGTH_SHORT).show();

                    } else {

                        Intent sendMessageIntent = new Intent(rootView.getContext(), com.android.gudana.chat.activities.ChatActivity.class);

                        sendMessageIntent.putExtra("room_id", room_id);
                        sendMessageIntent.putExtra("room_name", friend_name);
                        sendMessageIntent.putExtra("type", com.android.gudana.chat.activities.ChatActivity.ROOM); // public static final int ROOM = 0, FRIEND = 1;
                        startActivity(sendMessageIntent);
                    }
                }
            });

            mFirestore = FirebaseFirestore.getInstance();
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            profile_pic = rootView.findViewById(R.id.profile_pic);
            name = rootView.findViewById(R.id.name);
            username = rootView.findViewById(R.id.username);
            email = rootView.findViewById(R.id.email);
            location = rootView.findViewById(R.id.location);
            post = rootView.findViewById(R.id.posts);
            friend = rootView.findViewById(R.id.friends);
            bio = rootView.findViewById(R.id.bio);
            req_sent = rootView.findViewById(R.id.friend_sent);

            add_friend = rootView.findViewById(R.id.friend_no);
            remove_friend = rootView.findViewById(R.id.friend_yes);
            req_layout = rootView.findViewById(R.id.friend_req);
            accept = rootView.findViewById(R.id.accept);
            decline = rootView.findViewById(R.id.decline);

            email.setVisibility(View.GONE);

            mDialog = new ProgressDialog(rootView.getContext());
            mDialog.setMessage("Please wait..");
            mDialog.setIndeterminate(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);

            // get Users Informations
            mFirestore.collection("Users")
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            friend_name = documentSnapshot.getString("name");
                            friend_email = documentSnapshot.getString("email");
                            friend_image = documentSnapshot.getString("image");
                            friend_token = documentSnapshot.getString("token_id");

                            username.setText(String.format(Locale.ENGLISH, "@%s", documentSnapshot.getString("username")));
                            name.setText(friend_name);
                            email.setText(friend_email);
                            location.setText(documentSnapshot.getString("location"));
                            bio.setText(documentSnapshot.getString("bio"));

                            Glide.with(rootView.getContext())
                                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                                    .load(friend_image)
                                    .into(profile_pic);


                        }
                    });

            mFirestore.collection("Users")
                    .document(currentUser.getUid())
                    .collection("Friends")
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists())
                                showRemoveButton();
                            else {

                                mFirestore.collection("Users")
                                        .document(id)
                                        .collection("Friend_Requests")
                                        .document(currentUser.getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (!documentSnapshot.exists()) {

                                                    mFirestore.collection("Users")
                                                            .document(currentUser.getUid())
                                                            .collection("Friend_Requests")
                                                            .document(id)
                                                            .get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {

                                                                        showRequestLayout();
                                                                        send_message.setVisibility(View.GONE);
                                                                    } else {

                                                                        showAddButton();
                                                                        send_message.setVisibility(View.GONE);

                                                                    }


                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w("error", "fail", e);
                                                                }
                                                            });

                                                } else {
                                                    req_sent.setVisibility(View.VISIBLE);
                                                    send_message.setVisibility(View.GONE);
                                                    req_sent.setAlpha(0.0f);

                                                    req_sent.animate()
                                                            .setDuration(500)
                                                            .alpha(1.0f)
                                                            .start();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("error", "fail", e);
                                    }
                                });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("error", "fail", e);
                        }
                    });


            mFirestore.collection("Users")
                    .document(id)
                    .collection("Friends")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            //Total Friends
                            friend.setText(String.format(Locale.ENGLISH, "Total Friends : %d", documentSnapshots.size()));
                        }
                    });

            FirebaseFirestore.getInstance().collection("Posts")
                    .whereEqualTo("userId", id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {

                            post.setText(String.format(Locale.ENGLISH, "Total Posts : %d", querySnapshot.size()));

                        }
                    });


            currentUserId = currentUser.getUid();
            otherUserId = id;
            FCM_Message_Sender = new CustomFcm_Util();


            return rootView;
        }

        private void showRequestLayout() {

            req_layout.setVisibility(View.VISIBLE);
            req_layout.setAlpha(0.0f);
            req_layout.animate()
                    .setDuration(500)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    new BottomDialog.Builder(rootView.getContext())
                                            .setTitle("Accept Friend Request")
                                            .setContent("Are you sure do you want to accept " + friend_name + "'s friend request?")
                                            .setPositiveText("Yes")
                                            .setPositiveBackgroundColorResource(R.color.colorAccentt)
                                            .setNegativeText("No")
                                            .onPositive(new BottomDialog.ButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull BottomDialog dialog) {
                                                    //acceptRequest();
                                                    create_room_chatServer();
                                                    dialog.dismiss();
                                                }
                                            }).onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                                }
                            });

                            decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    new BottomDialog.Builder(rootView.getContext())
                                            .setTitle("Decline Friend Request")
                                            .setContent("Are you sure do you want to decline " + name + "'s friend request?")
                                            .setPositiveText("Yes")
                                            .setPositiveBackgroundColorResource(R.color.colorAccentt)
                                            .setNegativeText("No")
                                            .onPositive(new BottomDialog.ButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull BottomDialog dialog) {
                                                    declineRequest();
                                                    dialog.dismiss();
                                                }
                                            }).onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                                }
                            });


                        }
                    }).start();


        }

        private void showAddButton() {

            add_friend.setVisibility(View.VISIBLE);
            add_friend.setAlpha(0.0f);
            add_friend.animate()
                    .setDuration(500)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            add_friend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    new BottomDialog.Builder(rootView.getContext())
                                            .setTitle("Add Friend ")
                                            .setContent("Are you sure do you want to send friend request to " + friend_name + " ?")
                                            .setPositiveText("Yes")
                                            .setPositiveBackgroundColorResource(R.color.colorAccentt)
                                            .setNegativeText("No")
                                            .onPositive(new BottomDialog.ButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull BottomDialog dialog) {
                                                    addFriend();
                                                    dialog.dismiss();
                                                }
                                            }).onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                                }
                            });
                        }
                    }).start();


        }

        private void showRemoveButton() {

            remove_friend.setVisibility(View.VISIBLE);
            remove_friend.setAlpha(0.0f);
            remove_friend.animate()
                    .setDuration(500)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            remove_friend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    new BottomDialog.Builder(rootView.getContext())
                                            .setTitle("Remove Friend ")
                                            .setContent("Are you sure do you want to remove " + friend_name + " from your friend list?")
                                            .setPositiveText("Yes")
                                            .setPositiveBackgroundColorResource(R.color.colorAccentt)
                                            .setNegativeText("No")
                                            .onPositive(new BottomDialog.ButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull BottomDialog dialog) {
                                                    removeFriend();

                                                    // remove friend  group chat  ...
                                                    // removeFriend_group_chat("mailtoremove");


                                                    // remove  on fireebase ... for chat

                                                    Map map = new HashMap<>();
                                                    map.put("Friends/" + otherUserId + "/" + currentUserId, null);
                                                    map.put("Friends/" + currentUserId + "/" + otherUserId, null);

                                                    // remove message button
                                                    send_message.setVisibility(View.GONE);
                                                    // Updating data

                                                    FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            if (databaseError == null) {
                                                                Log.d("debug ", "friend removed ");
                                                            } else {
                                                                Log.d("debug ", "removeFriend failed: " + databaseError.getMessage());
                                                            }
                                                        }
                                                    });
                                                    dialog.dismiss();
                                                }
                                            }).onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                                }
                            });
                        }
                    }).start();


        }

        public void acceptRequest() {

            mDialog.show();
            currentUserId = currentUser.getUid();
            otherUserId = id;


            // Pushing notification to get keyId
            DatabaseReference acceptNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            String acceptNotificationId = acceptNotificationRef.getKey();

            // "Packing" request
            HashMap<String, String> acceptNotificationData = new HashMap<>();
            acceptNotificationData.put("from", currentUserId);
            acceptNotificationData.put("type", "accept");

            // "Packing" data
            Map map = new HashMap<>();
            map.put("Friends/" + otherUserId + "/" + currentUserId + "/date", ServerValue.TIMESTAMP);
            map.put("Friends/" + currentUserId + "/" + otherUserId + "/date", ServerValue.TIMESTAMP);

            map.put("Requests/" + otherUserId + "/" + currentUserId, null);
            map.put("Requests/" + currentUserId + "/" + otherUserId, null);

            map.put("Notifications/" + otherUserId + "/" + acceptNotificationId, acceptNotificationData);

            // Updating data

            FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        // Toast.makeText(getContext()), "You are now friends!", Toast.LENGTH_SHORT).show();
                        Log.d("Request failed  ", "ok : ");

                    } else {
                        Log.d("Request failed  ", "acceptRequest failed: " + databaseError.getMessage());
                    }
                }
            });


            //Delete from friend request
            mFirestore.collection("Users")
                    .document(currentUser.getUid())
                    .collection("Friend_Requests")
                    .document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String, Object> friendInfo = new HashMap<>();
                            friendInfo.put("name", friend_name);
                            friendInfo.put("email", friend_email);
                            friendInfo.put("id", id);
                            friendInfo.put("image", friend_image);
                            friendInfo.put("token_id", friend_token);
                            friendInfo.put("room_uid", room_uid);
                            friendInfo.put("type_id", type_id);
                            friendInfo.put("room_id", room_id);
                            friendInfo.put("notification_id", String.valueOf(System.currentTimeMillis()));
                            friendInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));

                            //Add data friend to current user
                            mFirestore.collection("Users/" + currentUser.getUid() + "/Friends/")
                                    .document(id)
                                    .set(friendInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            //get the current user data
                                            mFirestore.collection("Users")
                                                    .document(currentUser.getUid())
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                            String name_c = documentSnapshot.getString("name");
                                                            final String email_c = documentSnapshot.getString("email");
                                                            final String id_c = documentSnapshot.getId();
                                                            String image_c = documentSnapshot.getString("image");
                                                            String token_c = documentSnapshot.getString("token_id");


                                                            final Map<String, Object> currentuserInfo = new HashMap<>();
                                                            currentuserInfo.put("name", name_c);
                                                            currentuserInfo.put("email", email_c);
                                                            currentuserInfo.put("id", id_c);
                                                            currentuserInfo.put("image", image_c);
                                                            currentuserInfo.put("room_uid", room_uid);
                                                            currentuserInfo.put("type_id", type_id);
                                                            currentuserInfo.put("token_id", token_c);
                                                            currentuserInfo.put("room_id", room_id);
                                                            currentuserInfo.put("notification_id", String.valueOf(System.currentTimeMillis()));
                                                            currentuserInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));

                                                            //Save current user data to Friend
                                                            mFirestore.collection("Users/" + id + "/Friends/")
                                                                    .document(id_c)
                                                                    .set(currentuserInfo)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {


                                                                            mFirestore.collection("Notifications")
                                                                                    .document(id)
                                                                                    .collection("Accepted_Friend_Requests")
                                                                                    .document(email_c)
                                                                                    .set(currentuserInfo)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            mDialog.dismiss();
                                                                                            Toasty.info(rootView.getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();

                                                                                            req_layout.animate()
                                                                                                    .alpha(0.0f)
                                                                                                    .setDuration(500)
                                                                                                    .setListener(new AnimatorListenerAdapter() {
                                                                                                        @Override
                                                                                                        public void onAnimationEnd(Animator animation) {
                                                                                                            super.onAnimationEnd(animation);
                                                                                                            req_layout.setVisibility(View.GONE);
                                                                                                            send_message.setVisibility(View.VISIBLE);
                                                                                                            showRemoveButton();
                                                                                                        }
                                                                                                    }).start();


                                                                                            // add friend  to groups Chat
                                                                                            //addNewFriend(friend_email);

                                                                                            // send notification   to tell that you ae a new friend   ...
                                                                                            FCM_Message_Sender.sendWithOtherThread("token",
                                                                                                    friend_token,
                                                                                                    "new friend",
                                                                                                    currentUserId,
                                                                                                    StaticConfigUser_fromFirebase.USER_NAME,
                                                                                                    StaticConfigUser_fromFirebase.USER_URL_IMAGE,
                                                                                                    getDateAndTime(),
                                                                                                    "room_disable",
                                                                                                    " You have a new friend");

                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Log.e("Error", e.getMessage());
                                                                                        }
                                                                                    });

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    mDialog.dismiss();
                                                                    Log.w("fourth", "listen:error", e);
                                                                }
                                                            });

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mDialog.dismiss();
                                                    Log.w("third", "listen:error", e);
                                                }
                                            });


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mDialog.dismiss();
                                    Log.w("second", "listen:error", e);
                                }
                            });
                            ;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Log.w("first", "listen:error", e);
                }
            });

        }

        public void create_room_chatServer() {
            // always chehck if you are  internet connection   ...
            // create room on Chat server
            String room = friend_name.trim();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String key = database.getReference("room_uid_key").push().getKey();
            String username = RoomsFragment.username;
            String session = RoomsFragment.session;
            String url_profile_pic = friend_image;
            type_id = "simple";
            room_uid = key;
            int User_id = RoomsFragment.user_id;
            String initiator_user_id = FirebaseAuth.getInstance().getUid();
            String member_user_id = id;

            //String fake_url_pic =  "https://image.flaticon.com/sprites/new_packs/145841-avatar-set.png";
            if (!room.isEmpty() && room.length() <= 100) {
                new CreateRoomAsyncTask_local(getActivity(), username,
                        session, room,
                        url_profile_pic, type_id,
                        room_uid, initiator_user_id,
                        member_user_id, User_id).execute();

            } else {
                Toasty.error(getActivity(), "Room name must be between 1-100 characters long.", Toast.LENGTH_LONG).show();
            }


            try {
                // to give time for  asyncheone parrallele prozess  Chat room validation on Chat server
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void declineRequest() {

            try {
                // decline chat friend request   ....
                // "Packing" data

                Map map = new HashMap<>();
                map.put("Requests/" + otherUserId + "/" + currentUserId, null);
                map.put("Requests/" + currentUserId + "/" + otherUserId, null);

                // Updating data on database

                FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.d("Error ", " Request declined ...");

                        } else {
                            Log.d("Error ", "cancelRequest failed: " + databaseError.getMessage());
                        }
                    }
                });


                //delete friend request data
                mFirestore.collection("Users").document(currentUser.getUid())
                        .collection("Friend_Requests").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.info(rootView.getContext(), "Friend request denied", Toast.LENGTH_SHORT).show();

                        req_layout.animate()
                                .alpha(0.0f)
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        req_layout.setVisibility(View.GONE);
                                        showAddButton();
                                    }
                                }).start();

                        // friend request declined   ...
                        // send notification   to tell that you ae a new friend   ...
                        // i want to take this function and adapt this to my   .... ..
                        FCM_Message_Sender.sendWithOtherThread("token",
                                friend_token,
                                "friend request declined",
                                currentUserId,
                                StaticConfigUser_fromFirebase.USER_NAME,
                                StaticConfigUser_fromFirebase.USER_URL_IMAGE,
                                getDateAndTime(),
                                "room_disable",

                                "declined Friend Request");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Error decline", e.getMessage());
                    }
                });
            } catch (Exception ex) {
                Log.w("error", "fail", ex);
                Toast.makeText(rootView.getContext(), "Some technical error occurred while declining friend request, Try again later.", Toast.LENGTH_SHORT).show();
            }
        }

        public void addFriend() {

            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(id)
                    .collection("Friends")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (!documentSnapshot.exists()) {

                                FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(id)
                                        .collection("Friend_Requests")
                                        .document(currentUser.getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                if (!documentSnapshot.exists()) {
                                                    executeFriendReq();
                                                } else {

                                                    add_friend.animate()
                                                            .alpha(0.0f)
                                                            .setDuration(500)
                                                            .setListener(new AnimatorListenerAdapter() {
                                                                @Override
                                                                public void onAnimationEnd(Animator animation) {
                                                                    super.onAnimationEnd(animation);
                                                                    add_friend.setVisibility(View.GONE);
                                                                    req_sent.setVisibility(View.VISIBLE);
                                                                    req_sent.setAlpha(0.0f);

                                                                    req_sent.animate()
                                                                            .setDuration(500)
                                                                            .alpha(1.0f)
                                                                            .start();
                                                                }
                                                            }).start();


                                                    Toast.makeText(rootView.getContext(), "Friend request has been sent already", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


                                // add friend in firebase chat   ....
                                // Pushing notification to get keyId
                                // send requests  for chat  ....
                                // Pushing notification to get keyId

                                // we don't need  this function  because  the cloud  ave bee disable on  google cloud Server  .....  so   i want to use a custom Server   ..
                                // a Customlocal  function  to send all kind of notification    ..
                                /*
                                DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
                                String notificationId = notificationRef.getKey();

                                // "Packing" request

                                HashMap<String, String> notificationData = new HashMap<>();
                                notificationData.put("from", currentUserId);
                                notificationData.put("type", "request");

                                HashMap map = new HashMap();
                                map.put("Requests/" + otherUserId + "/" + currentUserId + "/type", "received");
                                map.put("Requests/" + currentUserId + "/" + otherUserId + "/type", "sent");
                                map.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);


                                // send  Friend

                                // Updating data into database

                                FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                                {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                    {
                                        if(databaseError == null)
                                        {
                                            Log.d("failed " , "request send  " );
                                        }
                                        else
                                        {
                                            Log.d("failed " , "sendRequest failed: " + databaseError.getMessage());
                                        }
                                    }
                                });

                                */

                            }

                        }
                    });

        }

        private void executeFriendReq() {

            final Map<String, Object> userMap = new HashMap<>();

            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            final String email = documentSnapshot.getString("email");
                            final String name = documentSnapshot.getString("name");
                            final String image = documentSnapshot.getString("image");

                            userMap.put("name", documentSnapshot.getString("name"));
                            userMap.put("id", documentSnapshot.getString("id"));
                            userMap.put("email", email);
                            userMap.put("image", documentSnapshot.getString("image"));
                            userMap.put("token", documentSnapshot.getString("token_id"));
                            userMap.put("notification_id", String.valueOf(System.currentTimeMillis()));
                            userMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                            //Add to user
                            FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(id)
                                    .collection("Friend_Requests")
                                    .document(documentSnapshot.getString("id"))
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            // custom notification ....
                                            try {
                                                System.out.println("Send Notification");
                                                FCM_Message_Sender.sendWithOtherThread("token",
                                                        friend_token,
                                                        "Friend Request",
                                                        FirebaseAuth.getInstance().getUid(),
                                                        name,
                                                        image,
                                                        getDateAndTime(),
                                                        "room_disable",
                                                        "you are a hev Friend Request");

                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }


                                            //Add for notification data
                                            FirebaseFirestore.getInstance()
                                                    .collection("Notifications")
                                                    .document(id)
                                                    .collection("Friend_Requests")
                                                    .document(email)
                                                    .set(userMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(rootView.getContext(), "Friend request sent.", Toast.LENGTH_SHORT).show();

                                                            add_friend.animate()
                                                                    .alpha(0.0f)
                                                                    .setDuration(500)
                                                                    .setListener(new AnimatorListenerAdapter() {
                                                                        @Override
                                                                        public void onAnimationEnd(Animator animation) {
                                                                            super.onAnimationEnd(animation);
                                                                            add_friend.setVisibility(View.GONE);
                                                                            req_sent.setVisibility(View.VISIBLE);
                                                                            req_sent.setAlpha(0.0f);

                                                                            req_sent.animate()
                                                                                    .setDuration(500)
                                                                                    .alpha(1.0f)
                                                                                    .start();
                                                                        }
                                                                    }).start();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Error", e.getMessage());
                                                }
                                            });


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Error", e.getMessage());
                                }
                            });

                        }
                    });

        }

        public void removeFriend() {

            mFirestore.collection("Users").document(currentUser.getUid())
                    .collection("Friends").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(id)
                            .collection("Friends")
                            .document(currentUser.getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // remove friend  ...

                                    Toast.makeText(rootView.getContext(), "Friend removed successfully", Toast.LENGTH_SHORT).show();

                                    remove_friend.animate()
                                            .alpha(0.0f)
                                            .setDuration(500)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                    remove_friend.setVisibility(View.GONE);
                                                    showAddButton();
                                                }
                                            }).start();

                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error", e.getMessage());
                }
            });

        }


        public class CreateRoomAsyncTask_local extends AsyncTask<String, String, JSONObject> {
            private Activity activity;
            private String username, session, room_name, url_profile_pic, type_id, room_uid, initiator_user_id, member_user_id;
            private int user_id;

            public CreateRoomAsyncTask_local(Activity activity, String username,
                                             String session, String room_name,
                                             String url_profile_pic, String type_id,
                                             String room_uid, String initiator_user_id,
                                             String member_user_id, int user_id) {
                this.activity = activity;
                this.username = username;
                this.session = session;
                this.room_name = room_name;
                this.url_profile_pic = url_profile_pic;
                this.type_id = type_id;
                this.room_uid = room_uid;
                this.initiator_user_id = initiator_user_id;
                this.member_user_id = member_user_id;
                this.user_id = user_id;
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("initiator_user_id", initiator_user_id);
                    jsonObject.put("member_user_id", member_user_id);
                    jsonObject.put("room_uid", room_uid);
                    jsonObject.put("type_id", type_id);
                    jsonObject.put("user_id", user_id);
                    jsonObject.put("username", username);
                    jsonObject.put("session", session);
                    jsonObject.put("room_name", room_name);
                    jsonObject.put("profile_picture_id", url_profile_pic);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

                JSONParser jsonParser = new JSONParser();
                return jsonParser.getJSONFromUrl(((ChatApplication) activity.getApplication()).getURL() + "/createroom", jsonObject);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject == null) {
                    Toast.makeText(activity, "Cannot create room '" + room_name + "'", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    if (jsonObject.getBoolean("created") && jsonObject.has("room_id")) {
                        room_id = jsonObject.getInt("room_id");
                        acceptRequest();
                        //Intent intent = new Intent(activity, ChatActivity.class);
                        //intent.putExtra("room_name", room_name);
                        //intent.putExtra("room_id", room_id);
                        //activity.startActivity(intent);
                    } else {
                        if (jsonObject.has("error")) {
                            Toast.makeText(activity, "Cannot create room '" + room_name + "': " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Cannot create room '" + room_name + "'", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Cannot create room '" + room_name + "'", Toast.LENGTH_SHORT).show();
                }
            }


        }


    }


}
