package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.SRBracelet;

import java.util.HashMap;

public class SearchRequest extends Message{

    private SRBracelet sender;
    private SRBracelet receiver;
    // inherits timestamp
    private int preyID;

    public SearchRequest(SRBracelet sender, int preyID){
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        this.preyID = preyID;
    }

    @Override
    public void process() {
        receiver.storeSearchRequest(this);
        if (receiver.getDataBase().containsKey(preyID)){
            HashMap<Integer, DatabaseEntry> locations = new HashMap<>();
            locations.put(preyID, receiver.getDataBase().get(preyID));
            SearchResponse msg = new SearchResponse(receiver, locations);
            receiver.storeSearchResponseToBroadcast(msg);
            // TODO improve logic to include locations in only one message?
        }
    }

    public int getPreyID() {
        return preyID;
    }

}
