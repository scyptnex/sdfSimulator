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
		return "--" + name  + "->";
	}
	
	public int getBufferSize(){
		return tokens.size();
	}
	
	public void setBuffer(ArrayList<Object> toks){
		tokens.clear();
		injectBuffer(toks);
	}
	
	public void injectBuffer(ArrayList<Object> toks){
		for(Object ob : toks){
			tokens.push(ob);
		}
	}
	
	public boolean produce(ArrayList<Object> objs){
		System.out.println("producing to " + getName());
		if(objs.size() != prodamt) return false;
		injectBuffer(objs);
		return true;
	}
	
	public ArrayList<Object> consume(){
		ArrayList<Object> ret = new ArrayList<Object>(consamt);
		for(int i=0; i<consamt; i++){
			Object ob = tokens.pollFirst();
			if(ob == null) return null;
			ret.add(ob);
		}
		return ret;
	}

}
