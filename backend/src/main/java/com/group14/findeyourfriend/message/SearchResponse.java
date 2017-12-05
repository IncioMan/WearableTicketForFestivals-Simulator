package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.SRBracelet;

import java.util.HashMap;

public class SearchResponse extends Message{

    private SRBracelet sender;
    // inherits timestamp
    private HashMap<Integer, DatabaseEntry> searchedLocations = new HashMap<>();

    public SearchResponse(SRBracelet sender, HashMap<Integer, DatabaseEntry> locations){
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        searchedLocations = locations;
    }

    @Override
    public void process() {
        SRBracelet srReceiver = (SRBracelet) receiver;
        srReceiver.storeSearchResponseToRelay(this);
        for (int dbKey: searchedLocations.keySet()) {
            if(dbKey == srReceiver.getPerson().getId()) continue; //Dont update my own position
            DatabaseEntry entry = searchedLocations.get(dbKey);
            if(entry.getTimeStamp() > srReceiver.getDataBase().getOrDefault(dbKey, new DatabaseEntry()).getTimeStamp()) srReceiver.getDataBase().put(dbKey, entry); // update or overwrite
        }
    }
}
