package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.bracelet.SRBracelet;

public class SearchRequest extends Message{

    private SRBracelet sender;
    // inherits receiver
    // inherits timestamp
    private int preyID;

    public SearchRequest(SRBracelet sender, int preyID){
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        this.preyID = preyID;
    }

    @Override
    public void process() {
        sender.storeSearchRequest(this);
        // TODO if present in db, create Search Response and store it
    }

    public int getPreyID() {
        return preyID;
    }

}
