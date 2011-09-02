package simulator;

import java.util.*;

public class Scheduler {
	
	public static final int LONGEST_SATURATE = 2000;
	
	public static ArrayList<Integer> saturate(Topology2 top){
		if(top.rep == null) return null;
		ArrayList<Integer> ret = new ArrayList<Integer>();
		int[] bufstates = new int[top.chans.size()];
		for(int c=0; c<bufstates.length; c++){
			bufstates[c] = top.chans.get(c).getBufferSize();
		}
		while(ret.size() < LONGEST_SATURATE){
			int nxt = getNextSaturate(top, bufstates);
			if(nxt == -2) return null;
			else if(nxt == -1) return ret;
			else{
				for(int cons = 0; cons < top.actors.get(nxt).numConsumptions(); cons++){
					Channel cchan = top.actors.get(nxt).getConsChannel(cons);
					int cidex = top.chanIdex(cchan);
					bufstates[cidex] = bufstates[cidex] - cchan.consamt;
				}
				for(int prod = 0; prod < top.actors.get(nxt).numProductions(); prod++){
					Channel pchan = top.actors.get(nxt).getProdChannel(prod);
					int pidex = top.chanIdex(pchan);
					bufstates[pidex] = bufstates[pidex] + pchan.prodamt;
				}
				ret.add(nxt);
			}
		}
		return null;
	}
	
	private static int getNextSaturate(Topology2 top, int[] bufs){
		boolean needed = false;
		for(int i=0; i<top.actors.size(); i++){
			if(!childrenSatisfied(top, i, bufs)){
				needed = true;
				if(canFire(top, i, bufs)) return i;
			}
		}
		if(needed) return -2;
		return -1;
	}
	
	private static boolean canFire(Topology2 top, int act, int[] bufs){
		Actor a = top.actors.get(act);
		for(int prod = 0; prod < a.numConsumptions(); prod++){
			Channel consumptor = a.getConsChannel(prod);
			if(bufs[top.chanIdex(consumptor)] < consumptor.consamt) return false;
		}
		return true;
	}
	
	private static boolean childrenSatisfied(Topology2 top, int act, int[] bufs){
		Actor a = top.actors.get(act);
		for(int chi = 0; chi < a.numProductions(); chi++){
			Channel chiline = a.getProdChannel(chi);
			int bufreq = chiline.consamt * top.rep[top.actIdex(chiline.consumer)];
			if(bufreq > bufs[top.chanIdex(chiline)]) return false;
		}
		return true;
	}
	
}
