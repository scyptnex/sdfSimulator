package simulator.benchmarks;

import simulator.*;
import simulator.Topology.InvalidTopologyException;
import simulator.Topology.NoValidScheduleException;

import java.util.*;

public class BitonicSort {

	public final int p;
	public final int n;
	
	public Topology top;
	
	public static void main(String[] args){
		for(int n=0; n<6; n++){
			for(int i=5; i>n; i--){
				System.out.print(" ");
			}
			for(int k=0; k<=n; k++){
				System.out.print(choose(n, k) + " ");
			}
			System.out.println();
		}
		
		//new BitonicSort(3);
		
		//new BitonicSort(5);
		
		try{
			AbstractExecutor.NaiveExecute(new BitonicSort(8).top, 20);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	public BitonicSort(int pow) throws InvalidTopologyException, NoValidScheduleException{
		if(pow < 1) pow = 1;
		p = pow;
		n = (int)Math.pow(2, p);
		
		ArrayList<AbstractActor> aa = new ArrayList<AbstractActor>();
		ArrayList<AbstractLink> al = new ArrayList<AbstractLink>();
		
		AbstractActor ia = new InputActor(0, n);
		AbstractActor oa = new OutputActor(1, n);
		
		aa.add(ia);
		aa.add(oa);
		
		AbstractActor[] outputters = new AbstractActor[n];
		for(int i=0; i<n; i++){
			outputters[i] = ia;
		}
		
		for(int blue=0; blue<p; blue++){
			int bsize = (int)Math.pow(2, blue+1);
			for(int red = blue; red >= 0; red--){
				AbstractActor[] inputters = new AbstractActor[n];
				int rsize = (int)Math.pow(2, red+1);
				if(red == blue){//flippies
					for(int orng = 0; orng < n/bsize; orng++){
						for(int i=0; i<bsize/2; i++){
							AbstractActor tmp = new SwitchActor(aa.size());
							int ra = orng*bsize + i;
							int rb = orng*bsize + bsize-i-1;
							inputters[ra] = tmp;
							inputters[rb] = tmp;
							aa.add(tmp);
						}
					}
				}
				else{//shifties
					for(int rblock=0; rblock < n/rsize; rblock++){
						for(int i=0; i<rsize/2; i++){
							AbstractActor tmp = new SwitchActor(aa.size());
							int ra = rblock*rsize + i;
							int rb = rblock*rsize + i + rsize/2;
							inputters[ra] = tmp;
							inputters[rb] = tmp;
							aa.add(tmp);
						}
					}
				}
				
				//finally shift the inputters to be outputters for the next group
				for(int i=0; i<n; i++){
					al.add(new AbstractLink(al.size(), outputters[i], 1, inputters[i], 1));
					outputters[i] = inputters[i];
				}
			}
		}
		
		for(int i=0; i<n; i++){
			al.add(new AbstractLink(al.size(), outputters[i], 1, oa, 1));
		}
		
		AbstractActor[] acts = new AbstractActor[aa.size()];
		for(int i=0; i<acts.length; i++){
			acts[i] = aa.get(i);
			//System.out.println(acts[i].myIndex);
		}
		AbstractLink[] lnks = new AbstractLink[al.size()];
		for(int i=0; i<lnks.length; i++){
			lnks[i] = al.get(i);
		}
		
		top = new Topology(acts, lnks);
		
	}
	
	/*
	 * Actors
	 */
	public class SwitchActor extends AbstractActor{
		public SwitchActor(int idex){
			super(idex);
		}
		
		//we are supposed to have 2 in 2 out
		protected void work(Object[][] in, Object[][] out){
			int a = (Integer)in[0][0];
			int b = (Integer)in[1][0];
			out[0][0] = Math.min(a, b);
			out[1][0] = Math.max(a, b);
		}
	}
	public class InputActor extends AbstractActor{
		private final int number;
		public InputActor(int idex, int num){
			super(idex);
			number = num;
		}
		
		//we are supposed to have no input and number outputs
		protected void work(Object[][] in, Object[][] out){
			System.out.print("Unsorted:");
			for(int i=0; i<number; i++){
				out[i][0] = (Integer)((int)Math.floor(Math.random()*100));
				System.out.print(" " + out[i][0] + ",");
			}
			System.out.println();
		}
	}
	public class OutputActor extends AbstractActor{
		private final int number;
		public OutputActor(int idex, int num){
			super(idex);
			number = num;
		}
		
		//we are supposed to have number input and no outs
		protected void work(Object[][] in, Object[][] out){
			int[] rd = new int[number];
			boolean successful = true;
			for(int i=0; i<number; i++){
				rd[i] = (Integer)in[i][0];
				if(i > 0 && rd[i] < rd[i-1]) successful = false;
			}
			System.out.println(successful ? "Sorted" : "FAILED");
		}
	}
	
	//n choose k
	public static long choose(int n, int k){
		if(k == 0) return 1;
		if(n == 0) return 0;
		Fraction ret = new Fraction(1, 1);
		for(int i=1; i<=k; i++){
			Fraction sub = new Fraction(n-(k-i), i);
			ret = ret.times(sub);
		}
		return ret.numerator();
	}
	
}
