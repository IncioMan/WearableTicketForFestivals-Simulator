package com.group14.findeyourfriend.chart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import com.group14.findeyourfriend.ParameterParser;
import com.group14.findeyourfriend.Parameters;
import com.group14.findeyourfriend.Simulator;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.simulation.Simulation;
import com.group14.findeyourfriend.simulation.events.Event;
import com.group14.findeyourfriend.simulation.events.EventParser;

public class ChartSimulator {

    public static void main(String[] args) {
        DebugLog.setEnabled(false);
        DebugLog.setEnabledTimers(false);
        try {
            InputStream eventStream = Simulator.class.getClassLoader().getResourceAsStream("chartevents.txt");
            InputStream paramStream = Simulator.class.getClassLoader().getResourceAsStream("params.txt");
            Queue<Event> events = EventParser.parse(eventStream);
            Queue<Parameters> parameters = ParameterParser.parse(paramStream, false);
            Simulation simulation= new Simulation();
            int simulationtime = (int)(86400000*0.1);// Simulate days in ms * days
            simulation.init(events, false, simulationtime);
            simulation.run(parameters.poll());
            Chart.main(new String[0]);// Show chart
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
