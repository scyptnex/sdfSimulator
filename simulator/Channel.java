package simulator;

import java.util.*;

public class Channel {
	
	public static int countr = 0; 
	
	public final int name;
	private final LinkedList<Object> tokens;
	
	public final Actor producer, consumer;
	public final int prodamt, consamt;
	
	public Channel(Actor prod, int productions, Actor cons, int consumptions){
		name = countr;
		countr++;
		
		producer = prod;
		consumer = cons;
		prodamt = productions;
		consamt = consumptions;
		tokens = new LinkedList<Object>();
		
		producer.addProduction(this);
		consumer.addConsumptions(this);
	}
	
	public String getName(){
		return "-" + name  + ">";
	}
	
	public String bufferString(){
		return tokens.toString();
	}
	
	public String toString(){
		return producer.getName() + "--" + name + "(" + tokens.size() + ")" + "->" + consumer.getName();
	}
	
	public int getBufferSize(){
		return tokens.size();
	}
	
	public void setBuffer(Object[] toks){
		tokens.clear();
		injectBuffer(toks);
	}
	
	public void injectBuffer(Object[] toks){
		for(Object ob : toks){
			tokens.add(ob);
		}
	}
	
	public synchronized boolean produce(Object[] objs){
		//System.out.println("producing " + atos(objs) + " to " + getName() + "(" + tokens.size() + ") " + tokens);
		if(objs.length != prodamt) return false;
		injectBuffer(objs);
		return true;
	}
	
	public Object[] snapshot(){
		Object[] ret = new Object[tokens.size()];
		for(int i=0; i<tokens.size(); i++){
			ret[i] = tokens.get(i);
		}
		return ret;
	}
	
	public synchronized Object eat(){
		return tokens.pollFirst();
	}
	
	//public synchronized Object[] consume(){
		//Object[] ret = new Object[consamt];
		//for(int i=0; i<consamt; i++){
			//Object ob = tokens.pollFirst();
			//System.out.println("polling " + ob);
			//if(ob == null) return null;
			//ret[i] = ob;
		//}
		//System.out.println("consumed " + atos(ret) + " from " + getName() + "(" + tokens.size() + ") " + tokens);
		//return ret;
	//}
	
	public static String atos(Object[] arr){
		StringBuffer ret = new StringBuffer("[");
		for(int i=0; i<arr.length; i++){
			ret.append(arr[i]);
			if(i != arr.length-1) ret.append(", ");
		}
		ret.append("]");
		return ret.toString();
	}

}
