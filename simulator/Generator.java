package simulator;

import java.util.*;

public class Generator {
	
	public static final long[] REP_CHOICE = {1, 2, 3, 6};
	public static final long REP_TOKS = Fraction.lcmm(REP_CHOICE);
	
	public static void main(String[] args){
		System.out.println(generateSimulated(false, false, 5, 3));
		System.out.println("----------------------------------------");
		System.out.println(generateSimulated(false, false, 6, 3));
	}
	
	public static Topology2 generateSimulated(boolean unimod, boolean cyclic, int actors, int degree){
		return generateSimulated(unimod, cyclic, actors, degree, Topology2.INVOKE_COST, Topology2.INVOKE_RANGE);
	}
	
	public static Topology2 generateSimulated(boolean unimod, boolean cyclic, int actors, int degree, int minAffine, int affineRange){
		Actor[] tmp = new Actor[actors];
		long[] reps = new long[actors];
		for(int a=0; a<actors; a++){
			tmp[a] = new Actor(new Actor.Simulated());
			reps[a] = (unimod ? 1 : REP_CHOICE[(int)(Math.floor(Math.random()*REP_CHOICE.length))]);
		}
		
		int c = 0;
		for(int chn=0; chn<actors*degree/2; chn++){
			int prod = c;
			int cons = c;
			while(cons == prod) cons = (int)Math.floor(Math.random()*actors);
			
			if(!cyclic){
				if(prod > cons){//if the producer is lower down the topology, swap them, only when we are barring cycles
					int temp = prod;
					prod = cons;
					cons = temp;
				}
			}
			Channel newChan = new Channel(tmp[prod], (int)(unimod ? 1 : REP_TOKS/reps[prod]), tmp[cons], (int)(unimod ? 1 : REP_TOKS/reps[cons]));
			//System.out.println(tmp[prod].getName() + "->" + tmp[cons].getName());
			c = (c+1)%actors;
		}
		Topology2 ret = new Topology2(tmp[0]);
		ret.setAffinities(Problem.genAffinities(ret.actors.size(), minAffine, affineRange));
		ret.setCommunication(1);
		return ret;
	}
	
}
