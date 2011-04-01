import java.util.*;
import java.io.*;

public class Topology {
	
	public final int numActors;
	public final int numLinks;
	
	public final int[][] matrix;//matrix[actor][link]
	
	public final Actor[] actors;
	public final Link[] links;
	
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
	
	public Topology(int[][] mat) throws InvalidTopologyException, NoValidScheduleException{
		matrix = mat;
		numActors = matrix.length;
		numLinks = (numActors > 0 ? matrix[0].length : 0);

		actors = new Actor[numActors];
		for(int a=0; a<numActors; a++) actors[a] = new Actor(a);
		
		links = new Link[numLinks];
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
			//links show consumption as a positive integer, so must negate the value in topology matrix
			links[l] = new Link(l, actors[prod], matrix[prod][l], actors[con], -matrix[con][l]);
		}
		
		repetitions = calcRepetitionVector();
	}
	
	private int[] calcRepetitionVector() throws NoValidScheduleException{
		if(numActors == 0) return null;
		
		//recurseively set the fractional repetitions of all actors
		actors[0].setRepsRecursively(new Fraction(1, 1));
		
		//list the denominators, so we can find their LCM and make all repetitions integral
		long[] denoms = new long[numActors];
		for(int i=0; i<numActors; i++)
			denoms[i] = actors[i].repetitions.denominator();
		long lcm = Fraction.lcmm(denoms);
		
		//check that the production ammount equals the consumption ammount
		for(Link l : links){
			Fraction totProduce = l.producer.repetitions.times(l.produceAmmount);
			Fraction totConsume = l.consumer.repetitions.times(l.consumeAmmount);
			if(!totProduce.equals(totConsume))
				throw new NoValidScheduleException("Inconsistant repetitions on link " + l + ", which produces " + totProduce + " and consumes " + totConsume);
		}
		
		//return the vector of ints
		//realistically all repetitions are currently stored fractionally in their Actor wrapper classes
		int[] ret = new int[numActors];
		for(int i=0; i<numActors; i++)
			ret[i] = (int)actors[i].repetitions.times(lcm).numerator();
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
			repetitions = null;
		}
		
		public void setRepsRecursively(Fraction r){
			repetitions = r;
			for(Link pl: productions){
				if(pl.consumer.repetitions == null){
					Fraction newRep = repetitions.times(pl.produceAmmount).dividedBy(pl.consumeAmmount);
					pl.consumer.setRepsRecursively(newRep);
				}
			}
			
			for(Link cl: consumptions){
				if(cl.consumer.repetitions == null){
					Fraction newRep = repetitions.times(cl.consumeAmmount).dividedBy(cl.produceAmmount);
					cl.consumer.setRepsRecursively(newRep);
				}
			}
		}
		
		public String toString(){
			String ret = "Actor " + myIndex + " (r = " + repetitions + ")\n" + productions.size() + " productions";
			for(Link l : productions) ret += "\n  " + l.toString();
			ret += "\n" + consumptions.size() + " consumptions";
			for(Link l : consumptions) ret += "\n  " + l.toString();
			return ret;
		}
	}
	
	//Links automatically add themselves to the actors they link
	public static class Link{
		
		public final int myIndex;
		
		public final Actor producer;
		public final int produceAmmount;
		
		public final Actor consumer;
		public final int consumeAmmount;
		
		public Link(int idex, Actor prod, int pamt, Actor con, int camt){
			myIndex = idex;
			
			producer = prod;
			produceAmmount = pamt;
			
			consumer = con;
			consumeAmmount = camt;
			
			producer.productions.add(this);
			consumer.consumptions.add(this);
		}
		
		public String toString(){
			return myIndex + ": " + producer.myIndex + "(" + produceAmmount + ") -> " + consumer.myIndex + "(" + consumeAmmount + ")";
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
