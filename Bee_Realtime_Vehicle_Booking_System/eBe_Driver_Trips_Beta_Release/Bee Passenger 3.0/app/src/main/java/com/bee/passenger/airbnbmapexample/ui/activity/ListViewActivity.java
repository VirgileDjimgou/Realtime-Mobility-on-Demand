package com.bee.passenger.airbnbmapexample.ui.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bee.passenger.R;
import com.bee.passenger.airbnbmapexample.constants.NetworkConstants;
import com.bee.passenger.airbnbmapexample.controller.BaseInterface;
import com.bee.passenger.airbnbmapexample.model.AddressModel;
import com.bee.passenger.airbnbmapexample.ui.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    ProgressBar mLoadingMoreDataProgress;
    private RecyclerView mRecyclerView;
    private ListViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    // private Places mPlaces;
    private AddressModel mAddressModel;
    private int mPageNumber = 1;
    private boolean mHasMore = true;
    private List<AddressModel> mAddressList = new ArrayList<AddressModel>();
    private SwipeRefreshLayout mSwipeLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        getSupportActionBar().setTitle("List View");
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_lisiting);

        mLoadingMoreDataProgress = (ProgressBar) findViewById(R.id.loading_progress);


        mLoadingMoreDataProgress.getIndeterminateDrawable().setColorFilter(0xff00b1c7, PorterDuff.Mode.MULTIPLY);
        mLoadingMoreDataProgress.setVisibility(View.GONE);


        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ListViewAdapter(mAddressList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.requestLayout();

        mSwipeLayout.setColorSchemeResources(R.color.gomalan_bule_bg);

        mAddressModel=new AddressModel(this, new BaseInterface() {
            @Override
            public void handleNetworkCall(Object object, int requestCode) {
                if (requestCode == NetworkConstants.ADDRESS_REQUEST) {
                    if (object instanceof ArrayList) {
                        mAddressList = new ArrayList<>();
                        mAddressList = (ArrayList) object;
                        mAdapter = new ListViewAdapter(mAddressList, ListViewActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(ListViewActivity.this, (String)object, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        mAddressModel.fetchAddressFromServer();

    }
}
