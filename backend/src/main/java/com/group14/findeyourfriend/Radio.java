package com.group14.findeyourfriend;

//========================================================================

// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Radio {
	private double range_M;

	public final double getRange_M() {
		return range_M;
	}

	public final void setRange_M(double value) {
		range_M = value;
	}

	private RadioState State = RadioState.values()[0];

	public final RadioState getState() {
		return State;
	}

	public final void setState(RadioState value) {
		State = value;
	}

	private double _passiveConsumption;
	private double _transmittingConsumption;
	private double _receivingConsumption;

	public Radio(double range, double passiveConsumption, double transmittingConsumption, double receivingConsumpion) {
		_passiveConsumption = passiveConsumption;
		_transmittingConsumption = transmittingConsumption;
		_receivingConsumption = receivingConsumpion;
		range_M = range;
		setState(RadioState.Passive);
	}

	public final double getConsumption() {
		switch (getState()) {
		case Passive:
			return _passiveConsumption;
		case Transmitting:
			return _transmittingConsumption;
		case Receiving:
			return _receivingConsumption;
		default:
			return 0;
		}
	}

}