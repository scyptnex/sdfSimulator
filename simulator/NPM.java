package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class NPM {
	
	public static final int CPU_POWER = 10;
	public static final int CPU_RANGE = 3;
	
	//public Topology2 top;
	public final int numProcessors;
	public final double[][] bandwidths;
	public final int[][] pAffinities;
	
	public static void main(String[] args){
		Topology2 top = Generator.generateSimulated(false, false, 4, 3);
		Topology2 top2 = new Topology2(top, 3);
		//System.out.println(top);
		NPM npm = new NPM(5);
		
		Filer.save(new File("test1"), top, npm);
		Filer.save(new File("test2"), top2, npm);
		//Filer.save(new File("test3"), top2, npm);
		
		//Mapper m = new Mapper.Rounds(top, npm);
		//Mapper m2 = new Mapper.Rounds(top2, npm);
		//Mapper m3 = new Mapper.Optimal(top, npm);
		//Mapper m2 = new Mapper.Rounds(top.actors.size(), npm.numProcessors, top, npm.invocationcosts, npm.communications, npm.bandwidths);
		//ArrayList<Integer> isch = Scheduler.saturate(top);
		//System.out.println(isch.size());
		//for(int ai : isch){
			//top.actors.get(ai).invoke();
		//}
		//npm.printFillState();
	}
	
	public NPM(int pcount){
		numProcessors = pcount;
		bandwidths = new double[numProcessors][numProcessors];
		for(int p1=0; p1<numProcessors; p1++){
			for(int p2=p1; p2<numProcessors; p2++){
				bandwidths[p1][p2] = Mapper.roundFourDecimals(1.0 + Math.random());
				bandwidths[p2][p1] = bandwidths[p1][p2];
				if(p1 == p2) bandwidths[p1][p2] = 0;//TODO zero cost?
			}
		}
		pAffinities = Mapper.genAffinities(numProcessors, CPU_POWER, CPU_RANGE);
	}
	
	public NPM(int pc, double[][] band, int[][] affine){
		numProcessors = pc;
		bandwidths = band;
		pAffinities = affine;
	}
	
	public String toString(){
		StringBuffer ret = new StringBuffer("Machine(" + numProcessors + ")");
		ret.append("\nBand:");
		for(int y=0; y<numProcessors; y++){
			ret.append("\n");
			for(int x=0; x<numProcessors; x++){
				ret.append(" " + bandwidths[x][y]);
			}
		}
		ret.append("\nAffine:");
		for(int p=0; p<numProcessors; p++){
			ret.append("\n");
			for(int a=0; a<Mapper.NUM_AFFINITIES; a++){
				ret.append(" " + pAffinities[p][a]);
			}
		}
		return ret.toString();
	}
	
	public void printFillState(){
		
	}
	
}
