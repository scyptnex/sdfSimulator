package simulator;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

public class Mapper {
	
	public final String file;
	
	public final Problem prob;
	
	public final HashMap<Integer, Integer> opt;
	public final HashMap<Integer, Integer> brute;
	public final HashMap<Integer, Integer> heur;
	
	public final double optCost;
	public final double bruteCost;
	public final double heurCost;
	
	public int heuristicRounds;
	
	public static void main(String[] args){
		if(args.length != 1){
			System.err.println("usage: java Mapper something.exp");
			System.exit(1);
		}
		
		File fi = new File(args[0]);
		if(!fi.exists() || !fi.getName().endsWith(".exp")){
			System.err.println("Argument makes no sense");
			System.exit(1);
		}
		
		Mapper.generateHeuristic(fi, 1);
		Mapper.generateHeuristic(fi, 2);
		Mapper.generateHeuristic(fi, 5);
	}
	
	public Mapper(String name){
		File expfile = new File(name + ".exp");
		File optfi = new File(name + ".opt");
		File heurfi = new File(name + ".heu");
		File brutefi = new File(name + ".bru");
		
		file = expfile.getAbsolutePath();
		
		prob = new Problem(expfile);
		
		opt = loadMapping(optfi);
		heur = loadHeuristic(heurfi);
		brute = loadMapping(brutefi);
		
		optCost = getCost(opt);
		heurCost = getCost(heur);
		bruteCost = getCost(brute);
	}
	
	public void report(){
		System.out.println("========== Mapping Report ==========");
		System.out.println(file);
		System.out.println(prob);
		System.out.println("====================================");
		System.out.println("Opt: (" + optCost + ") - " + (opt == null ? "[]" : mapString(opt)));
		System.out.println("Bru: (" + bruteCost + ") - " + (brute == null ? "[]" : mapString(brute)));
		System.out.println("Heu: (" + heurCost + ") - " + (heur == null ? "[]" : mapString(heur)));
	}
	
	public int[] getMap(HashMap<Integer, Integer> mapping){
		int[] ret = new int[prob.n];
		for(Integer act : mapping.keySet()){
			ret[act] = mapping.get(act);
		}
		return ret;
	}
	
	private HashMap<Integer, Integer> loadHeuristic(File fi){
		String nm = fi.getAbsolutePath();
		heuristicRounds = 0;
		for(int i=10; i>0; i--){
			String roundfi = nm.substring(0, nm.lastIndexOf(".")) + "." + i + ".heu";
			HashMap<Integer, Integer> mping = loadMapping(new File(roundfi));
			if(mping != null){
				heuristicRounds = i;
				return mping;
			}
		}
		return null;
	}
	
	private HashMap<Integer, Integer> loadMapping(File optfi){
		if(!optfi.exists()) return null;
		try{
			HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
			Scanner sca = new Scanner(optfi);
			Pattern p = Pattern.compile(".*x\\[[0-9]*,[0-9]*\\].*");
			while(sca.hasNextLine()){
				String ln = sca.nextLine();
				if(p.matcher(ln).matches()){
					ln = ln.substring(ln.indexOf("[") + 1);
					int act = Integer.parseInt(ln.substring(0, ln.indexOf(","))) - 1;
					ln = ln.substring(ln.indexOf(",") + 1);
					int proc = Integer.parseInt(ln.substring(0, ln.indexOf("]"))) - 1;
					ln = ln.substring(ln.indexOf("*") + 1).trim();
					boolean assigned = ln.charAt(0) == '1';
					if(assigned) ret.put(act, proc);
				}
			}
			sca.close();
			return ret;
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public double getCost(HashMap<Integer, Integer> mapping){
		return getCost(mapping, prob.invoke, prob.top.communication, prob.mac.bandwidths);
	}
	
	public static double getCost(HashMap<Integer, Integer> map, double[][] pi, double[][] ca, double[][] cp){
		double cost = 0;
		if(map == null) return cost;
		
		for(Integer i : map.keySet()){
			cost = cost + pi[i][map.get(i)];
		}
		for(int a=0; a<pi.length; a++){
			for(int b=a+1; b<pi.length; b++){
				if(map.containsKey(a) && map.containsKey(b)){
					cost = cost + ca[a][b]*cp[map.get(a)][map.get(b)];
				}
			}
		}
		
		return cost;
	}
	
	private static boolean saveMapping(Problem prob, File out, HashMap<Integer, Integer> mapping){
		try{
			//File svfile = ;
			PrintWriter pr = new PrintWriter(new FileWriter(out));
			for(int a=0; a<prob.n; a++){
				for(int p=0; p<prob.p; p++){
					pr.println("x[" + (a+1) + "," + (p+1) + "] * " + (mapping.containsKey(a) && mapping.get(a) == p ? "1" : "0") + " 0 1");
				}
			}
			pr.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean generateBrute(File experiment){
		Problem prob = new Problem(experiment);
		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		
		System.out.println("Brute force solvin " + experiment.getAbsolutePath());
		long time = System.currentTimeMillis();
		
		for(int i=0; i<prob.n; i++){
			mapping.put(i, 0);
		}
		
		boolean going = true;
		int[] best = null;
		double bestcost = -1;
		while(going){
			
			double cost = getCost(mapping, prob.invoke, prob.commu, prob.band);
			if(best == null || cost < bestcost){
				best = new int[prob.n];
				for(int i=0; i<best.length; i++){
					best[i] = mapping.get(i);
				}
				bestcost = cost;
			}
			
			for(int i=0; i<prob.n; i++){
				mapping.put(i, mapping.get(i)+1);
				if(mapping.get(i) >= prob.p){
					mapping.put(i, 0);
					if(i == prob.n-1) going = false;
				}
				else{
					break;//i = prob.n;
				}
			}
		}
		
		for(int i=0; i<prob.n; i++){
			mapping.put(i, best[i]);
		}
		
		System.out.println(" - completed in " + (((double)(System.currentTimeMillis()-time))/1000));
		return saveMapping(prob, new File(experiment.getAbsolutePath().substring(0, experiment.getAbsolutePath().lastIndexOf(".")) + ".bru"), mapping);
	}
	
	public static boolean generateHeuristic(File experiment, int rounds){
		Problem prob = new Problem(experiment);
		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		for(int round=0; round<rounds; round++){
			//System.out.println("ROUND " + round);
			for(int act=0; act<prob.n; act++){
				
				//System.out.print("A: " + act);
				
				if(mapping.containsKey(act)) mapping.remove(act);
				
				Set<Integer> duplocs = new HashSet<Integer>();
				for(int dup=0; dup < prob.top.duplicates; dup++){
					int dupIndex = dup + (act/prob.top.duplicates)*prob.top.duplicates;
					if(dupIndex != act && mapping.containsKey(dupIndex)){
						duplocs.add(mapping.get(dupIndex));
					}
				}
				
				int bestproc = -1;
				double bestcost = 0.0;
				//System.out.print("  Check: ");
				//check each processor for the best one to assign this actor to
				for(int trialproc = 0; trialproc<prob.p; trialproc++){
					if(!duplocs.contains(trialproc)){//we can never assign to the same processor as one of the duplicates is on
						mapping.put(act, trialproc);
						double trialcost = getCost(mapping, prob.invoke, prob.commu, prob.band);
						mapping.remove(act);
						//System.out.print(trialcost + ", ");
						if(bestproc == -1 || trialcost < bestcost){
							bestproc = trialproc;
							bestcost = trialcost;
						}
					}
				}
				if(bestproc != -1){
					//System.out.print("  Assign: " + bestproc);
					mapping.put(act, bestproc);
				}
				//System.out.println();
			}
		}
		return saveMapping(prob, new File(experiment.getAbsolutePath().substring(0, experiment.getAbsolutePath().lastIndexOf(".")) + "." + rounds + ".heu"), mapping);
	}
	
	public static String mapString(HashMap<Integer, Integer> mapping){
		StringBuffer ret = new StringBuffer("[");
		
		for(int i=0; i<mapping.size(); i++){
			ret.append(i + "-" + mapping.get(i) + ", ");
		}
		
		ret.append("]");
		return ret.toString();
	}
	
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
	
	
}
