package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class NPM {
	
	public static final int NUM_AFFINITIES = 3;
	public static final int CPU_PWR = 5;
	public static final int ACT_COST = 30;
	public static final int COM_PWR = 4;
	public static final int TOK_COST = 8;
	public static final double INVOK_SCALE = 0.1;
	public static final double COMM_SCALE = 0.1;
	
	public Topology2 top;
	public final int numProcessors;
	
	public final int[][] pAffinities;
	public final int[][] aAffinities;
	
	public final double[][] invocationcosts;
	
	public static void main(String[] args){
		Topology2 top = Generator.generateSimulated(false, false, 16, 3);
		System.out.println(top);
		NPM npm = new NPM(top, 8);
		//ArrayList<Integer> isch = Scheduler.saturate(top);
		//System.out.println(isch.size());
		//for(int ai : isch){
			//top.actors.get(ai).invoke();
		//}
		//npm.printFillState();
	}
	
	public NPM(Topology2 topol, int pcount){
		top = topol;
		numProcessors = pcount;
		
		pAffinities = new int[numProcessors][NUM_AFFINITIES];
		aAffinities = new int[top.actors.size()][NUM_AFFINITIES];
		
		rgen(pAffinities, CPU_PWR);
		rgen(aAffinities, ACT_COST);
		
		invocationcosts = affine(pAffinities, aAffinities);
		
		Mapper.printProblem(System.out, numProcessors, invocationcosts, top);
		
		if(top.rep == null){
			//TODO handle this
		}
		
		
	}
	
	public void printFillState(){
		for(Channel cnl : top.chans){
			System.out.println(cnl.producer.getName() + " ---(" + cnl.getBufferSize() + ")--> " + cnl.consumer.getName());
		}
	}
	
	public static double[][] affine(int[][] proca, int[][] acta){
		double[][] ret = new double[acta.length][proca.length];
		for(int a=0; a<acta.length; a++){
			for(int p=0; p<proca.length; p++){
				double sm = 0;
				for(int i=0; i<NUM_AFFINITIES; i++){
					sm += (double)acta[a][i]/(double)proca[p][i];
				}
				ret[a][p] = sm*INVOK_SCALE;
			}
		}
		return ret;
	}
	
	public static void rgen(int[][] arr, int rng){
		for(int i=0; i<arr.length; i++){
			for(int j=0; j<arr[i].length; j++){
				arr[i][j] = rng + (int)Math.floor(Math.random()*rng);
			}
		}
	}
	
}
