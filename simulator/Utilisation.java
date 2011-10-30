package simulator;

import java.io.*;
import java.util.*;

public class Utilisation {
	
	public ArrayList<Boolean> multi;
	public ArrayList<Integer> scale;
	public ArrayList<Double> minInvoke;
	public ArrayList<Double> maxInvoke;
	public ArrayList<Integer> ps;
	public ArrayList<Integer> optUtil;
	public ArrayList<Integer> heurUtil;
	
	public static void main(String[] args){
		new Utilisation(new File("distrib"));
	}
	
	public Utilisation(File dir){
		
		multi = new ArrayList<Boolean>();
		scale = new ArrayList<Integer>();
		minInvoke = new ArrayList<Double>();
		maxInvoke = new ArrayList<Double>();
		ps = new ArrayList<Integer>();
		optUtil = new ArrayList<Integer>();
		heurUtil = new ArrayList<Integer>();
		
		for(File fi : dir.listFiles()){
			if(fi.getName().endsWith(".exp")){
				account(fi);
			}
		}
		
		for(int i=0; i<14; i++){
			average(i);
		}
	}
	
	private void average(int ord){
		int mcount = 0;
		int ucount = 0;
		double mMax = 0.0;
		double mMin = 0.0;
		double uMax = 0.0;
		double uMin = 0.0;
		double mOpt = 0.0;
		double uOpt = 0.0;
		double mHeu = 0.0;
		double uHeu = 0.0;
		for(int i=0; i<multi.size(); i++){
			if(scale.get(i) == ord){
				if(multi.get(i)){
					mcount++;
					mMax += maxInvoke.get(i);
					mMin += minInvoke.get(i);
					mOpt += (double)optUtil.get(i)/(double)ps.get(i);
					mHeu += (double)heurUtil.get(i)/(double)ps.get(i);
				}
				else{
					ucount++;
					uMax += maxInvoke.get(i);
					uMin += minInvoke.get(i);
					uOpt += (double)optUtil.get(i)/(double)ps.get(i);
					uHeu += (double)heurUtil.get(i)/(double)ps.get(i);
				}
				
				
			}
		}
		
		if(mcount + ucount != 0 ){
			System.out.print("{\\bf " + ord + "} & " + Problem.roundFourDecimals((uMin + mMin)/(ucount + mcount)) + " - " + Problem.roundFourDecimals((uMax + mMax)/(ucount + mcount)));
			System.out.print(" & " + Problem.roundFourDecimals(100.0*(uOpt + mOpt)/(ucount + mcount)) + "\\% & " + Problem.roundFourDecimals(100.0*uOpt/ucount) + "\\% & " + Problem.roundFourDecimals(100.0*mOpt/mcount) + "\\%");
			System.out.print(" & " + Problem.roundFourDecimals(100.0*(uHeu + mHeu)/(ucount + mcount)) + "\\% & " + Problem.roundFourDecimals(100.0*uHeu/ucount) + "\\% & " + Problem.roundFourDecimals(100.0*mHeu/mcount) + "\\%");
			System.out.println(" \\\\ \\hline");
		}
	}
	
	private void account(File fi){
		Mapper m = new Mapper(fi.getAbsolutePath().substring(0, fi.getAbsolutePath().lastIndexOf(".")));
		String nm = fi.getName();
		multi.add((nm.startsWith("m")));
		scale.add(Integer.parseInt(nm.substring(nm.indexOf("s") + 1, nm.indexOf("x"))));
		
		double min = Double.MAX_VALUE;
		double max = 0.0;
		for(int act=0; act<m.prob.n; act++){
			for(int proc=0; proc < m.prob.p; proc++){
				min = Math.min(min, m.prob.invoke[act][proc]);
				max = Math.max(max, m.prob.invoke[act][proc]);
			}
		}
		minInvoke.add(min);
		maxInvoke.add(max);
		
		optUtil.add(utilisation(m.opt, m.prob.p));
		heurUtil.add(utilisation(m.heur, m.prob.p));
		
		ps.add(m.prob.p);
		
		
		
		System.out.print(fi.getName() + "\t" + multi.get(multi.size()-1) + ", " + scale.get(scale.size()-1) + ", " + minInvoke.get(minInvoke.size()-1) + ", " + maxInvoke.get(maxInvoke.size()-1));
		System.out.println(", " + optUtil.get(optUtil.size()-1) + ", " + heurUtil.get(heurUtil.size()-1) + ", " + ps.get(ps.size()-1));
	}
	
	private int utilisation(HashMap<Integer, Integer> map, int p){
		int[] count = new int[p];
		
		for(Integer i : map.keySet()){
			count[map.get(i)] = count[map.get(i)] + 1;
		}
		
		int c = 0;
		for(int i=0; i<count.length; i++){
			if(count[i] != 0) c++;
		}
		return c;
	}
	
}
