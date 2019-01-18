package com.android.gudana.hify.ui.fragment;

import android.app.Dialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.android.gudana.hify.ui.activities.create_post;
import com.android.gudana.R;
import com.android.gudana.hify.ui.activities.post.PostImage;
import com.android.gudana.hify.ui.activities.post.PostText;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import android.widget.TextView;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class PostShortcutFragment extends AAH_FabulousFragment {

    Button btn_close;

    private FirebaseRecyclerAdapter adapter;
    FloatingActionButton action_chat ;
    public static  String WindowsTitel = "Create new Post";


    public static PostShortcutFragment newInstance() {
        PostShortcutFragment f = new PostShortcutFragment();
        return f;
    }


    @Override

    public void setupDialog(Dialog dialog, int style) {
        final View contentView = View.inflate(getContext(), R.layout.filter_sample_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        //RecyclerView rl_content = (RecyclerView) contentView.findViewById(R.id.chat_recycler);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        TextView TitelWindows =  (TextView) contentView.findViewById(R.id.titel_windows);
        TitelWindows.setText(WindowsTitel);


        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });


        LinearLayout text_post = contentView.findViewById(R.id.text_post);
        LinearLayout photo_post = contentView.findViewById(R.id.image_post);
        LinearLayout hybrid_post = contentView.findViewById(R.id.hybrid_post);

        hybrid_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter("closed");
                create_post.startActivity(PostShortcutFragment.this.getActivity());
            }
        });

        text_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter("closed");
                PostText.startActivity(PostShortcutFragment.this.getActivity());

            }
        });

        photo_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter("closed");
                PostImage.startActivity(PostShortcutFragment.this.getActivity());
            }
        });

        try{
            //params to set
            setAnimationDuration(400); //optional; default 500ms
            setPeekHeight(300); // optional; default 400dp
            setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
            setViewMain(rl_content); //necessary; main bottomsheet view
            setMainContentView(contentView); // necessary; call at end before super
            super.setupDialog(dialog, style); //call super at last

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();

    }

    @Override
    public void onStop()
    {
        super.onStop();

    }

}
