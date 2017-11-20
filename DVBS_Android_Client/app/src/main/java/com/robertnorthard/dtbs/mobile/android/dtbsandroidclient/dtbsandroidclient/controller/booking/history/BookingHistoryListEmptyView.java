package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.history;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;

/**
 * Handles all interactions related to an empty booking history list.
 *
 * @author robertnorthard
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
                attrs, R.styleable.BookingHistoryListEmptyView, defStyle, 0);
    }
}
