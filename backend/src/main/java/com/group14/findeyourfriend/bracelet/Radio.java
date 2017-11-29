package com.group14.findeyourfriend.bracelet;

//========================================================================

// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Radio
{
	private double range_M;

	public final double getRange_M()
	{
		return range_M;
	}
	public final void setRange_M(double value)
	{
		range_M = value;
	}

	private RadioState State = RadioState.values()[0];
	public final RadioState getState()
	{
		return State;
	}
	public final void setState(RadioState value)
	{
		State = value;
	}

	private double _passiveConsumption_mA;
	private double _transmittingConsumption_mA;
	private double _receivingConsumption_mA;

	public Radio(double range, double passiveConsumption_mA, double transmittingConsumption_mA, double receivingConsumpion_mA)
	{
		_passiveConsumption_mA = passiveConsumption_mA;
		_transmittingConsumption_mA = transmittingConsumption_mA;
		_receivingConsumption_mA = receivingConsumpion_mA;
		range_M = range;
		setState(RadioState.Passive);
	}
	public final double getConsumption()
	{
		switch (getState())
		{
			case Passive:
				return _passiveConsumption_mA;
			case Transmitting:
				return _transmittingConsumption_mA;
			case Receiving:
				return _receivingConsumption_mA;
			default:
				return 0;
		}
	}

}