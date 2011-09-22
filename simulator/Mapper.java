package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public abstract class Mapper {

	public static final int NUM_AFFINITIES = 3;
	//public static final int CPU_PWR = 5;
	//public static final int ACT_COST = 30;
	//public static final int COM_PWR = 4;
	//public static final int TOK_COST = 8;
	//public static final double INVOKE_SCALE = 0.1;
	//public static final double COMM_SCALE = 0.1;
	
	/**public static void main(String[] args){
		NPM.main(args);
	}
	
	public final int n;
	public final int p;
	public final Topology2 top;
	public final NPM mac;
	public final double[][] pi;//[actor][processor]
	public final double[][] ca;
	public final double[][] cp;
	
	public Mapper(Topology2 topolog, NPM machine){
		top = topolog;
		mac = machine;
		n = top.actors.size();
		p = machine.numProcessors;
		pi = affine(mac.pAffinities, top.affinities, INVOKE_SCALE);
		ca = top.communication;
		cp = mac.bandwidths;
		formulate();
	}
	
	public abstract void formulate();
	
	//-1 on failure or somesuch
	public abstract int map(int act);
	
	public static class Rounds extends Mapper{
		
		public HashMap<Integer, Integer> mapping;
		
		public Rounds(Topology2 top, NPM machine){
			super(top, machine);
			System.out.println("assignment cost: " + partialCost(mapping));
			for(int a=0; a<n; a++){
				for(int p=0; p<this.p; p++){
					System.out.print(pi[a][p] + "\t");
				}
				System.out.println(" - " + map(a));
			}
		}

		@Override
		public void formulate() {
			mapping = new HashMap<Integer, Integer>();
			for(int round=0; round<1; round++){//TODO multiple rounds
				for(int act=0; act<n; act++){
					
					if(mapping.containsKey(act)) mapping.remove(act);
					
					Set<Integer> duplocs = new HashSet<Integer>();
					for(int dup=0; dup < top.duplicates; dup++){
						int dupIndex = dup + (act/top.duplicates)*top.duplicates;
						if(dupIndex != act && mapping.containsKey(dupIndex)){
							duplocs.add(mapping.get(dupIndex));
						}
					}
					
					int bestproc = -1;
					double bestcost = 0.0;
					//check each processor for the best one to assign this actor to
					for(int trialproc = 0; trialproc<p; trialproc++){
						if(!duplocs.contains(trialproc)){//we can never assign to the same processor as one of the duplicates is on
							mapping.put(act, trialproc);
							double trialcost = partialCost(mapping);
							mapping.remove(act);
							if(bestproc == -1 || trialcost < bestcost){
								bestproc = trialproc;
								bestcost = trialcost;
							}
						}
					}
					if(bestproc != -1){
						mapping.put(act, bestproc);
					}
				}
			}
		}
		
		private double partialCost(HashMap<Integer, Integer> map){
			double cost = 0;
			for(Integer i : map.keySet()){
				cost = cost + pi[i][map.get(i)];
			}
			
			for(int a=0; a<n; a++){
				for(int b=a+1; b<n; b++){
					if(map.containsKey(a) && map.containsKey(b)){
						cost = cost + ca[a][b]*cp[map.get(a)][map.get(b)];
					}
				}
			}
			
			return cost;
		}

		@Override
		public int map(int act) {
			if(mapping.containsKey(act)) return mapping.get(act);
			return -1;
		}
	}
	
	public static class Optimal extends Mapper{
		
		public HashMap<Integer, Integer> mapping;
		
		public Optimal(Topology2 top, NPM machine){
			super(top, machine);
		}

		@Override
		public void formulate() {
			mapping = new HashMap<Integer, Integer>();
			try{
				File tmp = new File("mapdata.temp.dat");
				FileOutputStream fos = new FileOutputStream(tmp);
				PrintStream ps = new PrintStream(fos);
				//printProblem(System.out, this);
				ps.close();
				fos.close();
			}
			catch(IOException e){
				e.printStackTrace();
				mapping = null;
			}
		}

		@Override
		public int map(int act) {
			if(mapping == null) return -1;
			return mapping.get(act);
		}
	}
	**/
	public static int[][] genAffinities(int nbr, int min, int rng){
		int[][] ret = new int[nbr][];
		for(int i=0; i<nbr; i++){
			ret[i] = genAffinity(min, rng);
		}
		return ret;
	}
	public static int[] genAffinity(int min, int rng){
		int[] ret = new int[NUM_AFFINITIES];
		for(int i=0; i<NUM_AFFINITIES; i++){
			ret[i] = min + (int)Math.floor(Math.random()*rng);
		}
		return ret;
	}
	public static double[][] affine(int[][] proca, int[][] acta, double scale){
		double[][] ret = new double[acta.length][proca.length];
		for(int a=0; a<acta.length; a++){
			for(int p=0; p<proca.length; p++){
				double sm = 0;
				for(int i=0; i<NUM_AFFINITIES; i++){
					sm += (double)acta[a][i]/(double)proca[p][i];
				}
				ret[a][p] = roundFourDecimals(sm*scale);
			}
		}
		return ret;
	}
	public static double roundFourDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		return Double.valueOf(twoDForm.format(d));
	}
	
}
