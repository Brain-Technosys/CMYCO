package com.braintech.cmyco.adapter;

/**
 * Created by Braintech on 11/6/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapterPollData extends BaseAdapter {

    Context context;

    ArrayList<PollData> rowItems;

    HashMap<Integer,ArrayList<PollOptions>> hashMapPollOptions;

    public CustomAdapterPollData(Context context, ArrayList<PollData> rowItems,HashMap<Integer,ArrayList<PollOptions>> hashMapPollOptions) {


        this.context = context;
        this.rowItems = rowItems;
        this.hashMapPollOptions=hashMapPollOptions;

    }

    @Override
    public int getCount() {

        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_poll_data, null);

            holder = new ViewHolder();

            holder.txtViewPollName = (TextView) convertView.findViewById(R.id.txtViewPollName);
            holder.txtViewPollOptions=(TextView)  convertView.findViewById(R.id.txtViewPollOptions);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PollData pollData = rowItems.get(position);

        holder.txtViewPollName.setText(pollData.getPoll_name());
        holder.txtViewPollName.setTag(pollData.getPoll_id());
        if(position==0)
        {
            holder.txtViewPollName.setText("FULL COURT PRESS+2-3 ZONE");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtViewPollName;
        TextView txtViewPollOptions;
    }
}
