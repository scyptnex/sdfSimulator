package simulator;

import java.util.*;

public abstract class Actor {
	
	public final boolean simulated;
	
	private final ArrayList<Channel> productions;//links which i produce for
	private final ArrayList<Channel> consumptions;//links which i consume from
	
	public Actor(boolean sim){
		simulated = sim;
		productions = new ArrayList<Channel>();
		consumptions = new ArrayList<Channel>();
	}
	
	public void addProduction(Channel c){
		productions.add(c);
	}
	
	public void addConsumptions(Channel c){
		consumptions.add(c);
	}
	
	public final void invoke(){
		if(!simulated){
			customInvoke();
		}
		else{
			for(Channel con : consumptions){
				con.consume();
			}
			for(Channel pro : productions){
				pro.produce(null);
			}
		}
	}
	
	protected abstract void customInvoke();
	
	public static class Default extends Actor{
		
		String name;
		
		public Default(String na){
			super(false);
			name = na;
		}
		
		protected void customInvoke() {
			System.out.println("name invoked");
		}
		
	}
	
}
