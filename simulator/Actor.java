package simulator;

import java.util.*;

public abstract class Actor {
	
	private final ArrayList<Channel> productions;//links which i produce for
	private final ArrayList<Channel> consumptions;//links which i consume from
	
	public Actor(){
		productions = new ArrayList<Channel>();
		consumptions = new ArrayList<Channel>();
	}
	
	public void addProduction(Channel c){
		productions.add(c);
	}
	
	public void addConsumptions(Channel c){
		consumptions.add(c);
	}
	
}
