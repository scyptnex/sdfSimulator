package simulator;

import java.util.*;
import java.io.IOException;

public final class Actor {
	
	public static int countr = 0; 
	
	public final int name;
	public final Action method;
	private final ArrayList<Channel> productions;//links which i produce for
	private final ArrayList<Channel> consumptions;//links which i consume from
	
	public static Actor simulated(){
		return new Actor(new Simulated());
	}
	
	public Actor(Action actn){
		name = countr;
		method = actn;
		countr++;
		productions = new ArrayList<Channel>();
		consumptions = new ArrayList<Channel>();
	}
	
	public String getName(){
		return "{" + name  + "}";
	}
	
	public void addProduction(Channel c){
		productions.add(c);
	}
	
	public void addConsumptions(Channel c){
		consumptions.add(c);
	}
	
	public final void invoke(){
		
	}
	
	public final int numProductions(){
		return productions.size();
	}
	
	public final int numConsumptions(){
		return consumptions.size();
	}
	
	protected final int productionSize(int idex){
		return productions.get(idex).prodamt;
	}
	
	protected final int consumptionSize(int idex){
		return consumptions.get(idex).consamt;
	}
	
	protected final boolean produceTo(int idex, ArrayList<Object> data){
		productions.get(idex).produce(data);
		return true;//TODO this?
	}
	
	protected final ArrayList<Object> consumeFrom(int idex){
		return consumptions.get(idex).consume();//TODO this?
	}
	
	public final Channel getProdChannel(int idex){
		return productions.get(idex);
	}
	
	public final Channel getConsChannel(int idex){
		return consumptions.get(idex);
	}
	
	public static class SDFException extends IOException{
		public SDFException(String message){
			super("SDF Exception raised: " + message);
		}
	}
	
	public static class Simulated implements Action{
		@Override
		public void invoke(Actor parent, ArrayList<Object>[] inputs, ArrayList<Object>[] outputs) {
			System.out.println("Invoke: " + parent.getName());
			//does nothing;
		}
	}
	
	public static class Duplicated implements Action{
		int dups;
		Action sub;
		public Duplicated(int dup, Action subAct){
			sub = subAct;
			dups = dup;
		}
		@Override
		public void invoke(Actor parent, ArrayList<Object>[] inputs, ArrayList<Object>[] outputs) {
			System.out.println("Invoke: " + parent.getName());
			//TODO this
		}
	}
	
}
