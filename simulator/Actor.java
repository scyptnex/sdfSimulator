package simulator;

import java.util.*;
import java.io.IOException;

public abstract class Actor {
	
	public static int countr = 0; 
	
	public final int name;
	private final ArrayList<Channel> productions;//links which i produce for
	private final ArrayList<Channel> consumptions;//links which i consume from
	
	public Actor(){
		name = countr;
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
		customInvoke();
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
	
	protected abstract void customInvoke();
	
	public static class SDFException extends IOException{
		public SDFException(String message){
			super("SDF Exception raised: " + message);
		}
	}
	
	public static class Default extends Actor{
		String name;
		public Default(String na){
			super();
			name = na;
		}
		
		protected void customInvoke() {
			System.out.println("name invoked");
		}
	}
	
	public static class Simulated extends Actor{
		public Simulated(){
			super();
		}
		protected void customInvoke(){
			System.out.println("Invoke: " + getName());
			for(int i=0; i<this.numConsumptions(); i++){
				this.consumeFrom(i);//do nothing with return
			}
			for(int p=0; p<this.numProductions(); p++){
				ArrayList<Object> temp = new ArrayList<Object>(this.productionSize(p));
				for(int ps=0; ps<this.productionSize(p); ps++) temp.add(null);
				this.produceTo(p, temp);//do nothing if it fails
				
			}
		}
	}
	
}
