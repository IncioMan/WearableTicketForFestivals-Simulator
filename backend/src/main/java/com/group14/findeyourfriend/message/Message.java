package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;

import java.util.HashMap;

public abstract class Message {

    protected Bracelet sender;
    protected Bracelet receiver;
    protected long timestamp;

    public abstract void process();

    public void setReceiver(Bracelet receiver) {
        this.receiver = receiver;
    }

}
