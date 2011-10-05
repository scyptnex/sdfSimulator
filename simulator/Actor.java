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
	
	//protected final boolean produceTo(int idex, Object[] data){
	//	productions.get(idex).produce(data);
	//	return true;//TODO this?
	//}
	
	//protected final ArrayList<Object> consumeFrom(int idex){
	//	return consumptions.get(idex).consume();//TODO this?
	//}
	
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
		
		public Double state = 0.0;
		
		@Override
		public int invoke(String parent, ArrayList<Object[]> inputs, ArrayList<Object[]> outputs){
			if(inputs.size() == 0){
				state = state + 1.0;
			}
			else{
				state = 0.0;
				int count = 0;
				for(Object[] in : inputs){
					//System.out.print(Channel.atos(in));
					for(int i=0; i<in.length; i++){
						if(in[i] == null) return Action.RETURN_FAIL;
						state += (Double)in[i];
						count++;
					}
				}
				state = state/count;
			}
			if(outputs.size() == 0){
				System.out.println(parent + " - " + state);
			}
			for(int i=0; i<outputs.size(); i++){
				int len = outputs.get(i).length;
				for(int tok=0; tok<len; tok++){
					//System.out.println("placing " + state + " on " + i);
					outputs.get(i)[tok] = state;
					state = state + 1.0;
				}
				//System.out.println("placing " + Channel.atos(arr));
			}
			return Action.RETURN_OK;
		}
	}
	
	public static class Duplicated implements Action{
		int dups;
		int order;
		Action sub;
		public Duplicated(int dup, int ord, Action subAct){
			sub = subAct;
			dups = dup;
			order = ord;
		}
		@Override
		public int invoke(String parent, ArrayList<Object[]> inputs, ArrayList<Object[]> outputs){
			//System.out.println("duplicate invoke " + parent + ", " + order + "[" + inputs.size()/dups + "]");
			boolean foundout = false;
			ArrayList<Object[]> subin = new ArrayList<Object[]>();
			for(int i=0; i<inputs.size()/dups; i++){
				Object[] subchan = new Object[inputs.get(i*dups).length];
				for(int t=0; t<subchan.length; t++){
					Object take = null;
					int didex = 0;
					while(take == null && didex < dups){
						take = inputs.get(i*dups + didex)[t];
						if(take == null) foundout = true;//one of the input tokens was null, so theres dead processors
						didex++;
					}
					if(take == null){
						return Action.RETURN_FAIL;//all processors were dead
					}
					subchan[t] = take;
				}
				subin.add(subchan);
			}
			ArrayList<Object[]> subout = new ArrayList<Object[]>();
			for(int o=0; o<outputs.size()/dups; o++){
				subout.add(new Object[outputs.get(o*dups).length]);
			}
			
			int subret = sub.invoke(parent + "[" + order + "]", subin, subout);
			
			if(subret == Action.RETURN_FAIL){
				System.out.println(parent + " - how on earth did my child fail?");
				return Action.RETURN_FAIL;
			}
			
			for(int o=0; o<outputs.size(); o++){
				for(int t=0; t<outputs.get(o).length; t++){
					outputs.get(o)[t] = subout.get(o/dups)[t];
				}
			}
			
			return (foundout ? Action.RETURN_DETECT: Action.RETURN_OK);
		}
	}
	
}
