package simulator;

import java.util.*;

public class Topology2 {
	
	public static final int INVOKE_COST = 2*NPM.CPU_POWER;
	public static final int INVOKE_RANGE = 10*NPM.CPU_RANGE;
	
	public final ArrayList<Actor> actors;
	public final ArrayList<Channel> chans;
	public final int duplicates;//how many versions of each actor are there
	public final int[][] gamma;
	public final int[] rep;
	public int[][] affinities;
	public double[][] communication;
	
	public static void main(String[] args){
		Topology2 top = Generator.generateSimulated(false, false, 4, 2);
		System.out.println(top);
		Topology2 top2 = new Topology2(top, 2);
		System.out.println(top2);
	}
	
	public Topology2(int[][] gam, double[][] comm, int dups, int[][] affine){
		duplicates = dups;
		gamma = gam;
		rep = this.getRep();
		affinities = affine;
		communication = comm;
		actors = new ArrayList<Actor>();
		chans = new ArrayList<Channel>();
		for(int i=0; i<gamma.length; i++){
			actors.add(Actor.simulated());
		}
		if(actors.size() > 0){
			for(int c=0; c<gamma[0].length; c++){
				int prod = -1;
				int con = -1;
				int pamt = 0;
				int camt = 0;
				for(int a=0; a<gamma.length; a++){
					if(gamma[a][c] > 0){
						prod = a;
						pamt = gamma[a][c];
					}
					else if(gamma[a][c] < 0){
						con = a;
						camt = -gamma[a][c];
					}
				}
				if(prod != -1 && con != -1){
					chans.add(new Channel(actors.get(prod), pamt, actors.get(con), camt));
				}
			}
		}
	}
	
	public Topology2(Topology2 base, int dups){
		actors = new ArrayList<Actor>(base.actors.size()*dups);
		chans = new ArrayList<Channel>();
		duplicates = dups;
		affinities = null;
		communication = null;
		
		for(int i=0; i<base.actors.size(); i++){
			for(int d=0; d<duplicates; d++){
				actors.add(new Actor(base.actors.get(i).method));//TODO this should be the duplicate actor
			}
		}
		
		for(Channel c : base.chans){
			int prbi = base.actIdex(c.producer);
			int cobi = base.actIdex(c.consumer);
			for(int produp=0; produp < duplicates; produp++){
				for(int condup=0; condup < duplicates; condup++){
					int prodex = produp + prbi*duplicates;
					int condex = condup + cobi*duplicates;
					chans.add(new Channel(actors.get(prodex), c.prodamt, actors.get(condex), c.consamt));
				}
			}
		}
		
		if(base.affinities != null){
			affinities = new int[base.actors.size()*dups][Mapper.NUM_AFFINITIES];
			for(int i=0; i<affinities.length; i++){
				for(int a=0; a<Mapper.NUM_AFFINITIES; a++){
					affinities[i][a] = base.affinities[i/duplicates][a];
				}
			}
		}
		
		if(base.communication != null){
			communication = new double[base.actors.size()*dups][base.actors.size()*dups];
			for(int p=0; p<communication.length; p++){
				for(int c=0; c<communication[p].length; c++){
					int basep = p/duplicates;
					int basec = c/duplicates;
					communication[p][c] = base.communication[basep][basec];
				}
			}
		}
		
		gamma = getGamma();
		rep = getRep();
		
		
		
	}
	
	public Topology2(Actor someActor){
		Set<Actor> actSet = new HashSet<Actor>();
		Set<Channel> chanSet = new HashSet<Channel>();
		recursivelyAdd(someActor, actSet, chanSet);
		actors = new ArrayList<Actor>();
		chans = new ArrayList<Channel>();
		duplicates = 1;
		for(Actor a : actSet){
			actors.add(a);
		}
		for(Channel c : chanSet){
			chans.add(c);
		}
		
		gamma = getGamma();
		rep = getRep();
		affinities = null;
		communication = null;
	}
	
	public boolean isdup(int i, int j){
		if(i >= actors.size() || j >= actors.size()){
			return false;
		}
		return ((i != j) && (i/duplicates == j/duplicates));
	}
	
	public boolean setAffinities(int[][] affins){
		if(affins.length != actors.size() || affins[0].length != Mapper.NUM_AFFINITIES){
			affinities = null;
			return false;
		}
		affinities = affins;
		return true;
	}
	public boolean setCommunication(double pertoken){
		double[] arg = new double[chans.size()];
		for(int i=0; i<arg.length; i++){
			arg[i] = pertoken;
		}
		return setCommunication(arg);
	}
	public boolean setCommunication(double[] channelPerToken){
		if(channelPerToken == null || channelPerToken.length != chans.size()){
			communication = null;
			return false;
		}
		communication = new double[actors.size()][actors.size()];
		int cnum = 0;
		for(Channel c : chans){
			int pidex = this.actIdex(c.producer);
			int cidex = this.actIdex(c.consumer);
			double namt = c.prodamt*rep[pidex]*channelPerToken[cnum];
			communication[pidex][cidex] = communication[pidex][cidex] + namt;
			communication[cidex][pidex] = communication[cidex][pidex] + namt;
			cnum++;
		}
		return true;
	}
	
	private final int[][] getGamma(){
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
		return tempGamma;
	}
	
	public final int[] getRep(){
		int[] tempRep = null;
		try{
			Topology hackfix = new Topology(gamma);
			tempRep = hackfix.repetitions;
		}
		catch(Exception e){
			//do nothing
		}
		return tempRep;
	}
	
	public int actIdex(Actor a){
		return actors.indexOf(a);
	}
	
	public int chanIdex(Channel c){
		return chans.indexOf(c);
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
		StringBuffer sb = new StringBuffer();
		for(Channel c : chans){
			sb.append(c.producer.getName() + "--[" + c.prodamt + ", " + c.consamt + "]-->" + c.consumer.getName() + "\n");
		}
		sb.append(ret);
		
		if(communication != null){
			for(int i=0; i<actors.size(); i++){
				sb.append("\n| ");
				for(int j=0; j<actors.size(); j++){
					sb.append(communication[i][j] + "\t");
				}
				if(affinities != null){
					sb.append("[");
					for(int a=0; a<affinities[i].length; a++){
						sb.append(affinities[i][a] + ",");
					}
					sb.append("]");
				}
			}
		}
		
		return sb.toString();
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
