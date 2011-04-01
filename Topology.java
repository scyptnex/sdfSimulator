import java.util.*;
import java.io.*;

public class Topology {
	
	public final int numActors;
	public final int numLinks;
	
	public final int[][] matrix;//matrix[actor][link]
	
	public final Actor[] actors;
	
	public final int[] repetitions;
	
	public static Topology loadTopology(Scanner top) throws InvalidTopologyException, NoValidScheduleException {
		int actors = Integer.parseInt(top.nextLine());
		int links = Integer.parseInt(top.nextLine());
		int[][] topolog = new int[actors][links];
		for(int l=0; l<links; l++){
			for(int a=0; a<actors; a++){
				topolog[a][l] = top.nextInt();
			}
		}
		return new Topology(topolog);
	}
	
	/**public Topology(int act, int link){
		numActors = act;
		numLinks = link;
		matrix = new int[numActors][numLinks];
		
		repetitions = calcRepetitionVector();
	}**/
	
	public Topology(int[][] mat) throws InvalidTopologyException, NoValidScheduleException{
		matrix = mat;
		numActors = matrix.length;
		numLinks = (numActors > 0 ? matrix[0].length : 0);

		actors = new Actor[numActors];
		for(int a=0; a<numActors; a++) actors[a] = new Actor(a);
		for(int l=0; l<numLinks; l++){
			int prod = -1;
			int con = -1;
			int a = 0;
			while((prod == -1 || con == -1) && a < numActors){
				if(matrix[a][l] < 0) con = a;
				if(matrix[a][l] > 0) prod = a;
				a++;
			}
			if(prod == -1 || con == -1){
				throw new InvalidTopologyException("Link " + l + " does not have valid producers/consumers");
			}
			new Link(actors[prod], matrix[prod][l], actors[con], -matrix[con][l]);//links show consumption as a positive integer, so must negate the value in topology matrix
		}
		
		repetitions = calcRepetitionVector();
	}
	
	private void setReps(Fraction[] rv, int act, Fraction rep){
		//naive non-pre-sorted operation, O(n^3)
		//will have to re-do this when there's time
		rv[act] = rep;
		
		for(int l=0; l<numLinks; l++){
			if(matrix[act][l] < 0){
				//System.out.println(act + " has a link on edge " + l);
				for(int a=0; a<numActors; a++){
					if(matrix[a][l] > 0){
						Fraction newRep = rep.times(-matrix[a][l]).dividedBy(matrix[act][l]);
						//System.out.println("Repeating link " + a + "(" + rv[a] + ") with rep " + newRep + ": " + matrix[a][l] + " " + matrix[act][l]);
						//System.exit(0);
						if(rv[a].equals(0)) setReps(rv, a, newRep);
						break;
					}
				}
			}
		}
		for(int l=0; l<numLinks; l++){
			if(matrix[act][l] > 0){
				for(int a=0; a<numActors; a++){
					if(matrix[a][l] < 0){
						Fraction newRep = rep.times(-matrix[act][l]).dividedBy(matrix[a][l]);
						//System.out.println("Repeating link " + a + "(" + rv[a] + ") with rep " + newRep + ": " + matrix[a][l] + " " + matrix[act][l]);
						if(rv[a].equals(0)) setReps(rv, a, newRep);//inverse of above
						break;
					}
				}
			}
		}
	}
	
	private int[] calcRepetitionVector() throws NoValidScheduleException{
		if(numActors == 0) return null;
		Fraction[] reps = new Fraction[numActors];
		for(int i=0; i<numActors; i++) reps[i] = new Fraction(0, 1);
		setReps(reps, 0, new Fraction(1, 1));
		long[] denoms = new long[numActors];
		for(int i=0; i<numActors; i++){
			denoms[i] = reps[i].denominator();
		}
		long lcm = Fraction.lcmm(denoms);
		
		int[] ret = new int[numActors];
		for(int i=0; i<numActors; i++){
			ret[i] = (int)reps[i].times(lcm).numerator();
			//System.out.println(i + " = " + ret[i]);
		}
		for(int l=0; l<numLinks; l++){
			int prod = 0;
			int cons = 0;
			for(int a=0; a<numActors; a++){
				if(matrix[a][l] > 0){
					prod = matrix[a][l] * ret[a];
					//System.out.println(a + "-" + l + " produces " + prod);
				}
				if(matrix[a][l] < 0){
					cons = -matrix[a][l] * ret[a];
					//System.out.println(a + "-" + l + " consumes " + prod);
				}
			}
			if(prod == 0 || prod != cons){
				throw new NoValidScheduleException("Inconsistant repetitions on link " + l);
			}
		}
		return ret;
	}
	
	public String toString(){
		String ret = "Gamma = " + numLinks + " x " + numActors;
		if(repetitions != null){
			ret += "\n= ";
			for(int a=0; a<numActors; a++){
				ret += repetitions[a] + "\t";
			}
			ret += "=";
		}
		for(int l=0; l<numLinks; l++){
			ret += "\n| ";
			for(int a=0; a<numActors; a++){
				ret += matrix[a][l] + "\t";
			}
			ret += "|";
		}
		for(int a=0; a<actors.length; a++){
			ret += "\n" + actors[a].toString();
		}
		return ret;
	}
	
	//the actors' links start empty and are added
	public static class Actor{
		public final int myIndex;
		public final ArrayList<Link> productions;//links which i produce for
		public final ArrayList<Link> consumptions;//links which i consume from
		
		public Fraction repetitions;
		
		public Actor(int idex){
			myIndex = idex;
			productions = new ArrayList<Link>();
			consumptions = new ArrayList<Link>();
			repetitions = new Fraction(0, 1);
		}
		
		public String toString(){
			String ret = "Actor " + myIndex + " (r= " + repetitions + ")\n" + productions.size() + " productions";
			for(Link l : productions) ret += "\n  " + l.toString();
			ret += "\n" + consumptions.size() + " consumptions";
			for(Link l : consumptions) ret += "\n  " + l.toString();
			return ret;
		}
	}
	
	//Links automatically add themselves to the actors they link
	public static class Link{
		public final Actor producer;
		public final int produceAmmount;
		
		public final Actor consumer;
		public final int consumeAmmount;
		
		public Link(Actor prod, int pamt, Actor con, int camt){
			producer = prod;
			produceAmmount = pamt;
			
			consumer = con;
			consumeAmmount = camt;
			
			producer.productions.add(this);
			consumer.consumptions.add(this);
		}
		
		public String toString(){
			return producer.myIndex + "(" + produceAmmount + ") -> " + consumer.myIndex + "(" + consumeAmmount + ")";
		}
	}
	
	/*
	 * Exceptions
	 */
	public class InvalidTopologyException extends IOException{
		public InvalidTopologyException(String message){super(message);}
	}
	public class NoValidScheduleException extends IOException{
		public NoValidScheduleException(String message){super(message);}
	}
}
