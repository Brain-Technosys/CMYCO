package com.braintech.cmyco.objectclasses;

/**
 * Created by Braintech on 11/6/2015.
 */
public class PollData {

    int poll_id;

    String poll_name;
    String poll_start_time;
    String poll_end_time;
    String poll_duration;

    String maxId;
    String maxValue;

    public PollData(int poll_id, String poll_name, String poll_start_time, String poll_end_time, String poll_duration,String maxId,String maxValue)
    {
        this.poll_id = poll_id;
        this.poll_name = poll_name;
        this.poll_start_time = poll_start_time;
        this.poll_end_time = poll_end_time;
        this.poll_duration = poll_duration;
        this.maxId=maxId;
        this.maxValue=maxValue;
    }

    public int getPoll_id() {
        return poll_id;
    }

    public void setPoll_id(int poll_id) {
        this.poll_id = poll_id;
    }

    public String getPoll_name() {
        return poll_name;
    }

    public void setPoll_name(String poll_name) {
        this.poll_name = poll_name;
    }

    public String getPoll_start_time() {
        return poll_start_time;
    }

    public void setPoll_start_time(String poll_start_time) {
        this.poll_start_time = poll_start_time;
    }

    public String getPoll_end_time() {
        return poll_end_time;
    }

    public void setPoll_end_time(String poll_end_time) {
        this.poll_end_time = poll_end_time;
    }

    public String getPoll_duration() {
        return poll_duration;
    }

    public void setPoll_duration(String poll_duration) {
        this.poll_duration = poll_duration;
    }

    public String getMaxId() {
        return maxId;
    }

    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }
}
