package com.group14.findeyourfriend.bracelet;

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


	public Battery(double capacity_mAh)
	{
		setCapacity_mAh(capacity_mAh);
		energyLeft = capacity_mAh;
	}

	public final void DecrementEnergy(double current_mA, double time_ms)
	{
		// Q = I*t (Charge(C) equals Current(A) times time(s)) then to convert to mAh we divide by 3600000ms/h
		double mAh = (current_mA * time_ms) / 3600000;
		energyLeft = energyLeft - mAh;
	}
}