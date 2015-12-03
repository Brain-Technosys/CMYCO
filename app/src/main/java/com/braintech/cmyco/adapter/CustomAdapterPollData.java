package com.braintech.cmyco.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.opengl.EGLExt;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.utils.Fonts;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.InjectView;

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

        if (convertView == null) {


            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_poll_data, null);

            holder = new ViewHolder();

            holder.txtViewPollName = (TextView) convertView.findViewById(R.id.txtViewPollName);
            holder.txtViewPollOptions = (TextView) convertView.findViewById(R.id.txtViewPollOptions);
            holder.txtViewPollNo = (TextView) convertView.findViewById(R.id.txtViewPollNo);
            holder.card_view = (CardView) convertView.findViewById(R.id.card_view);

            //set card view bg
            holder.card_view.setCardBackgroundColor(Color.parseColor("#2980b9"));

            //For Substitution
            holder.linLayoutPollTwo = (LinearLayout) convertView.findViewById(R.id.linLayoutPollTwo);
            holder.txtViewPollOptionsTwo = (TextView) convertView.findViewById(R.id.txtViewPollOptionsTwo);
            holder.txtViewPollNoTwo = (TextView) convertView.findViewById(R.id.txtViewPollNoTwo);


            Fonts.robotoRegular(context, holder.txtViewPollName);
            Fonts.robotoRegular(context, holder.txtViewPollOptions);
            Fonts.robotoRegular(context, holder.txtViewPollNo);

            PollData pollData = rowItems.get(position);
            Log.e("poll id", String.valueOf(pollData.getPoll_id()));

            if (pollData.getPoll_id() == 7) {
                holder.txtViewPollNo.setVisibility(View.GONE);
                holder.card_view.setCardBackgroundColor(Color.TRANSPARENT);
            }


            convertView.setTag(holder);


            if (pollData.getPoll_id() == 4) {
                //for Substitution, setting visibility visible to views
                holder.linLayoutPollTwo.setVisibility(View.VISIBLE);

                //For Substitution
                Fonts.robotoRegular(context, holder.txtViewPollOptionsTwo);
                Fonts.robotoRegular(context, holder.txtViewPollNoTwo);
            }


        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        PollData pollData = rowItems.get(position);

        holder.txtViewPollName.setText(pollData.getPoll_name());
        holder.txtViewPollName.setTag(pollData.getPoll_id());

        if (!pollData.getMaxId().equals("null") && !pollData.getMaxValue().equals("null")) {

            // data for all poll result
            if (Integer.parseInt(pollData.getMaxId().trim()) == 0) {
                holder.txtViewPollOptions.setVisibility(View.INVISIBLE);
                holder.txtViewPollNo.setVisibility(View.INVISIBLE);
            } else {
                holder.txtViewPollOptions.setVisibility(View.VISIBLE);
                holder.txtViewPollNo.setVisibility(View.VISIBLE);

                holder.txtViewPollNo.setText(pollData.getMaxId());
                holder.txtViewPollOptions.setText(pollData.getMaxValue());

            }


        }

        if (pollData.getPoll_id() == 4) {

            // data for Substitution player In
            if (Integer.parseInt(pollData.getMaxId().trim()) == 0) {
                holder.txtViewPollOptions.setVisibility(View.INVISIBLE);
                holder.txtViewPollNo.setVisibility(View.INVISIBLE);
            } else {
                holder.txtViewPollOptions.setVisibility(View.VISIBLE);
                holder.txtViewPollNo.setVisibility(View.VISIBLE);

                holder.txtViewPollNo.setText(pollData.getMaxId());
                holder.txtViewPollOptions.setText("Player In");
            }

            // data for Substitution player out
            PollData pollDataSubstitution = getRowItems.get(position + 1);

            if (!pollDataSubstitution.getMaxId().equals("null") && !pollDataSubstitution.getMaxValue().equals("null")) {
                if (Integer.parseInt(pollDataSubstitution.getMaxId().trim()) == 0) {
                    holder.txtViewPollOptionsTwo.setVisibility(View.INVISIBLE);
                    holder.txtViewPollNoTwo.setVisibility(View.INVISIBLE);
                } else {
                    holder.txtViewPollOptionsTwo.setVisibility(View.VISIBLE);
                    holder.txtViewPollNoTwo.setVisibility(View.VISIBLE);
                    holder.txtViewPollNoTwo.setText(pollDataSubstitution.getMaxId());
                    holder.txtViewPollOptionsTwo.setText("Player Out");
                }
            }


        }


        return convertView;
    }

    static class ViewHolder {
        TextView txtViewPollName;
        TextView txtViewPollNo;
        TextView txtViewPollOptions;

        LinearLayout linLayoutPollTwo;
        TextView txtViewPollNoTwo;
        TextView txtViewPollOptionsTwo;
        CardView card_view;
    }
}
