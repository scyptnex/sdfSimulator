package simulator;

public class MakeGLPK {
	
	public static final int CYCLE_TYPES = 5;
	public static final int ACTOR_COST = 20;
	public static final int PROC_CYCLES = 50;
	public static final int PROC_MIN = 5;
	
	public static void main(String[] args){
		try{
			new MakeGLPK(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		}
		catch(Exception e){
			System.err.println("Usage: java MakeGLPK <num actors> <duplicates> <processors>");
			new MakeGLPK(2, 1, 2);
		}
	}
	
	public MakeGLPK(int actors, int dups, int processors){
		/*
		 * Inits
		 */
		Compon[] acts = new Compon[actors];
		for(int i=0; i<acts.length; i++){
			acts[i] = new Compon();
			randFill(acts[i].cycles, ACTOR_COST + rand(ACTOR_COST));
		}
		
		Compon[] procs = new Compon[processors];
		for(int i=0; i<procs.length; i++){
			procs[i] = new Compon();
			randFill(procs[i].cycles, rand(PROC_CYCLES));
			for(int c=0; c<CYCLE_TYPES; c++){
				procs[i].cycles[c] += PROC_MIN;
			}
		}
		

		/*
		 * Preamble
		 */
		System.out.println("data;\n");
		System.out.println("param n := " + (actors*dups) + ";\n");
		System.out.println("param p := " + processors + ";\n");
		
		/*
		 * Grouping
		 */
		System.out.println("param AG :");
		System.out.print("\t");
		for(int i=0; i<actors*dups; i++){
			System.out.print((i+1) + "\t");
		}
		System.out.println(":=");
		for(int r=0; r<actors*dups; r++){
			System.out.print(" " + (r+1) + "\t");
			for(int c=0; c<actors*dups; c++){
				if(c == r) System.out.print(0);
				else if(c/dups == r/dups) System.out.print(1);
				else System.out.print(0);
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println(";\n");
		
		/*
		 * Process invocation
		 */
		double[][] invoke = new double[processors][actors*dups];
		for(int p=0; p<processors; p++){
			for(int act=0; act<actors*dups; act++){
				int a = act/dups;
				invoke[p][act] = 0.5;
			}
		}
		makeArray("PI", processors, actors*dups, invoke);
		
		/*
		 * Communicating Processors
		 */
		double[][] comproc = new double[processors][processors];
		for(int p1=0; p1<processors; p1++){
			for(int p2=0; p2<processors; p2++){
				comproc[p1][p2] = 0.7;
			}
		}
		makeArray("CP", processors, processors, comproc);
		
		/*
		 * Communicating actors
		 */
		double[][] comac = new double[actors*dups][actors*dups];
		for(int a=0; a<actors*dups; a++){
			for(int b=0; b<actors*dups; b++){
				comac[a][b] = 1.0;
			}
		}
		makeArray("CA", actors*dups, actors*dups, comac);
		
	}
	
	//in x-y configuration
	public static void makeArray(String name, int width, int height, double[][] vals){
		System.out.println("param " + name + " :");
		System.out.print("\t");
		for(int i=0; i<width; i++){
			System.out.print((i+1) + "\t");
		}
		System.out.println(":=");
		for(int r=0; r<height; r++){
			System.out.print(" " + (r+1) + "\t");
			for(int c=0; c<width; c++){
				System.out.print(vals[c][r]);
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println(";\n");
	}
	
	public class Compon{
		int[] cycles;
		public Compon(){
			cycles = new int[CYCLE_TYPES];
		}
		public String toString(){
			String ret = "";
			for(int i=0; i<CYCLE_TYPES; i++){
				ret += i + "=" + cycles[i] + " ";
			}
			return ret;
		}
	}
	
	public static int rand(int val){
		return (int)Math.floor(Math.random()*val);
	}
	
	public static void randFill(int[] target, int amt){
		int[] ords = new int[CYCLE_TYPES];
		for(int i=0; i<CYCLE_TYPES; i++){
			ords[i] = i;
		}
		for(int i=0; i<30; i++){
			int rec1 = rand(CYCLE_TYPES);
			int rec2 = rand(CYCLE_TYPES);
			int temp = ords[rec1];
			ords[rec1] = ords[rec2];
			ords[rec2] = temp;
		}
		for(int i=0; i<CYCLE_TYPES; i++){
			int cur = rand(amt);
			if(i == CYCLE_TYPES-1) cur = amt;
			amt -= cur;
			target[ords[i]] = cur;
		}
	}
	
}
