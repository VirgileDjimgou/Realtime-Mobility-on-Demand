package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.history;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
/**
 * A fragment representing a collection of bookings.
 *
 * @author djimgou patrick virgile
 */
public class BookingHistoryListEmptyView extends View {
    public BookingHistoryListEmptyView(Context context) {
        super(context);
        init(null, 0);
    }

    public BookingHistoryListEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BookingHistoryListEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, com.bee.dtbs.mobile.android.dtbsandroidclient.R.styleable.BookingHistoryListEmptyView, defStyle, 0);
    }
}
