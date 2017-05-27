package br.ufrn.imd.unisexbathroom.util;

import java.util.Random;

public class RandInt {
	
	private static Random rand;
	
	private final int max;
	private final int min;
	
	public RandInt(int min, int max){
		rand = new Random();
		this.min = min;
		this.max = max;
	}
	
	public int rand() {
		int randdom = rand.nextInt((max - min) + 1) + min;
		return randdom;
	}

}
