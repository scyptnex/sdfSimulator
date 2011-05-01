package simulator.benchmarks;

import java.util.*;

import simulator.*;

public class AbstractExecutor {
	
	public static void NaiveExecute(Topology top, int periods) throws Exception{
		NaiveExecute(top, periods, new AbstractDelayConfig(top));
	}
	
	//executes at least once, even if reps is less than 1
	public static void NaiveExecute(Topology top, int periods, AbstractDelayConfig dels) throws Exception{
		ArrayList<Integer> order = new ArrayList<Integer>();
		
		//inject the delays
		//we only inject for the first repetition, its supposed to be steady state from here on
		for(int l=0; l<top.numLinks; l++){
			for(int d=0; d<dels.delayData.get(l).size(); d++){
				((AbstractLink)top.links[l]).add(dels.delayData.get(l).get(d));
			}
		}
		
		//make a new copy of the reps vector
		int[] curReps = new int[top.numActors];
		while(hasFiresLeft(top, curReps)){
			int curActor = -1;
			//first, find an actor which can execute and wont exceed the reps
			for(int a=0; a<top.numActors; a++){
				if(canFire((AbstractActor)top.actors[a]) && curReps[a] < top.repetitions[a]){
					curActor = a;
					break;
				}
			}
			//check this actor
			if(curActor == -1) throw new Exception("Deadlocked");
			order.add(curActor);
			curReps[curActor] = curReps[curActor] + 1;
			((AbstractActor)top.actors[curActor]).fire();
		}
		
		//execute the schedule we calculated above in the normal fashion
		for(int p=1; p<periods; p++){
			for(int act : order){
				((AbstractActor)top.actors[act]).fire();
			}
		}
	}
	
	public static boolean hasFiresLeft(Topology top, int[] reps){
		for(int i=0; i<top.numActors; i++){
			if(reps[i] < top.repetitions[i]) return true;
		}
		return false;
	}
	
	//this really should look at delayconfig
	public static boolean canFire(AbstractActor act){
		for(Topology.Link l : act.consumptions){
			AbstractLink al = (AbstractLink)l;
			if(al.fillState() < al.consumeAmmount) return false;
		}
		return true;
	}
	
}
