package simulator;

import java.util.*;

public class Topology2 {
	
	public final ArrayList<Actor> actors;
	public final ArrayList<Channel> chans;
	public final int[][] gamma;
	public final int[] rep;
	
	public Topology2(Actor someActor){
		Set<Actor> actSet = new HashSet<Actor>();
		Set<Channel> chanSet = new HashSet<Channel>();
		recursivelyAdd(someActor, actSet, chanSet);
		actors = new ArrayList<Actor>();
		chans = new ArrayList<Channel>();
		for(Actor a : actSet){
			actors.add(a);
		}
		for(Channel c : chanSet){
			chans.add(c);
		}
		
		int[][] tempGamma = new int[actors.size()][chans.size()];
		for(int c=0; c<chans.size(); c++){
			for(int a=0; a<actors.size(); a++){
				if(actors.get(a) == chans.get(c).producer){
					tempGamma[a][c] = chans.get(c).prodamt;
				}
				else if(actors.get(a) == chans.get(c).consumer){
					tempGamma[a][c] = -chans.get(c).consamt;
				}
				else{
					tempGamma[a][c] = 0;
				}
			}
		}
		gamma = tempGamma;
		
		int[] tempRep = null;
		try{
			Topology hackfix = new Topology(gamma);
			tempRep = hackfix.repetitions;
		}
		catch(Exception e){
			//do nothing
		}
		rep = tempRep;
	}
	
	public int actIdex(Actor a){
		int i = 0;
		for(Actor acts : actors){
			if(acts == a) return i;
			i++;
		}
		return -1;
	}
	
	public int chanIdex(Channel c){
		int i = 0;
		for(Channel cns : chans){
			if(cns == c) return i;
			i++;
		}
		return -1;
	}
	
	public String toString(){
		String ret = "Gamma = " + actors.size() + "x" + chans.size() + "\n";
		for(int a=0; a<actors.size(); a++){
			ret = ret + "{" + actors.get(a).name + "}\t";
		}
		ret += "\n";
		for(int a=0; a<actors.size(); a++){
			ret = ret + "x" + rep[a] + "\t";
		}
		for(int c=0; c<chans.size(); c++){
			ret = ret + "\n";
			for(int a=0; a<actors.size(); a++){
				ret = ret + gamma[a][c] + "\t";
			}
		}
		return ret;
	}
	
	private static void recursivelyAdd(Actor cur, Set<Actor> actSet, Set<Channel> chanSet){
		actSet.add(cur);
		for(int p=0; p<cur.numProductions(); p++){
			Channel can = cur.getProdChannel(p);
			if(!chanSet.contains(can)) chanSet.add(can);
			if(!actSet.contains(can.consumer)) recursivelyAdd(can.consumer, actSet, chanSet);
		}
		
		for(int c=0; c<cur.numConsumptions(); c++){
			Channel can = cur.getConsChannel(c);
			if(!chanSet.contains(can)) chanSet.add(can);
			if(!actSet.contains(can.producer)) recursivelyAdd(can.producer, actSet, chanSet);
		}
	}
	
}
