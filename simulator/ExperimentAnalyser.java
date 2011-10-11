package simulator;

import java.io.*;
import java.util.*;

public class ExperimentAnalyser {
	
	public ArrayList<Double> optimalCosts;
	public ArrayList<Double> heuristicCosts;
	public ArrayList<Double> comparative;
	public ArrayList<Integer> ns;
	public ArrayList<Integer> ps;
	
	public int pmin;
	public int pmax;
	public int nmin;
	public int nmax;
	
	public static void main(String[] args){
		if(args.length != 1){
			args = new String[]{"."};
		}
		ExperimentAnalyser ea = new ExperimentAnalyser();
		
		for(int i=0; i<args.length; i++){
			File dir = new File(args[i]);
			if(!dir.exists() || !dir.isDirectory()){
				System.err.println("failure: directory " + args[i] + " isnt there");
				System.exit(1);
			}
			
			
			String[] fls = dir.list();
			for(int fil=0; fil<fls.length; fil++){
				if(fls[fil].endsWith(".exp")){
					ea.experiment(new File(dir, fls[fil]));
				}
			}
		}
		
		ea.analyse();
	}
	
	public ExperimentAnalyser(){
		optimalCosts = new ArrayList<Double>();
		heuristicCosts = new ArrayList<Double>();
		comparative = new ArrayList<Double>();
		ns = new ArrayList<Integer>();
		ps = new ArrayList<Integer>();
		pmin = Integer.MAX_VALUE;
		pmax = 0;
		nmin = Integer.MAX_VALUE;
		nmax = 0;
	}
	
	public void analyse(){
		subanalyse(nmin, nmax, pmin, pmax);
		rangeAnalyse(4);
	}
	
	private void rangeAnalyse(int grads){
		double gradat = (double)(nmax - nmin);
		gradat = gradat/grads;
		System.out.println(gradat);
		
		for(int g=0; g<grads; g++){
			double lbound  = nmin + g*gradat;
			double ubound = nmin + (g+1)*gradat;
			
			int tot = 0;
			double totalOptimal = 0.0;
			double totalHeuristic = 0.0;
			double totalComparative = 0.0;
			
			int divergents = 0;
			double divOpt = 0.0;
			double divHeu = 0.0;
			double divComp = 0.0;
			
			
			
			for(int i=0; i<optimalCosts.size(); i++){
				if(ns.get(i) <= ubound && (ns.get(i) > lbound || (ns.get(i) == lbound && g == 0))){
					tot++;
					totalOptimal += optimalCosts.get(i);
					totalHeuristic += heuristicCosts.get(i);
					totalComparative += comparative.get(i);
					
					if(!optimalCosts.get(i).equals(heuristicCosts.get(i))){
						divergents++;
						divOpt += optimalCosts.get(i);
						divHeu += heuristicCosts.get(i);
						divComp += comparative.get(i);
					}
				}
			}
			
			System.out.print(lbound + " - " + ubound + "(" + tot + "): ");
			System.out.print("opt " + totalOptimal/tot + " heu " + totalHeuristic/tot + " comp " + totalComparative/tot);
			System.out.println();
			System.out.println("\t" + "opt " + divOpt/divergents + " heu " + divHeu/divergents + " comp " + divComp/divergents);
		}
	}
	
	//min is inclusive max is exclusive
	private void subanalyse(int nmin, int nmax, int pmin, int pmax){
		double totalOptimal = 0.0;
		double totalHeuristic = 0.0;
		double totalComparative = 0.0;
		
		int divergents = 0;
		double divOpt = 0.0;
		double divHeu = 0.0;
		double divComp = 0.0;
		
		for(int i=0; i<optimalCosts.size(); i++){
			if(ps.get(i) >= pmin && ns.get(i) >= nmin && ps.get(i) < pmax && ns.get(i) < nmax)
			totalOptimal += optimalCosts.get(i);
			totalHeuristic += heuristicCosts.get(i);
			totalComparative += comparative.get(i);
			
			if(!optimalCosts.get(i).equals(heuristicCosts.get(i))){
				divergents++;
				divOpt += optimalCosts.get(i);
				divHeu += heuristicCosts.get(i);
				divComp += comparative.get(i);
			}
		}
		
		System.out.println("Range: p(" + pmin + "-" + pmax + "), n(" + nmin + "-" + nmax + ")");
		System.out.println("\tTest cases: " + optimalCosts.size());
		System.out.println("\t - Avg heurisitic: " + totalHeuristic/optimalCosts.size());
		System.out.println("\t - Avg optimal: " + totalOptimal/optimalCosts.size());
		System.out.println("\t - Comparative: " + totalComparative/optimalCosts.size());
		
		System.out.println("\tDivergents: " + divergents);
		if(divergents != 0){
			System.out.println("\t - Avg heurisitic: " + divHeu/divergents);
			System.out.println("\t - Avg optimal: " + divOpt/divergents);
			System.out.println("\t - Comparative: " + divComp/divergents);
		}
	}
	
	public void experiment(File expfi){
		String batch = expfi.getAbsolutePath().substring(0, expfi.getAbsolutePath().lastIndexOf("."));
		Mapper m = new Mapper(batch);
		if(m.optCost != 0.0 && m.heurCost != 0.0){
			optimalCosts.add(m.optCost);
			heuristicCosts.add(m.heurCost);
			comparative.add(m.heurCost/m.optCost);
			ns.add(m.prob.n);
			ps.add(m.prob.p);
			
			if(m.prob.n < 5){
				m.report();
			}
			
			pmin = Math.min(pmin, m.prob.p);
			nmin = Math.min(nmin, m.prob.n);
			pmax = Math.max(pmax, m.prob.p);
			nmax = Math.max(nmax, m.prob.n);
		}
	}
	
}
