package simulator.benchmarks;

import java.util.*;

import simulator.*;

public class AbstractDelayConfig extends DelayConfig{
	
	public ArrayList<ArrayList<Object>> delayData;
	
	public AbstractDelayConfig(Topology top) throws Exception{
		super(top, new int[top.numLinks]);
		delayData = new ArrayList<ArrayList<Object>>(top.numLinks);
		for(int i=0; i<top.numLinks; i++){
			delayData.add(new ArrayList<Object>());
		}
	}
	
	public void delay(int link, Object datum){
		super.delay(link);
		delayData.get(link).add(datum);
	}
	
}
