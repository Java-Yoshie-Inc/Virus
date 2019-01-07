package tools;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import tools.Logger.Level;

public class AverageNumber extends ArrayList<Double> {
	
	private static final long serialVersionUID = 1L;
	
	private int listIndexLimit;
	
	public AverageNumber(double number, int listIndexLimit) {
		this.listIndexLimit = listIndexLimit;
		add(number);
	}
	
	public double getAverage() {
		try {
			double average = 0;
			for(double numbers : this) {
				average += numbers;
			}
			average /= super.size();
			return average;
		} catch (ConcurrentModificationException e) {
			Logger.log(e.getMessage(), Level.ERROR);
			return 0;
		}
	}
	
	public void add(double number) {
		super.add(number);
		while(super.size() > listIndexLimit && listIndexLimit != -1) {
			super.remove(0);
		}
	}
	
	public double getLast() {
		return super.get(super.size()-1);
	}
	
	public double getFirst() {
		return super.get(0);
	}
	
	public int getListIndexLimit() {
		return listIndexLimit;
	}
	public void setListIndexLimit(int listIndexLimit) {
		this.listIndexLimit = listIndexLimit;
	}

}
