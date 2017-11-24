package com.group14.findeyourfriend;

//========================================================================

// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Battery
{
	private double Capacity_mAh;
	public final double getCapacity_mAh()
	{
		return Capacity_mAh;
	}
	public final void setCapacity_mAh(double value)
	{
		Capacity_mAh = value;
	}

	private double energyLeft;

	public final double getEnergyLeft()
	{
		return energyLeft;
	}
	public final void setEnergyLeft(double value)
	{
		energyLeft = value;
	}


	public Battery(double capacity_mAh)
	{
		setCapacity_mAh(capacity_mAh);
		energyLeft = capacity_mAh;
	}

	public final void DecrementEnergy(double current_mA, double time_S)
	{
		// Q = I*t (Charge(C) equals Current(A) times time(s)) then to convert to mAh we divide by 3600s/h
		double mAh = (current_mA * time_S) / 3600;
		energyLeft = energyLeft - mAh;
	}
}