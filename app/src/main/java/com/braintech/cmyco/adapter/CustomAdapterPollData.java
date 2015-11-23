package com.braintech.cmyco.adapter;

/**
 * Created by Braintech on 11/6/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.utils.Fonts;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapterPollData extends BaseAdapter {

    Context context;

    ArrayList<PollData> rowItems;
    ArrayList<PollData> getRowItems;
    HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions;


    public CustomAdapterPollData(Context context, ArrayList<PollData> rowItems, HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions, int ignorePos) {
        this.context = context;
        this.rowItems = rowItems;
        this.getRowItems = rowItems;
        rowItems.remove(ignorePos);
        this.hashMapPollOptions = hashMapPollOptions;

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
        ViewHolder holder = null;

      //  PollData pollDataParent = rowItems.get(position);
//        if (pollDataParent.getPoll_id() != 5) {


        if (convertView == null) {


            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_poll_data, null);

            holder = new ViewHolder();

            holder.txtViewPollName = (TextView) convertView.findViewById(R.id.txtViewPollName);
            holder.txtViewPollOptions = (TextView) convertView.findViewById(R.id.txtViewPollOptions);
            holder.txtViewPollNo = (TextView) convertView.findViewById(R.id.txtViewPollNo);

            //For Substitution
            holder.frameLayoutPollTwo = (FrameLayout) convertView.findViewById(R.id.frameLayoutPollTwo);
            holder.txtViewPollOptionsTwo = (TextView) convertView.findViewById(R.id.txtViewPollOptionsTwo);
            holder.txtViewPollNoTwo = (TextView) convertView.findViewById(R.id.txtViewPollNoTwo);


            Fonts.robotoRegular(context, holder.txtViewPollName);
            Fonts.robotoRegular(context, holder.txtViewPollOptions);
            Fonts.robotoRegular(context, holder.txtViewPollNo);

            PollData pollData = rowItems.get(position);
            Log.e("poll id", String.valueOf(pollData.getPoll_id()));

            if (pollData.getPoll_id() == 7) {
                holder.txtViewPollNo.setVisibility(View.GONE);
            }


            convertView.setTag(holder);

//            convertView.setVisibility((pollData.getPoll_id() == 8) ? View.GONE : View.VISIBLE);
//            notifyDataSetChanged();


        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        PollData pollData = rowItems.get(position);


        holder.txtViewPollName.setText(pollData.getPoll_name());
        holder.txtViewPollName.setTag(pollData.getPoll_id());

        if (!pollData.getMaxId().equals("null") && !pollData.getMaxValue().equals("null")) {
            holder.txtViewPollNo.setText(pollData.getMaxId());
            holder.txtViewPollOptions.setText(pollData.getMaxValue());
        }

        if (pollData.getPoll_id() == 4) {
            //for Substitution, setting visibility visible to views
            holder.frameLayoutPollTwo.setVisibility(View.VISIBLE);
            holder.txtViewPollOptionsTwo.setVisibility(View.VISIBLE);

            //For Substitution
            Fonts.robotoRegular(context, holder.txtViewPollOptionsTwo);
            Fonts.robotoRegular(context, holder.txtViewPollNoTwo);

            PollData pollDataSubstitution = getRowItems.get(position + 1);

            if (!pollDataSubstitution.getMaxId().equals("null") && !pollDataSubstitution.getMaxValue().equals("null")) {
                holder.txtViewPollNoTwo.setText(pollDataSubstitution.getMaxId());
                holder.txtViewPollOptionsTwo.setText(pollDataSubstitution.getMaxValue());
            }


        }


        return convertView;
    }

    static class ViewHolder {
        TextView txtViewPollName;
        TextView txtViewPollNo;
        TextView txtViewPollOptions;

        FrameLayout frameLayoutPollTwo;
        TextView txtViewPollNoTwo;
        TextView txtViewPollOptionsTwo;
    }
}
