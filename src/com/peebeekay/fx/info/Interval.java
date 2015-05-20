package com.peebeekay.fx.info;

public enum Interval{
	T(0,"t1",0), 
	M1(1,"m1",1), 
	M5(2,"m5",5), 
	M15(3,"m15",15), 
	M30(4,"m30",30)
	;
	
	int index;
	public String value;
	public int minutes;
	private Interval(int index, String value, int minutes){
		this.index = index;
		this.value = value;
		this.minutes = minutes;
	}
}
