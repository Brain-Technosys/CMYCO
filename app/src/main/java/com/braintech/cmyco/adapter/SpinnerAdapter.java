package com.braintech.cmyco.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.braintech.cmyco.R;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.Fonts;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Braintech on 10/12/2015.
 */
public class SpinnerAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> list;
    Context context;

    public SpinnerAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context,
                R.layout.spinner_layout, null);
        HashMap<String, String> events = list.get(position);
        textView.setText(events.get(Const.KEY_NAME));
        Fonts.robotoRegular(context, textView);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context,
                R.layout.spinner_dropdown, null);
        HashMap<String, String> events = list.get(position);
        textView.setText(events.get(Const.KEY_NAME));
        Fonts.robotoRegular(context, textView);
        return textView;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
