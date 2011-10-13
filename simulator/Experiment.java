package simulator;

import java.io.*;
import java.util.*;

public class Experiment {
	
	public static final int NUM_EXPERIMENTS = 4;
	
	public static void main(String[] args){
		//generateExperiments();
		//miniExperiments();
		multiExperiments();
	}
	
	public static void multiExperiments(){
		File dir = expdir("multi", false);
		for(int n=3; n<=6; n++){
			int deg = n - 1 - (n-2)/3;
			System.out.println(n + ", " + deg);
			for(int s=0; s<2; s++){
				for(int i=0; i<4; i++){
					Topology2 basetop = Generator.generateSimulated(true, false, n, deg);
					NPM basemac = new NPM(5);
					Problem prob = new Problem(basetop, basemac, Problem.INVOKE_SCALE + s*0.2);
					prob.selfinvoke();
					String nm = "n" + n + "s" + s + "x" + i;
					prob.save(new File(dir, nm + "d1.exp"));
					for(int d=2; n*d <= 12; d++){
						Problem dupl = new Problem(prob, d);
						dupl.save(new File(dir, nm + "d" + d + ".exp"));
					}
				}
			}
		}
	}
	
	public static void miniExperiments(){
		File dir = expdir("exp", false);
		int[] degs = new int[]{2, 3, 4};
		for(int n=5; n<11; n++){
			for(int p=3; p<5; p++){
				for(int d=0; d<degs.length; d++){
					for(int s=0; s<2; s++){
						for(int i=0; i<2; i++){
							Topology2 top = Generator.generateSimulated(true, false, n, degs[d]);
							NPM mac = new NPM(p);
							Problem prob = new Problem(top, mac, Problem.INVOKE_SCALE + s*0.2);
							String nm = "n" + n + "p" + p + "x" + (degs.length*i + d) + ".exp";
							//System.out.println(nm);
							//File sv = new File(dir, "a" + nm);
							//System.out.println(dir.getAbsolutePath() + ", " + sv.getAbsolutePath());
							prob.save(new File(dir, "a" + nm));
							prob.selfinvoke();
							prob.save(new File(dir, "r" + nm));
						}
					}
				}
			}
		}
	}
	
	public static File expdir(String nm, boolean ts){
		Calendar c = Calendar.getInstance();
		String stamp = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
		String name = nm;
		if(ts) name = name + " " + stamp;
		File expdir = new File(name);
		if(!expdir.exists() && !expdir.mkdir()) return null;
		return expdir;
	}
	
	public static void generateExperiments(){
		File expdir = expdir("experiments", true);
		if(expdir == null) return;
		experimentBatch(expdir);
	}
	
	public static void experimentBatch(File dir){
		experimentBatch(dir, "small", 3, 5);
		experimentBatch(dir, "medium", 10, 20);
		//experimentBatch(dir, "large", 50, 100);
	}
	
	public static void experimentBatch(File dir, String size, int minact, int maxact){
		experimentBatch(dir, size, minact, maxact, "sparse", 2);
		experimentBatch(dir, size, minact, maxact, "normal", 1 + (int)Math.ceil(Math.log(minact)));
		experimentBatch(dir, size, minact, maxact, "dense", minact);
	}
	
	public static void experimentBatch(File dir, String size, int minact, int maxact, String conn, int degree){
		experimentBatch(dir, size, minact, maxact, conn, degree, "few", 3);
		experimentBatch(dir, size, minact, maxact, conn, degree, "some", 1 + 3*(int)Math.floor(Math.log(minact))*(int)Math.floor(Math.log(minact)));
		experimentBatch(dir, size, minact, maxact, conn, degree, "many", maxact);
	}
	
	public static void experimentBatch(File dir, String size, int minact, int maxact, String conn, int degree, String mach, int numproc){
		for(int i=0; i<NUM_EXPERIMENTS; i++){
			Topology2 top = Generator.generateSimulated((i%2 == 0), false, minact + (int)Math.floor(Math.random()*(maxact - minact)), degree);
			NPM mac = new NPM(numproc);
			experimentBatch(dir, size, conn, mach, top, mac, i+1);
		}
	}
	
	public static void experimentBatch(File dir, String size, String conn, String mach, Topology2 top, NPM mac, int batchno){
		experiment(dir, size, conn, mach, top, mac, batchno);
		if(mac.numProcessors >= 2) experiment(dir, size, conn, mach, new Topology2(top, 2), mac, batchno);
		if(mac.numProcessors >= 3) experiment(dir, size, conn, mach, new Topology2(top, 3), mac, batchno);
		if(mac.numProcessors >= 5) experiment(dir, size, conn, mach, new Topology2(top, 5), mac, batchno);
	}
	
	public static void experiment(File dir, String size, String conn, String mach, Topology2 top, NPM mac, int batchno){
		File loc = new File(dir, size.substring(0,1) + conn.substring(0,1) + mach.substring(0,1) + "." + top.duplicates + "." + batchno + ".exp");
		Problem p = new Problem(top, mac, Problem.INVOKE_SCALE);
		p.save(loc);
		//Filer.save(loc, top, mac);
		System.out.println("saved " + loc.getName());
	}
}
