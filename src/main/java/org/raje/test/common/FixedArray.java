package org.raje.test.common;

import java.util.Arrays;

public class FixedArray {
	private int length;
	private int[] items;
	private int size = 0;


	public FixedArray(int length) {
		super();
		this.length = length;
		items = new int[length];
	}

	public synchronized void tryUpdateMinValue(int value) {
		if (size < length) {
			items[size] = value;
			size++;
			if(size == length) {
				Arrays.sort(items);
			}
		} else {
			if(value > items[0]) {
				items[0] = value;
				Arrays.sort(items);
			}
		}		
	}
	
	public int avgValue() {
		if(size == 0) {
			return 0;
		}
		int sum = 0;
		for(int item:items) {
			sum += item;
		}
		return sum / size;
	}

}
