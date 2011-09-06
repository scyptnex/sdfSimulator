package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class Mapper {
	
	public static void printProblem(PrintStream out, int p, double[][] invoke, Topology2 top){
		out.println("data;");
		out.println("\nparam n := " + top.actors.size());
		out.println("\nparam p := " + p);
		out.println("\nparam e := " + top.chans.size());
		
		System.out.println();
		printArray(out, "PI", invoke);
		
	}
	
	private static void printArray(PrintStream out, String name, double[][] arr){
		int rws = arr.length;
		int cols = (rws == 0 ? 0 : arr[0].length);
		out.println("param " + name + " :");
		for(int c=0; c<cols; c++){
			out.print("\t" + (c+1));
		}
		out.println(" :=");
		for(int r=0; r<rws; r++){
			out.print(r+1);
			for(int c=0; c<cols; c++){
				out.print("\t" + roundTwoDecimals(arr[r][c]));
			}
			out.println();
		}
		out.println(";");
	}
	//stolen froma forum http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
	private static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		return Double.valueOf(twoDForm.format(d));
	}
	
}
