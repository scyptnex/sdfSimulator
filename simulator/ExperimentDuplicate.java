package simulator;

import java.io.*;
import java.util.*;

public class ExperimentDuplicate {
	
	public double[][] totdiffs;
	public double[][] totopts;
	public int[][] counts;

	public static void main(String[] args){
		new ExperimentDuplicate(new File("multi"));
	}
	
	public ExperimentDuplicate(File dir){
		
		totdiffs = new double[4][4];
		counts = new int[4][4];
		totopts = new double[4][4];
		
		for(String s : dir.list()) if(s.endsWith("d1.exp")){
			duplrun(dir, s.substring(0, s.indexOf("d1.exp")));
		}
		
		
		for(int f=0; f<totdiffs.length; f++){
			System.out.print(" & {\\bf" + (f+1) + "}");
		}
		System.out.println(" \\\\ \\hline");
		for(int n=0; n<totdiffs.length; n++){
			System.out.print("{\\bf " + (n+3) + "}");
			for(int f=0; f<totdiffs[n].length; f++){
				System.out.print(" & " + (counts[n][f] == 0 ? "-" : Problem.roundFourDecimals(totopts[n][f]/counts[n][f])));
			}
			System.out.println(" \\\\ \\hline");
		}
		

		for(int f=0; f<totdiffs.length; f++){
			System.out.print(" & {\\bf" + (f+1) + "}");
		}
		System.out.println(" \\\\ \\hline");
		for(int n=0; n<totdiffs.length; n++){
			System.out.print("{\\bf " + (n+3) + "}");
			for(int f=0; f<totdiffs[n].length; f++){
				System.out.print(" & " + counts[n][f]);
			}
			System.out.println(" \\\\ \\hline");
		}
		
		
		for(int f=0; f<totdiffs.length; f++){
			System.out.print(" & {\\bf" + (f+1) + "}");
		}
		System.out.println(" \\\\ \\hline");
		for(int n=0; n<totdiffs.length; n++){
			System.out.print("{\\bf " + (n+3) + "}");
			for(int f=0; f<totdiffs[n].length; f++){
				System.out.print(" & " + (counts[n][f] == 0 ? "-" : Problem.roundFourDecimals(totdiffs[n][f]/counts[n][f])));
			}
			System.out.println(" \\\\ \\hline");
		}
	}
	
	public void duplrun(File dir, String run){
		ArrayList<Mapper> dupls = new ArrayList<Mapper>();
		for(int i=0; i<5; i++){
			File chk = new File(dir, run + "d" + (i+1) + ".exp");
			if(chk.exists()){
				Mapper nw = new Mapper(chk.getAbsolutePath().substring(0, chk.getAbsolutePath().lastIndexOf(".exp")));
				if(nw.opt != null){
					dupls.add(nw);
				}
			}
		}
		
		if(dupls.size() == 1){
			System.err.println(run + " is all alone");
			return;
		}
		
		System.out.print("n" + dupls.get(0).prob.n + "(" + dupls.size() + "):");
		
		for(int i=0; i<dupls.size(); i++){
			int n = dupls.get(0).prob.n;
			double cos = Problem.roundFourDecimals(dupls.get(i).heurCost/dupls.get(i).optCost);
			System.out.print(" " + cos);
			counts[n-3][i] = counts[n-3][i] + 1;
			totdiffs[n-3][i] = totdiffs[n-3][i] + cos;
			totopts[n-3][i] = totopts[n-3][i] +  dupls.get(i).optCost;
		}
		
		System.out.println();
	}
	
}
