package com.group14.findeyourfriend.message;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.bracelet.SRBracelet;

import java.util.HashMap;

public class SearchResponse extends Message{

    private SRBracelet sender;
    // inherits receiver
    // inherits timestamp
    private HashMap<Integer, DatabaseEntry> searchedLocations = new HashMap<>();

    public SearchResponse(SRBracelet sender, HashMap<Integer, DatabaseEntry> locations){
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        searchedLocations = locations;
    }

    @Override
    public void process() {
        sender.storeSearchResponse(this);
        for (int dbKey: searchedLocations.keySet()) {
            if(dbKey == sender.getPerson().getId()) continue; //Dont update my own position
            DatabaseEntry entry = searchedLocations.get(dbKey);
            if(entry.getTimeStamp() > sender.getDataBase().getOrDefault(dbKey, new DatabaseEntry()).getTimeStamp()) sender.getDataBase().put(dbKey, entry); // update or overwrite
        }
    }
}
