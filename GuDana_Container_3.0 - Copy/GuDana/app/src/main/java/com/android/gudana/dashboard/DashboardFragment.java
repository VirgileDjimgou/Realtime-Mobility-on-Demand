package com.android.gudana.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.GuDFeed.CardCustom;
import com.android.gudana.viewpagercards.CardItem;
import com.android.gudana.viewpagercards.CardPagerAdapter;
import com.android.gudana.viewpagercards.ShadowTransformer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.List;

import es.dmoral.toasty.Toasty;
public class DashboardFragment extends Fragment
{
    private FirebaseRecyclerAdapter adapter;
    private BoomMenuButton bmb ;
    private Context context =null;


    private RecyclerView recyclerView;
    private List<CardCustom> albumList;
    public static ViewPager mViewPager;

    public static CardPagerAdapter mCardAdapter;
    public static ShadowTransformer mCardShadowTransformer;
    public static FloatingActionButton floatButton;
    public static RelativeLayout layoutAnimation;
    public static Animation animShow, animHide;
    private ImageView  Driver , Passenger , Delivery , Money;




    public DashboardFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.dashboard_gudana, container, false);

        context = getActivity().getApplicationContext();




        // Init View Pager ...
        layoutAnimation = (RelativeLayout) view.findViewById(R.id.container);
        layoutAnimation.setVisibility(View.GONE);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        // init animation ...

        animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide);
        floatButton = (FloatingActionButton) view.findViewById(R.id.fab);
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


        Driver = (ImageView) view.findViewById(R.id.driver_action);
        Driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Driver_Card();
                layoutAnimation.setVisibility(View.VISIBLE);
                animShow.reset();
                animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
                layoutAnimation.setAnimation(DashboardFragment.animShow);
                Toasty.info(context,
                        "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();




            }
        });

        Money = (ImageView) view.findViewById(R.id.money_action);
        Money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(context,
                        "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();


            }
        });

        Passenger = (ImageView) view.findViewById(R.id.passenger_action);
        Passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Passenger_Card();
                animShow.reset();
                layoutAnimation.setVisibility(View.VISIBLE);
                animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
                layoutAnimation.setAnimation(DashboardFragment.animShow);
                Toasty.info(context,
                        "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();



            }
        });


        Delivery = (ImageView) view.findViewById(R.id.delivery_action);
        Delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(context,
                        "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();

            }
        });

        try{

            //bmb.setDraggable(true);


            // boom menu    ...
            bmb = (BoomMenuButton) view.findViewById(R.id.bmb);
            assert bmb != null;
            bmb.setButtonEnum(ButtonEnum.Ham);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);
            bmb.setBoomEnum(BoomEnum.values()[7]); // random  boom
            bmb.setUse3DTransformAnimation(true);
            //bmb.setDraggable(true);
            bmb.setDuration(500);


            Log.e("test" , "test");

            bmb.clearBuilders();

            // first
            HamButton.Builder builder_0_doc = new HamButton.Builder()
                    .normalImageRes(R.mipmap.ic_tamtam)
                    .normalText("Create your own Service")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

                            Toasty.info(context,
                                    "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();
                        }
                    });

            bmb.addBuilder(builder_0_doc);


            // first
            HamButton.Builder builder_0_video = new HamButton.Builder()
                    .normalImageRes(R.mipmap.ic_listener_tamtam)
                    .normalText("create a listener for a custom Service")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {


                            Toasty.info(context,
                                    "We are sorry. The service you requested is currently unavailable on your location . Please try again later.", Toast.LENGTH_LONG, true).show();


                        }
                    });

            bmb.addBuilder(builder_0_video);


        }catch(Exception ex){

            ex.printStackTrace();
        }

        return view;
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
    public static  void Paket_Delivery(){}



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