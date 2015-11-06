package com.braintech.cmyco.objectclasses;

import java.io.Serializable;

/**
 * Created by Braintech on 11/6/2015.
 */
public class PollOptions implements Serializable {

    String poll_id;
    String poll_name;

    public PollOptions(String poll_id, String poll_name) {
        this.poll_id = poll_id;
        this.poll_name = poll_name;
    }

    public String getPoll_id() {
        return poll_id;
    }

    public void setPoll_id(String poll_id) {
        this.poll_id = poll_id;
    }

    public String getPoll_name() {
        return poll_name;
    }

    public void setPoll_name(String poll_name) {
        this.poll_name = poll_name;
    }
}
