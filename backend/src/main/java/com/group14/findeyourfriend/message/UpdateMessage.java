package com.group14.findeyourfriend.message;

import com.group14.findeyourfriend.Clock;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;

import java.util.HashMap;

public class UpdateMessage extends Message{

    // inherits sender
    // inherits receiver
    // inherits timestamp
    private HashMap<Integer, DatabaseEntry> recentLocations = new HashMap<>();

    public UpdateMessage(Bracelet sender, HashMap<Integer, DatabaseEntry> locations){
        this.sender = sender;
        this.timestamp = Clock.getClock();
        this.recentLocations = locations;
    }

    @Override
    public void process() {
        //receiver.storeUpdateMessage(this);
        for (int dbKey: recentLocations.keySet()) {
            if(dbKey == receiver.getPerson().getId()) continue; //Dont update my own position
            DatabaseEntry entry = recentLocations.get(dbKey);
            if(entry.getTimeStamp() > receiver.getDataBase().getOrDefault(dbKey, new DatabaseEntry()).getTimeStamp()) receiver.getDataBase().put(dbKey, entry); // update or overwrite
        }
    }

    public HashMap<Integer, DatabaseEntry> getRecentLocations() {
        return recentLocations;
    }

}
