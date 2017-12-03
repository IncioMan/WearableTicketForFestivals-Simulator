package com.group14.findeyourfriend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;

import com.group14.findeyourfriend.bracelet.CPU;
import com.group14.findeyourfriend.bracelet.Radio;
import com.group14.findeyourfriend.debug.DebugLog;

public class ParameterParser {

	public static Queue<Parameters> parse(InputStream stream, boolean SRparameters) throws IOException {

		Queue<Parameters> parameters = new ArrayDeque<>();

		String thisLine;

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		int i = 0;
		CPU c = null;
		Radio r = null;
		while ((thisLine = in.readLine()) != null) {
			if (!thisLine.startsWith("#")) {
				switch (thisLine.charAt(0)) {
				case 'R':
					r = interpretRadio(thisLine.substring(2));
					i++;
					break;
				case 'C':
					if (SRparameters)
						c = interpretSRCPU(thisLine.substring(2));
					else
						c = interpretCPU(thisLine.substring(2));
					i++;
					break;
				}
				if (i % 2 == 0) {
					Parameters p = new Parameters();
					p.cpu = c;
					p.radio = r;
					parameters.add(p);
				}
			}
		}

		DebugLog.log("List of parameters correctly parsed.");
		return parameters;
	}

	private static Radio interpretRadio(String line) {
		try {
			String[] words = line.split(" ");
			double range = Double.parseDouble(words[0]);
			double passiveConsumtion = Double.parseDouble(words[1]);
			double activeConsumtion = Double.parseDouble(words[2]);
			double receivingConsumption = Double.parseDouble(words[3]);
			return new Radio(range, passiveConsumtion, activeConsumtion, receivingConsumption);
		} catch (Exception e) {
			System.out.println("Incorrect parsed radio");
			e.printStackTrace();
		}
		return null;

	}

	private static CPU interpretCPU(String line) {
		try {
			String[] words = line.split(" ");
			CPU c = new CPU();
			c.cpuCurrentSleep_mA = Double.parseDouble(words[0]);
			c.cpuCurrentRun_mA = Double.parseDouble(words[1]);
			c.cpuCurrentBroadcastAvg_mA = Double.parseDouble(words[2]);
			c.cpuCurrentConnectedAvg_mA = Double.parseDouble(words[3]);
			c.timerCpDelay = Integer.parseInt(words[4]);
			c.timerFDelay = Integer.parseInt(words[5]);
			c.timerLedDelay = Integer.parseInt(words[6]);
			c.timerRCPDelay = Integer.parseInt(words[7]);
			c.timerUpDelay = Integer.parseInt(words[8]);
			c.timerMoveToEventDelay = Integer.parseInt(words[9]);
			return c;

		} catch (Exception e) {
			System.out.println("Incorrect parsed CPU");
			e.printStackTrace();
		}
		return null;

	}

	private static CPU interpretSRCPU(String line) {
		try {
			String[] words = line.split(" ");
			CPU c = new CPU();
			c.cpuCurrentSleep_mA = Double.parseDouble(words[0]);
			c.cpuCurrentRun_mA = Double.parseDouble(words[1]);
			c.cpuCurrentBroadcastAvg_mA = Double.parseDouble(words[2]);
			c.cpuCurrentConnectedAvg_mA = Double.parseDouble(words[3]);
			c.timerCpDelay = Integer.parseInt(words[4]);
			c.timerFDelay = Integer.parseInt(words[5]);
			c.timerLedDelay = Integer.parseInt(words[6]);
			c.timerRCPDelay = Integer.parseInt(words[7]);
			c.timerUpDelay = Integer.parseInt(words[8]);
			// SR parameters
			c.timerIPDelay = Integer.parseInt(words[9]);
			c.timerLPDelay = Integer.parseInt(words[10]);
			c.timerDLPDelay = Integer.parseInt(words[11]);
			c.timerDRPDelay = Integer.parseInt(words[12]);
			c.timerRQPDelay = Integer.parseInt(words[13]);
			c.timerRRPDelay = Integer.parseInt(words[14]);
			c.timerRLPDelay = Integer.parseInt(words[15]);
			return c;

		} catch (Exception e) {
			System.out.println("Incorrect parsed CPU");
			e.printStackTrace();
		}
		return null;

	}
}
