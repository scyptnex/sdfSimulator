package simulator.mergesort;

import simulator.*;

public class MergeSort {
	
	public final int msNumber;
	
	public static void main(String[] args){
		new MergeSort(4);//asin 2^5 = 32
	}
	
	public MergeSort(int num){
		msNumber = (int)Math.floor(Math.pow(2, num));
		int numActors = 3 + (msNumber-1)*2;//2 branching strcutures, 2 print nodes and a generator
		int numLinks = 1 + (msNumber-1)*2 + msNumber;//2 branching structures, the flow between them, and the link from gen to first print
		int[][] top = new int[numActors][numLinks];
		
		Topology top = new Topology();
		
		Actor[] acts = new Actor[numActors];
		Link[] links = new Link[numLinks];
		
		
		
		Link l1 = new Link();
		Link outl = new Link();
		Actor ga = new GenActor(l1);
		Actor pa = new PrintActor(l1, outl);
		System.out.println(l1.fillState() + " - " + outl.fillState());
		ga.fire();
		System.out.println(l1.fillState() + " - " + outl.fillState());
		for(int i=0; i<msNumber-1; i++){
			ga.fire();
		}
		System.out.println(l1.fillState() + " - " + outl.fillState());
		pa.fire();
		System.out.println(l1.fillState() + " - " + outl.fillState());
	}
	
	private class SortMergeActor extends Actor{
		public final int amt;
		public final Link input1;
		public final Link input2;
		public final Link output;
		//a is the ammount this merger outputs
		public SortMergeActor(int a, Link in1, Link in2, Link out){
			amt = a;
			input1 = in1;
			input2 = in2;
			output = out;
		}
		
		public void work(){
			int numAlpha = 0;
			int numBeta = 0;
			while(numAlpha + numBeta < amt){
				if(numAlpha < amt/2){
					if(numBeta < amt/2){
						int alpha = (Integer)input1.get();
						int beta = (Integer)input2.get();
						if(alpha < beta){
							output.add(alpha);
							numAlpha++;
						}
						else{
							output.add(beta);
							numBeta++;
						}
					}
					else{
						output.add(input1.get());//stream the alphas quickly
						numAlpha++;
					}
				}
				else{
					//we know numbeta is less than to amt/2
					output.add(input2.get());//stream the betas quickly
					numBeta++;
				}
			}
		}
	}
	
	private class PrintActor extends Actor{
		public final Link input;
		public final Link output;
		public PrintActor(Link in, Link out){
			input = in;
			output = out;
		}
		
		public void work(){
			System.out.print("cur[" + msNumber + "] = ");
			for(int i=0; i<msNumber; i++){
				Object o = input.get();
				System.out.print(o.toString() + ", ");
				output.add(o);
			}
			System.out.println();
		}
	}
	
	private class GenActor extends Actor{
		public final Link output;
		public GenActor(Link out){
			output = out;
		}
		
		public void work(){
			output.add((int)Math.floor(Math.random()*100));
		}
	}
	
	private class SplitActor extends Actor{
		
		public final Link input;
		public final Link alpha;
		public final Link beta;
		
		public SplitActor(Link in, Link out1, Link out2){
			input = in;
			alpha = out1;
			beta = out2;
		}
		
		public void work(){
			alpha.add(input.get());
			alpha.add(input.get());
		}
	}
	
}
