package com.android.gudana.GuDFeed;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.gudana.GuDFeed.activities.create_post;
import com.android.gudana.MainActivity_with_Drawer;
import com.android.gudana.R;
import com.android.gudana.chatapp.activities.ProfileActivity;
import com.android.gudana.viewpagercards.CardItem;
import com.android.gudana.viewpagercards.CardPagerAdapter;
import com.android.gudana.viewpagercards.ShadowTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class GuDFeed_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<CardCustom> albumList;
    private Context context;
    public static ViewPager mViewPager;

    public static CardPagerAdapter mCardAdapter;
    public static ShadowTransformer mCardShadowTransformer;


    private BoomMenuButton bmb ;




    public GuDFeed_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view_layout =null;
        try{
            // Inflate the layout for this fragment
            view_layout = inflater.inflate(R.layout.fragment_card_main, container, false);

            //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);

            context = view_layout.getContext();
            mViewPager = (ViewPager) view_layout.findViewById(R.id.viewPager);


            // instaciate bmb button  ...
            try{

                //bmb.setDraggable(true);


                // boom menu    ...
                bmb = (BoomMenuButton) view_layout.findViewById(R.id.bmb);
                assert bmb != null;
                bmb.setButtonEnum(ButtonEnum.Ham);
                bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);
                bmb.setBoomEnum(BoomEnum.values()[7]); // random  boom
                bmb.setUse3DTransformAnimation(true);
                //bmb.setDraggable(true);
                bmb.setDuration(500);



                bmb.clearBuilders();

                // first
                HamButton.Builder builder_0_doc = new HamButton.Builder()
                        .normalImageRes(R.mipmap.ic_your_feed)
                        .normalText("Create your own Service")
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {


                                Intent profileIntent = new Intent(GuDFeed_Fragment.this.getContext(), create_post.class);
                                startActivity(profileIntent);

                                Toasty.info(context,
                                        "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();
                            }
                        });

                bmb.addBuilder(builder_0_doc);


                // first
                HamButton.Builder builder_0_video = new HamButton.Builder()
                        .normalImageRes(R.mipmap.ic_listener_tamtam)
                        .normalText("create a listener for custom Service")
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {

                                Toasty.info(context,
                                        "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();

                            }
                        });

                bmb.addBuilder(builder_0_video);


                // first
                HamButton.Builder search = new HamButton.Builder()
                        .normalImageRes(R.mipmap.ic_search)
                        .normalText("Search")
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

            // Init View Pager ...
            mViewPager = (ViewPager) view_layout.findViewById(R.id.viewPager);

            mCardAdapter = new CardPagerAdapter();
            mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.drawable.album4));
            mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.drawable.album3));
            mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.drawable.album2));
            mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.drawable.album1));
            //mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, context));




            // initCollapsingToolbar();
            final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) view_layout.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle("GuDFeed");
            AppBarLayout appBarLayout = (AppBarLayout) view_layout.findViewById(R.id.appbar);
            appBarLayout.setExpanded(true);

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
                        collapsingToolbar.setTitle(getString(R.string.gud_services));
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbar.setTitle("GuDFeed");
                        isShow = false;
                    }
                }
            });

            recyclerView = (RecyclerView) view_layout.findViewById(R.id.recycler_view);

            albumList = new ArrayList<>();
            adapter = new AlbumsAdapter(context, albumList);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

            prepareCards();

            try {
                //Glide.with(this).load(R.drawable.cover).into((ImageView) view.findViewById(R.id.backdrop));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }catch(Exception ex ){
            ex.printStackTrace();
        }



        return view_layout;
    }



    /**
     * Adding few albums for testing
     */
    private void prepareCards() {
        int[] covers = new int[]{
                R.drawable.taxi_driver,
                R.drawable.cab_passenger,
                R.drawable.gud_delivery,
                R.drawable.cash_back
            };

        try{

            CardCustom a = new CardCustom("post 1", 24,covers[0]);
            albumList.add(a);

            a = new CardCustom("post 2",  35,covers[1]);
            albumList.add(a);

            a = new CardCustom("post 3", 44, covers[2]);
            albumList.add(a);


            a = new CardCustom("GuD Money",56,  covers[3]);
            albumList.add(a);


        }catch(Exception ex){
            ex.printStackTrace();

        }

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
