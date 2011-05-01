package simulator.benchmarks;

import java.util.*;

import simulator.*;

public class MergeSort{
	
	public final int p;
	public final int maxNum;
	
	public final int numActors;
	public final int numLinks;
	
	public final Topology top;
	
	public static void main(String[] args){
		try{
			AbstractExecutor.NaiveExecute(new MergeSort(6).top, 1);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	public MergeSort(int power){
		p = power;
		maxNum = (int)Math.pow(2, p);
		

		numActors = 3 + (maxNum-1)*2;//2 branching strcutures, 2 print nodes and a generator
		numLinks = 1 + (maxNum-1)*2 + maxNum;//2 branching structures, the flow between them, and the link from gen to first print
		
		AbstractActor[] acts = new AbstractActor[numActors];
		AbstractLink[] lnks = new AbstractLink[numLinks];
		
		acts[0] = new GenActor(0);
		acts[1] = new PrintActor(1, true);//this printer on sends to the first splitter
		//add the split actors
		int numa = 2;
		int numl = 0;
		for(int n=0; n<p; n++){
			for(int a=0; a<Math.pow(2, n); a++){
				acts[numa] = new SplitActor(numa);
				if(n > 0){
					lnks[numl] = new AbstractLink(numl, acts[2 + (numa-3)/2], 1, acts[numa], 2);
					//System.out.println((2 + (numa-3)/2) + " -> " + numa + " : " + lnks[numl]);
					numl++;
				}
				numa++;
			}
		}
		//add the merge actors
		int firstMerge = numa;
		int firstLinks = numl;
		for(int n = p-1; n>=0; n--){
			for(int a=0; a<Math.pow(2, n); a++){
				acts[numa] = new MergeActor(numa, (int)Math.pow(2, p-n));
				//System.out.println("Merge " + (int)Math.pow(2, p-n) + " on " + n + ", " + a);
				if(n < p-1){
					lnks[numl] = new AbstractLink(numl, acts[firstMerge + numl - firstLinks], (int)Math.pow(2, p-n-1), acts[numa], (int)Math.pow(2, p-n-1));
					//System.out.println(lnks[numl]);
					numl++;
					lnks[numl] = new AbstractLink(numl, acts[firstMerge + numl - firstLinks], (int)Math.pow(2, p-n-1), acts[numa], (int)Math.pow(2, p-n-1));
					//System.out.println(lnks[numl]);
					numl++;
				}
				numa++;
			}
		}
		int firstLSplit = (int)(firstMerge - Math.pow(2, p-1));//the difficultly named First Last-Split
		//join the actors between split and merge
		for(int j=0; j<maxNum; j++){
			lnks[numl] = new AbstractLink(numl, acts[firstLSplit + j/2], 1, acts[firstMerge + j/2], 1);
			numl++;
		}
		acts[numa] = new PrintActor(acts.length-1, false);//the last actor does not output anything
		numa++;
		lnks[numl] = new AbstractLink(numl, acts[0], 1, acts[1], maxNum);//gen to print link
		numl++;
		lnks[numl] = new AbstractLink(numl, acts[1], maxNum, acts[2], 2);//print to first split link
		numl++;
		lnks[numl] = new AbstractLink(numl, acts[numa-2], maxNum, acts[numa-1], maxNum);//last merge to print link
		numl++;
		
		Topology topTemp = null;
		
		try{
			topTemp = new Topology(acts, lnks);
		}
		catch(Exception e){
			e.printStackTrace();
			topTemp = null;
		}
		
		top = topTemp;
		
	}
	
	public class NullActor extends AbstractActor{

		public NullActor(int idex) {
			super(idex);
		}
		
		//presume 1 input consumes 1
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
		private final boolean on;
		public PrintActor(int idex, boolean onsent){
			super(idex);
			on = onsent;
		}
		
		//we are supposed to have 1 input and 1 output
		protected void work(Object[][] in, Object[][] out){
			//System.out.println(in.length + ", " + out.length + ", " + in[0].length);
			System.out.print("seq[" + maxNum + "] = ");
			for(int i=0; i<maxNum; i++){
				System.out.print(in[0][i] + ", ");
				if(on) out[0][i] = in[0][i];
			}
			System.out.println();
		}
	}
}