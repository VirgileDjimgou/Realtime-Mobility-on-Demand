package com.android.gudana.cardview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.android.gudana.R;
import com.android.gudana.viewpagercards.CardItem;
import com.android.gudana.viewpagercards.CardPagerAdapter;
import com.android.gudana.viewpagercards.ShadowTransformer;

import java.util.ArrayList;
import java.util.List;

public class Card_Home_fragment extends Fragment implements Card_Home_fragment_onbackpressed {

    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<CardCustom> albumList;
    private Context context;
    public static ViewPager mViewPager;

    public static CardPagerAdapter mCardAdapter;
    public static ShadowTransformer mCardShadowTransformer;
    public static FloatingActionButton floatButton;
    public static RelativeLayout layoutAnimation;
    public static  Animation animShow, animHide;



    public Card_Home_fragment() {
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

            // Init View Pager ...
            layoutAnimation = (RelativeLayout) view_layout.findViewById(R.id.container);
            layoutAnimation.setVisibility(View.GONE);
            mViewPager = (ViewPager) view_layout.findViewById(R.id.viewPager);

            mCardAdapter = new CardPagerAdapter();
            mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.drawable.album4));
            mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.drawable.album3));
            mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.drawable.album2));
            mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.drawable.album1));
            //mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, context));

            mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
            //mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
            mViewPager.setOffscreenPageLimit(4);
            mCardShadowTransformer.enableScaling(true);


            // init animation ...

            animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
            animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide);
            floatButton = (FloatingActionButton) view_layout.findViewById(R.id.fab);
            floatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //container.setVisibility(View.GONE);
                    animHide.reset();
                    animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide);
                    layoutAnimation.setAnimation(animHide);
                    layoutAnimation.setVisibility(View.GONE);
                }
            });




            // initCollapsingToolbar();
            final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) view_layout.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle("GuD TamTam");
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
                        collapsingToolbar.setTitle("GuD TamTam");
                        isShow = false;
                    }
                }
            });


            try {
            recyclerView = (RecyclerView) view_layout.findViewById(R.id.recycler_view);

            albumList = new ArrayList<>();
            adapter = new AlbumsAdapter(context, albumList);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

            prepareCards();

                //Glide.with(this).load(R.drawable.cover).into((ImageView) view.findViewById(R.id.backdrop));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }catch(Exception ex ){
            ex.printStackTrace();
        }

        return view_layout;
    }

    public static void Driver_Card(){
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.moto_driver, R.drawable.moto_driver));
        mCardAdapter.addCardItem(new CardItem(R.string.taxi_driver, R.drawable.taxi_us));
        mCardAdapter.addCardItem(new CardItem(R.string.transporter, R.drawable.icon_transporte));
        mCardAdapter.addCardItem(new CardItem(R.string.custom, R.drawable.traffic));
        //mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, context));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        //mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(4);
        mCardShadowTransformer.enableScaling(true);

    }

    public static  void  Passenger_Card(){

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.drawable.cab_passenger));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.drawable.cab_passenger_wom));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.drawable.moto_driver));
        //mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, context));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        //mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mCardShadowTransformer.enableScaling(true);

    }
    public static  void Paket_Delivery(){


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
