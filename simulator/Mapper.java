package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public abstract class Mapper {

	public static final int NUM_AFFINITIES = 3;
	public static final int CPU_PWR = 5;
	public static final int ACT_COST = 30;
	public static final int COM_PWR = 4;
	public static final int TOK_COST = 8;
	public static final double INVOKE_SCALE = 0.1;
	public static final double COMM_SCALE = 0.1;
	
	public static void main(String[] args){
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
				printProblem(System.out, this);
				ps.close();
				fos.close();
			}
			catch(IOException e){
				e.printStackTrace();
				mapping = null;
			}
		}
		
		public static void printProblem(PrintStream out, Mapper m) throws IOException{
			out.println("data;");
			out.println("\nparam n := " + m.top.actors.size() + ";");
			out.println("\nparam p := " + m.p + ";");
			/**out.println("\nparam e := " + top.chans.size() + ";");
			
			out.print("\nset EDJ :=");
			int[] incidences = new int[top.actors.size()];
			for(int c=0; c<top.chans.size(); c++){
				Channel cur = top.chans.get(c);
				int pro = top.actIdex(cur.producer);
				int con = top.actIdex(cur.consumer);
				incidences[pro] = incidences[pro] + 1;
				incidences[con] = incidences[con] + 1;
				out.print(" (" + (pro+1) + "," + (con+1) + ")");
			}
			out.println(";");**/
			
			out.println("param AG :");
			for(int i=0; i<m.n; i++){
				out.print("\t" + (i+1));
			}
			out.println(" :=");
			for(int i=0; i<m.n; i++){
				out.print((i+1));
				for(int j=0; j<m.n; j++){
					//this sequence works even when there is 1 dup
					out.print("\t" + (m.top.isdup(i, j) ? "1" : "0"));
				}
				out.println();
			}
			out.println(";");
			
			//out.println();
			//printArray(out, "PD", incidences);
			//out.println();
			//printArray(out, "AG", new double[top.actors.size()][top.actors.size()]);
			out.println();
			printArray(out, "PI", m.pi);
			out.println();
			printArray(out, "CA", m.ca);
			out.println();
			printArray(out, "CP", m.cp);
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
					out.print("\t" + roundFourDecimals(arr[r][c]));
				}
				out.println();
			}
			out.println(";");
		}

		@Override
		public int map(int act) {
			if(mapping == null) return -1;
			return mapping.get(act);
		}
	}
	
	
	//stolen froma forum http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
	private static double roundFourDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		return Double.valueOf(twoDForm.format(d));
	}
	
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
	private static double[][] affine(int[][] proca, int[][] acta, double scale){
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
	
}
