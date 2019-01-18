package com.android.gudana.BootNavigation;

import android.os.Bundle;
import com.android.gudana.R;



public class MainActivityNoHide extends MainActivity {

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.bn_activity_main_fixed_scroll;
    }

    @Override
    protected void initializeUI(final Bundle savedInstanceState) {
        super.initializeUI(savedInstanceState);
    }

    @Override
    protected void initializeBottomNavigation(final Bundle savedInstanceState) { }

}
