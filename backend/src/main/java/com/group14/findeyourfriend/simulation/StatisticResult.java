package com.group14.findeyourfriend.simulation;

import org.apache.commons.lang3.tuple.Pair;

public class StatisticResult {
	private Pair<Double, Integer> currentValue;
	// these won't need to have also the number of instances, current value does
	// since it will be used to calculate the total average very time
	private Double maxValue;
	private Double minValue;

	public StatisticResult() {
		currentValue = Pair.of(0d, 0);
	}

	public Pair<Double, Integer> getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Pair<Double, Integer> currentValue) {
		this.currentValue = currentValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}
}
