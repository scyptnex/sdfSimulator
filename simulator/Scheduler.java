package simulator;

public class Scheduler {
	
	//returns an array of length top.numActors with an index for which processor(s) an actor lives on
	//return array is NOT necessarily square
	//actor-processor[], i.e. if actor 0 is on processor 2 and actor 1 is on processors 0 and 1:
	//	ret[0][0] = 2
	//	ret[1][0] = 0
	//	ret[1][1] = 1
	//actors are only duplicated for the static parallel robustness method
	public int[][] schedule(Topology top, MachineSpecification mac){
		return null;
	}
	
	//returns the number of invocations required to init the schedule
	//this can only be worked out once we have an assignment
	// - each index of ret is the actor to be fired next (on whatever processor it resides
	// - i.e. if init is 1-1-2-0 then ret = {1, 1, 2, 0}
	public int[] initSchedule(int[][] assignment){
		return null;
	}
	
	//as above, for the death schedule
	public int[] deathSchedule(int[][] assignment){
		return null;
	}
	
}