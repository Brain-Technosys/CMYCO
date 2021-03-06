package com.braintech.cmyco.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
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
import com.braintech.cmyco.activity.GameActivity;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.objectclasses.PollData;
import com.braintech.cmyco.objectclasses.PollOptions;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.Fonts;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.InjectView;

public class CustomAdapterPollData extends BaseAdapter {

    Activity context;

    ArrayList<PollData> rowItems;

    HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions;

    String max_id;
    String poll_duration;

    CommonAPI commonAPI;

    public CustomAdapterPollData(Activity context, ArrayList<PollData> rowItems, HashMap<Integer, ArrayList<PollOptions>> hashMapPollOptions, int ignorePos) {
        this.context = context;
        this.rowItems = rowItems;

        max_id = rowItems.get(4).getMaxId();
        poll_duration = rowItems.get(4).getPoll_duration();

        rowItems.remove(ignorePos);
        this.hashMapPollOptions = hashMapPollOptions;

        commonAPI=new CommonAPI(context);

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            holder.linLayPollOne = (LinearLayout) convertView.findViewById(R.id.linLayPollOne);
            holder.linLayoutPollTwo = (LinearLayout) convertView.findViewById(R.id.linLayoutPollTwo);
            holder.txtViewPollOptionsTwo = (TextView) convertView.findViewById(R.id.txtViewPollOptionsTwo);
            holder.txtViewPollNoTwo = (TextView) convertView.findViewById(R.id.txtViewPollNoTwo);

            holder.frameLayOptions = (FrameLayout) convertView.findViewById(R.id.frameLayOptions);


            Fonts.robotoRegular(context, holder.txtViewPollName);
            Fonts.robotoRegular(context, holder.txtViewPollOptions);
            Fonts.robotoRegular(context, holder.txtViewPollNo);

            PollData pollData = rowItems.get(position);
            //  Log.e("poll id", String.valueOf(pollData.getPoll_id()));

            if (pollData.getPoll_id() == 4) {
                //for Substitution, setting visibility visible to views
                holder.linLayoutPollTwo.setVisibility(View.VISIBLE);

                //For Substitution
                Fonts.robotoRegular(context, holder.txtViewPollOptionsTwo);
                Fonts.robotoRegular(context, holder.txtViewPollNoTwo);
            }

            if (pollData.getPoll_id() == 7) {
                holder.txtViewPollNo.setVisibility(View.GONE);
                holder.card_view.setCardBackgroundColor(Color.TRANSPARENT);
            }

            holder.frameLayOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = Integer.parseInt(v.getTag().toString());

                    Log.e("pos", "" + pos);
                    int poll_id = rowItems.get(pos).getPoll_id();
                    Log.e("poll_id", "" + poll_id);
                    if (poll_id == 7 || poll_id == 4) {
                        //do nothing
                    } else {
                        passIntent(poll_id, pos);
                    }

                }
            });


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.linLayPollOne.setTag(position);
        holder.linLayoutPollTwo.setTag(position);
        holder.frameLayOptions.setTag(position);

        PollData pollData = rowItems.get(position);

        holder.txtViewPollName.setText(pollData.getPoll_name());

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
                holder.txtViewPollOptions.setVisibility(View.VISIBLE);
                holder.txtViewPollNo.setVisibility(View.VISIBLE);
                holder.txtViewPollOptions.setText("Player Out");
            } else {
                holder.txtViewPollOptions.setVisibility(View.VISIBLE);
                holder.txtViewPollNo.setVisibility(View.VISIBLE);

                holder.txtViewPollNo.setText(pollData.getMaxId());
                holder.txtViewPollOptions.setText("Player Out");
            }

            // data for Substitution player out
            // PollData pollDataSubstitution = getRowItems.get(position + 1);

            // if (!pollDataSubstitution.getMaxId().equals("null") && !pollDataSubstitution.getMaxValue().equals("null")) {

            //Log.e("polloptions",""+pollDataSubstitution.getMaxId());
            if (max_id.equals("0")) {
                holder.txtViewPollOptionsTwo.setVisibility(View.VISIBLE);
                holder.txtViewPollNoTwo.setVisibility(View.VISIBLE);
                holder.txtViewPollOptionsTwo.setText("Player In");
            } else {
                holder.txtViewPollNoTwo.setVisibility(View.VISIBLE);
                holder.txtViewPollNoTwo.setText(max_id);
                holder.txtViewPollOptionsTwo.setText("Player In");
            }
            // }

            holder.linLayPollOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    commonAPI.setActiveUserStatus();

                    int pos = Integer.parseInt(v.getTag().toString());
                    int poll_id = rowItems.get(pos).getPoll_id();

                    if (poll_id == 4 || poll_id == 8)

                        passIntent(poll_id, position);

                }
            });

            holder.linLayoutPollTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    commonAPI.setActiveUserStatus();

                    int pos = Integer.parseInt(v.getTag().toString());
                    int poll_id = rowItems.get(pos).getPoll_id();

                    if (poll_id == 4)
                        passIntent(8, position);
                }
            });


        }


        return convertView;
    }

    static class ViewHolder {
        TextView txtViewPollName;
        TextView txtViewPollNo;
        TextView txtViewPollOptions;

        LinearLayout linLayoutPollTwo;
        LinearLayout linLayPollOne;

        TextView txtViewPollNoTwo;
        TextView txtViewPollOptionsTwo;
        CardView card_view;

        FrameLayout frameLayOptions;
    }

    private void passIntent(int poll_id, int position) {
        String pollName = rowItems.get(position).getPoll_name();


        ArrayList<PollOptions> arrayListPollOpt = hashMapPollOptions.get(poll_id);
        Intent intent = new Intent(context, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.KEY_POLL_NAME, pollName);
        bundle.putSerializable(Const.TAG_POLL_OPTION, arrayListPollOpt);
        if (poll_id == 8) {
            bundle.putLong(Const.KEY_POLL_DURATION, Long.parseLong(poll_duration));
        } else
            bundle.putLong(Const.KEY_POLL_DURATION, Long.parseLong(rowItems.get(position).getPoll_duration()));
        bundle.putInt(Const.KEY_POLL_ID, poll_id);
        intent.putExtras(bundle);

        context.finish();

        context.startActivity(intent);
    }
}
