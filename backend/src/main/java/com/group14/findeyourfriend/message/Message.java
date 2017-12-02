package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.bracelet.Bracelet;

import java.util.HashSet;
import java.util.Set;

public abstract class Message {

    protected Bracelet sender;
    protected Bracelet receiver;
    protected long timestamp;
    protected Set<Integer> seenBracelets = new HashSet<>();

    public abstract void process();

    public void setReceiver(Bracelet receiver) {
        this.receiver = receiver;
    }

    public void setSeenBracelet(int braceletID) {
        seenBracelets.add(braceletID);
    }

    public boolean isSeen(int braceletID) {
        if(seenBracelets.contains(braceletID))
            return true;

        return false;
    }
}
