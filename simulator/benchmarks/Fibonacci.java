package simulator.benchmarks;

import simulator.*;

public class Fibonacci {
	
	public Topology top;
	public AbstractDelayConfig delcon;
	
	public static void main(String[] args) throws Exception{
		Fibonacci fib = new Fibonacci();
		AbstractExecutor.NaiveExecute(fib.top, 20, fib.delcon);
	}
	
	public Fibonacci(){
		AbstractActor add = new AddActor(0);
		AbstractActor dup = new DupActor(1);
		AbstractActor out = new OutActor(2);
		AbstractActor[] acts = new AbstractActor[]{add, dup, out};
		AbstractLink ao = new AbstractLink(0, add, 1, out, 1);
		AbstractLink od = new AbstractLink(1, out, 1, dup, 1);
		AbstractLink da1 = new AbstractLink(2, dup, 1, add, 1);
		AbstractLink da2 = new AbstractLink(3, dup, 1, add, 1);
		AbstractLink[] lnks = new AbstractLink[]{ao, od, da1, da2};
		try{
			top = new Topology(acts, lnks);
			System.out.println(top);
			delcon = new AbstractDelayConfig(top);
			delcon.delay(ao.myIndex, 1);
			delcon.delay(da2.myIndex, 0);
		}
		catch(Exception e){
			e.printStackTrace();
			top = null;
		}
	}
	
	public static class DupActor extends AbstractActor{
		public DupActor(int idex){
			super(idex);
		}
		
		//we are supposed to have 1 input and 2 outputs
		protected void work(Object[][] in, Object[][] out){
			out[0][0] = in[0][0];
			out[1][0] = in[0][0];
		}
	}
	
	public static class OutActor extends AbstractActor{
		public OutActor(int idex){
			super(idex);
		}
		
		//we are supposed to have 1 input and 1 output
		protected void work(Object[][] in, Object[][] out){
			System.out.println(in[0][0]);
			out[0][0] = in[0][0];
		}
	}
	
	public static class AddActor extends AbstractActor{
		public AddActor(int idex){
			super(idex);
		}
		
		//we are supposed to have 2 inputs and 1 output
		//Oa = Ia + Ib
		protected void work(Object[][] in, Object[][] out){
			out[0][0] = ((Integer)in[0][0]) + ((Integer)in[1][0]);
		}
	}
	
}
