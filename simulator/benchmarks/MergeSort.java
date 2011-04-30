package simulator.benchmarks;

import java.util.*;

import simulator.*;

public class MergeSort{
	
	public final int p;
	public final int maxNum;
	
	public final int numActors;
	public final int numLinks;
	
	public static void main(String[] args){
		new MergeSort(3);
	}
	
	public MergeSort(int power){
		p = power;
		maxNum = (int)Math.pow(2, p);
		

		numActors = 3 + (maxNum-1)*2;//2 branching strcutures, 2 print nodes and a generator
		numLinks = 1 + (maxNum-1)*2 + maxNum;//2 branching structures, the flow between them, and the link from gen to first print
		
		AbstractActor[] acts = new AbstractActor[numActors];
		AbstractLink[] lnks = new AbstractLink[numLinks];
		
		//acts[0] = new GenActor(0);
		//acts[1] = new PrintActor(1);
		//acts[acts.length-1] = new PrintActor(acts.length-1);
		
		AbstractActor[] testAct = new AbstractActor[5];
		AbstractLink[] testLink = new AbstractLink[4];
		
		testAct[0] = new GenActor(0);
		testAct[1] = new GenActor(1);
		testAct[2] = new MergeActor(2, 2);
		testAct[3] = new PrintActor(3);
		testAct[4] = new NullActor(4);
		
		testLink[0] = new AbstractLink(0, testAct[0], 1, testAct[2], 1);
		testLink[1] = new AbstractLink(1, testAct[1], 1, testAct[2], 1);
		testLink[2] = new AbstractLink(2, testAct[2], 2, testAct[3], maxNum);
		testLink[3] = new AbstractLink(3, testAct[3], maxNum, testAct[4], 1);
		
		
		try{
			Topology top = new Topology(testAct, testLink);
			System.out.println(top);
			AbstractExecutor.NaiveExecute(top, 4);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public class NullActor extends AbstractActor{

		public NullActor(int idex) {
			super(idex);
		}
		
		//presume 1 input consumes 2, 2 outputs produce 1 each
		protected void work(Object[][] in, Object[][] out) {
		}
	}
	
	public class MergeActor extends AbstractActor{
		
		int outLength;

		public MergeActor(int idex, int ol) {
			super(idex);
			outLength = ol;
		}
		
		protected void work(Object[][] in, Object[][] out) {
			int aRead = 0;
			int bRead = 0;
			for(int i=0; i<outLength; i++){
				if(aRead < outLength/2){
					if(bRead < outLength/2){
						int alpha = (Integer) in[0][aRead];
						int beta = (Integer) in[1][bRead];
						if(alpha < beta){
							out[0][i] = in[0][aRead];
							aRead++;
						}
						else{
							out[0][i] = in[1][bRead];
							bRead++;
						}
					}
					else{
						out[0][i] = in[0][aRead];
						aRead++;
					}
				}
				else{
					out[0][i] = in[1][bRead];
					bRead++;
				}
			}
		}
	}
	
	public class SplitActor extends AbstractActor{

		public SplitActor(int idex) {
			super(idex);
		}
		
		//presume 1 input consumes 2, 2 outputs produce 1 each
		protected void work(Object[][] in, Object[][] out) {
			out[0][0] = in[0][0];
			out[1][0] = in[0][1];
		}
	}
	
	public class GenActor extends AbstractActor{
		public GenActor(int idex){
			super(idex);
		}
		
		//we are supposed to have no input and 1 output
		protected void work(Object[][] in, Object[][] out){
			out[0][0] = (Integer)((int)Math.floor(Math.random()*100));
		}
	}
	
	public class PrintActor extends AbstractActor{
		public PrintActor(int idex){
			super(idex);
		}
		
		//we are supposed to have 1 input and 1 output
		protected void work(Object[][] in, Object[][] out){
			//System.out.println(in.length + ", " + out.length + ", " + in[0].length);
			System.out.print("seq[" + maxNum + "] = ");
			for(int i=0; i<maxNum; i++){
				System.out.print(in[0][i] + ", ");
				out[0][i] = in[0][i];
			}
			System.out.println();
		}
	}
}