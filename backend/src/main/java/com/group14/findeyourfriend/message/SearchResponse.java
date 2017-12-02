package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.SRBracelet;

import java.util.HashMap;

public class SearchResponse extends Message{

    private SRBracelet sender;
    private SRBracelet receiver;
    // inherits timestamp
    private HashMap<Integer, DatabaseEntry> searchedLocations = new HashMap<>();

    public SearchResponse(SRBracelet sender, HashMap<Integer, DatabaseEntry> locations){
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        searchedLocations = locations;
    }

    @Override
    public void process() {
        receiver.storeSearchResponseToRelay(this);
        for (int dbKey: searchedLocations.keySet()) {
            if(dbKey == receiver.getPerson().getId()) continue; //Dont update my own position
            DatabaseEntry entry = searchedLocations.get(dbKey);
            if(entry.getTimeStamp() > receiver.getDataBase().getOrDefault(dbKey, new DatabaseEntry()).getTimeStamp()) receiver.getDataBase().put(dbKey, entry); // update or overwrite
        }
    }
}
