package simulator;

import java.util.*;
import java.io.*;

public class Simulator {
	
	public static final File CHECKPOINT_DIR = getCheckpointDir();
	
	public static final int FAIL_NONE = 0;
	public static final int FAIL_IMPENDING = 1;
	public static final int FAIL_DONE = 2;
	
	public final boolean tolerant;
	public final Problem prob;
	public final int[] mapping;
	public final Machine[] machs;
	
	public static File getCheckpointDir(){
		File ret = new File("checkpoints");
		if(!ret.exists()) ret.mkdir();
		return ret;
	}
	
	public static void main(String[] args){
		Mapper m = new Mapper("beta");
		//m.report();
		//System.out.println(m.prob);
		//System.out.println(m.heurCost + ", " + m.heuristicRounds);
		//System.out.println(m.optCost);
		
		//int[] map = new int[m.prob.n];
		//for(int i=0; i<map.length; i++){
		//	map[i] = i%m.prob.p;
		//}
		
		Simulator sim = new Simulator(m.prob, m.getMap(m.opt), false);
		sim.simulate(4);
	}
	
	public Simulator(Problem p, int[] map, boolean tolerance){
		prob = p;
		tolerant = tolerance;
		mapping = map;
		ArrayList<Integer> init = Scheduler.saturate(p.top);
		
		for(Integer i : init){
			invokeActor(i);
		}
		
		machs = new Machine[prob.p];
		
		for(int proc=0; proc<prob.p; proc++){
			ArrayList<Integer> thisproc = new ArrayList<Integer>();
			ArrayList<Integer> reps = new ArrayList<Integer>();
			for(int a=0; a<mapping.length; a++){
				if(mapping[a] == proc){
					thisproc.add(a);
					reps.add(prob.top.rep[a]);
					
				}
			}
			machs[proc] = new Machine(proc, thisproc, reps, this);
		}
		
		System.out.println(prob.top.fillState());
	}
	
	//duration in number of rounds
	public void simulate(int duration){
		
		for(int ss=0; ss<duration; ss++){
			System.out.println("-- Steady State " + ss + " --");
			for(int p=0; p<prob.p; p++){
				machs[p].saveBuffers(ss);
			}
			//System.out.println(prob.top.fillState());
			//if(ss == 1){
				//machs[mapping[0]].queueFailure();
			//}
			for(int p=0; p<prob.p; p++){
				//System.out.println("starting " + p);
				machs[p].steadyState();
			}
			for(int p=0; p<prob.p; p++){
				machs[p].steadyStateFinish();
				//System.out.println("ended " + p);
			}
		}
	}
	
	public synchronized void invokeActor(int actor){
		Actor act = prob.top.actors.get(actor);
		ArrayList<Object[]> in = new ArrayList<Object[]>();
		ArrayList<Object[]> out = new ArrayList<Object[]>();
		
		for(int i=0; i<act.numConsumptions(); i++){
			Object[] cons = new Object[act.getConsChannel(i).consamt];
			for(int t=0; t<cons.length; t++){
				cons[t] = act.getConsChannel(i).eat();
			}
			in.add(cons);
		}
		
		for(int o=0; o<act.numProductions(); o++){
			out.add(new Object[act.productionSize(o)]);
		}
		//System.out.println("Invoking " + act.getName());
		int ret = act.method.invoke(act.getName(), in, out);
		
		if(ret == Action.RETURN_FAIL){
			System.out.println("INVOCATION FAIIIIIIILURE");
		}
		else{
			for(int p=0; p<act.numProductions(); p++){
				act.getProdChannel(p).produce(out.get(p));
			}
			if(ret == Action.RETURN_DETECT){
				System.out.println("## Fault Detected");
			}
		}
	}
	
	public synchronized void call(String message){
		System.out.println(message);
	}
	
	private class Machine{
		
		public final int idex;
		public final Simulator parent;
		public final int[] myActors;
		public final int[] myReps;
		public int failure;
		private Thread exec;
		private final ArrayList<Integer> myChannels;
		
		public Machine(int idx, ArrayList<Integer> actors, ArrayList<Integer> reps, Simulator sim){
			idex = idx;
			parent = sim;
			myActors = new int[actors.size()];
			myReps = new int[myActors.length];
			myChannels = new ArrayList<Integer>();
			
			for(int i=0; i<myActors.length; i++){
				myActors[i] = actors.get(i);
				myReps[i] = reps.get(i);
				for(int cons=0; cons < parent.prob.top.actors.get(myActors[i]).numConsumptions(); cons++){
					Channel c = parent.prob.top.actors.get(myActors[i]).getConsChannel(cons);
					myChannels.add(parent.prob.top.chanIdex(c));
				}
			}
			
			failure = FAIL_NONE;
			System.out.println("initialize machine " + idex + " for actors " + actors + " repeating " + reps + " and channels " + myChannels);
		}
		
		public void queueFailure(){
			failure = FAIL_IMPENDING;
		}
		
		public boolean loadBuffers(int callnum){
			File svfi = new File(CHECKPOINT_DIR, idex + "." + callnum + ".chk");
			if(!svfi.exists()) return false;
			//TODO this
			return true;
		}
		
		public boolean saveBuffers(int callnum){
			File svfi = new File(CHECKPOINT_DIR, idex + "." + callnum + ".chk");
			try{
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(svfi));
				for(int i=0; i<myChannels.size(); i++){
					Channel cur = parent.prob.top.chans.get(i);
					Object[] ss = cur.snapshot();
					oos.writeObject(ss);
				}
				oos.close();
				return true;
			}
			catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}
		
		public void steadyState(){
			//call("executing a steady state on processor " + idex);
			final int thisfailure = failure;
			exec = new Thread(){
				public void run(){
					if(thisfailure == FAIL_DONE){
						System.out.println("processor " + idex + " is dead");
					}
					else{
						int alen = myActors.length;
						if(thisfailure == FAIL_IMPENDING){
							alen = (int)Math.floor(Math.random()*alen);
						}
						for(int a=0; a<alen; a++){
							int rlen = myReps[a];
							if(thisfailure == FAIL_IMPENDING){
								rlen = (int)Math.floor(Math.random()*rlen);
							}
							for(int r=0; r<rlen; r++){
								//call("invoke: " + System.currentTimeMillis());
								invokeActor(myActors[a]);
								Thread.yield();
							}
						}
						if(thisfailure == FAIL_IMPENDING){
							System.out.println("## " + idex + "> Farewell cruel world!");
							failure = FAIL_DONE;
						}
					}
				}
			};
			exec.start();
		}
		public void steadyStateFinish(){
			try {
				exec.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
