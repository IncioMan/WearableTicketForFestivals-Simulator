package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.Clock;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.SRBracelet;

import java.util.HashMap;

public class SearchRequest extends Message{

    private SRBracelet sender;
    // inherits timestamp
    private int preyID;

    public SearchRequest(SRBracelet sender, int preyID){
        this.sender = sender;
        this.timestamp = Clock.getClock();
        this.preyID = preyID;
    }

    @Override
    public void process() {
        SRBracelet SRreceiver = (SRBracelet) receiver;
        SRreceiver.storeSearchRequest(this);
        if (SRreceiver.getDataBase().containsKey(preyID)){
            HashMap<Integer, DatabaseEntry> locations = new HashMap<>();
            locations.put(preyID, SRreceiver.getDataBase().get(preyID));
            SearchResponse msg = new SearchResponse(SRreceiver, locations);
            SRreceiver.storeSearchResponseToBroadcast(msg);
            // TODO improve logic to include locations in only one message?
        }
    }

    public int getPreyID() {
        return preyID;
    }

}
