package br.ufrn.imd.unisexbathroom.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notes {
	
	private static Date date = new Date();
	private static int count = 1;
	
	public static String getHora(){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
		date = new Date();
		return dateFormat.format(date); 
	}
	
	public static void print(Object obClass, String message, Object... object) {
		String TAG = "["+obClass.getClass().getSimpleName().toUpperCase()+"]\t";
		System.out.printf(getHora() + "  " + count++ + "\t"+TAG+message+"\n",object);
		
	}
	

}
