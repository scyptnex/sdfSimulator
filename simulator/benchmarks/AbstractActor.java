package simulator.benchmarks;

import simulator.*;

public abstract class AbstractActor extends Topology.Actor{
	
	public AbstractActor(int idex){
		super(idex);
	}
	
	public void fire(){
		//here we may do something with duration of actors or some such
		this.work();
	}
	
	//Each actor is responsible for pushing and popping its own datas
	protected abstract void work();
	
}