package com.peebeekay.fx.simulation.data.types;

public class ReferenceLine {
	
	private double value;
	
	public ReferenceLine(double value){
		this.value = value;
	}
	
	public double getValue(){
		return value;
	}
	
	public void adjustValue(double newValue){
		this.value = newValue;
	}

}
