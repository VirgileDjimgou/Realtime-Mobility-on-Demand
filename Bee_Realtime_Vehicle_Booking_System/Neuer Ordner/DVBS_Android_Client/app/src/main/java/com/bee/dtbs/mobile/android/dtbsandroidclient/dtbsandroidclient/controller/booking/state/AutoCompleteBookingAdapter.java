package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.GeocodeService;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoCompleteBookingAdapter extends ArrayAdapter {

    private static final String TAG = AutoCompleteBookingAdapter.class.getName();

    public AutoCompleteBookingAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public Filter getFilter() {
        return new AddressFilter();
    }

    final class AddressFilter extends Filter {
        @Override
        protected FilterResults performFiltering(
                CharSequence constraint) {
            FilterResults result = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            try {

                GeocodeService gs = new GeocodeService();
                String address = gs.findAddress(constraint.toString());
                if(address != null){
                    suggestions.add(address);
                }else{
                    suggestions.add("Address not found.");
                }

                result.values = suggestions;
                result.count = suggestions.size();

            } catch (IOException|JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.count > 0) {
                for (String s : (List<String>) results.values) {
                    add(s);
                }
            }
            notifyDataSetChanged();
        }
    }
}