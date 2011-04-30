package simulator.benchmarks;

import simulator.*;

public abstract class AbstractActor extends Topology.Actor{
	
	public AbstractActor(int idex){
		super(idex);
	}
	
	public void fire(){
		//here we may do something with duration of actors or some such
		Object[][] in = new Object[this.consumptions.size()][];
		for(int c=0; c<this.consumptions.size(); c++){
			in[c] = new Object[this.consumptions.get(c).consumeAmmount];
			for(int i=0; i<in[c].length; i++){
				in[c][i] = ((AbstractLink)this.consumptions.get(c)).get();
			}
		}
		Object[][] out = new Object[this.productions.size()][];
		for(int p=0; p<this.productions.size(); p++){
			out[p] = new Object[this.productions.get(p).produceAmmount];
		}
		this.work(in, out);
		for(int p=0; p<this.productions.size(); p++){
			for(int i=0; i<out[p].length; i++){
				((AbstractLink)this.productions.get(p)).add(out[p][i]);
			}
		}
	}
	
	protected abstract void work(Object[][] in, Object[][] out);
	
}