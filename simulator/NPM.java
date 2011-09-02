package simulator;

import java.util.*;

public class NPM {
	
	Topology2 top;
	
	public static void main(String[] args){
		Topology2 top = Generator.generateSimulated(false, false, 5, 3);
		System.out.println(top);
		NPM npm = new NPM(top);
		ArrayList<Integer> isch = Scheduler.saturate(top);
		System.out.println(isch.size());
		for(int ai : isch){
			top.actors.get(ai).invoke();
		}
		npm.printFillState();
	}
	
	public NPM(Topology2 topol){
		top = topol;
		if(top.rep == null){
			//TODO handle this
		}
		
		
	}
	
	public void printFillState(){
		for(Channel cnl : top.chans){
			System.out.println(cnl.producer.getName() + " ---(" + cnl.getBufferSize() + ")--> " + cnl.consumer.getName());
		}
	}
	
	
	
}
