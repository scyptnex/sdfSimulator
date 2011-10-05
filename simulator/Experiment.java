package simulator;

import java.io.*;
import java.util.*;

public class Experiment {
	
	public static final int NUM_EXPERIMENTS = 4;
	
	public static void main(String[] args){
		try{
			generateExperiments();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void generateExperiments() throws IOException{
		Calendar c = Calendar.getInstance();
		File expdir = new File("experiments " + c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
		if(! expdir.mkdir()) return;
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
