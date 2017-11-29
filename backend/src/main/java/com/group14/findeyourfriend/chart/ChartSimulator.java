package com.group14.findeyourfriend.chart;

import com.group14.findeyourfriend.ParameterParser;
import com.group14.findeyourfriend.Parameters;
import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.simulation.Event;
import com.group14.findeyourfriend.simulation.EventParser;
import com.group14.findeyourfriend.simulation.Simulation;
import com.group14.findeyourfriend.simulation.Simulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

public class ChartSimulator {

    public static void main(String[] args) {
        DebugLog.setEnabled(true);
        DebugLog.setEnabledTimers(false);
        try {
            InputStream eventStream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
            InputStream paramStream = Simulator.class.getClassLoader().getResourceAsStream("params.txt");
            Queue<Event> events = EventParser.parse(eventStream);
            Queue<Parameters> parameters = ParameterParser.parse(paramStream);
            Simulation simulation= new Simulation();
            simulation.init(events, 86400000*1);// Simulate 1 days in ms * days
            simulation.run(parameters.poll());
            Chart.main(new String[0]);// Show chart
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
