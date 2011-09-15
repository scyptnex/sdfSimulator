package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class NPM {
	
	public static final int CPU_POWER = Mapper.CPU_PWR;
	public static final int CPU_RANGE = Mapper.CPU_PWR;
	
	//public Topology2 top;
	public final int numProcessors;
	public final double[][] bandwidths;
	public final int[][] pAffinities;
	
	public static void main(String[] args){
		Topology2 top = Generator.generateSimulated(false, false, 4, 3);
		Topology2 top2 = new Topology2(top, 3);
		//System.out.println(top);
		NPM npm = new NPM(5);
		
		Mapper m = new Mapper.Rounds(top, npm);
		Mapper m2 = new Mapper.Rounds(top2, npm);
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
				bandwidths[p1][p2] = 1.0 + Math.random();
				bandwidths[p2][p1] = bandwidths[p1][p2];
				if(p1 == p2) bandwidths[p1][p2] = 0;//TODO zero cost?
			}
		}
		pAffinities = Mapper.genAffinities(numProcessors, CPU_POWER, CPU_RANGE);
		
	}
	
	public void printFillState(){
		
	}
	
}
