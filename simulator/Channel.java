package simulator;

import java.util.*;

public class Channel {
	
	private final LinkedList<Object> tokens;
	
	public final Actor producer, consumer;
	public final int prodamt, consamt;
	
	public Channel(Actor prod, int productions, Actor cons, int consumptions){
		producer = prod;
		consumer = cons;
		prodamt = productions;
		consamt = consumptions;
		tokens = new LinkedList<Object>();
		
		producer.addProduction(this);
		consumer.addProduction(this);
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
