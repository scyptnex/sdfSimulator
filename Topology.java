import java.util.*;

public class Topology {
	
	public final int numActors;
	public final int numLinks;
	
	public final int[][] matrix;//matrix[actor][link]
	
	public final int[] repetitions;
	
	public static Topology loadTopology(Scanner top){
		int actors = Integer.parseInt(top.nextLine());
		int links = Integer.parseInt(top.nextLine());
		int[][] topolog = new int[actors][links];
		for(int l=0; l<links; l++){
			for(int a=0; a<actors; a++){
				topolog[a][l] = top.nextInt();
			}
		}
		return new Topology(topolog);
	}
	
	public Topology(int act, int link){
		numActors = act;
		numLinks = link;
		matrix = new int[numActors][numLinks];
		
		repetitions = calcRepetitionVector();
	}
	
	public Topology(int[][] mat){
		matrix = mat;
		numActors = matrix.length;
		numLinks = (numActors > 0 ? matrix[0].length : 0);
		
		repetitions = calcRepetitionVector();
	}
	
	private void setReps(Fraction[] rv, int act, Fraction rep){
		//naive non-pre-sorted operation, O(n^3)
		//will have to re-do this when there's time
		rv[act] = rep;
		for(int l=0; l<numLinks; l++){
			if(matrix[act][l] < 0){
				//System.out.println(act + " has a link on edge " + l);
				for(int a=0; a<numActors; a++){
					if(matrix[a][l] > 0){
						Fraction newRep = rep.times(-matrix[a][l]).dividedBy(matrix[act][l]);
						//System.out.println("Repeating link " + a + "(" + rv[a] + ") with rep " + newRep + ": " + matrix[a][l] + " " + matrix[act][l]);
						//System.exit(0);
						if(rv[a].equals(0)) setReps(rv, a, newRep);
						break;
					}
				}
			}
		}
		for(int l=0; l<numLinks; l++){
			if(matrix[act][l] > 0){
				for(int a=0; a<numActors; a++){
					if(matrix[a][l] < 0){
						Fraction newRep = rep.times(-matrix[act][l]).dividedBy(matrix[a][l]);
						//System.out.println("Repeating link " + a + "(" + rv[a] + ") with rep " + newRep + ": " + matrix[a][l] + " " + matrix[act][l]);
						if(rv[a].equals(0)) setReps(rv, a, newRep);//inverse of above
						break;
					}
				}
			}
		}
	}
	
	private int[] calcRepetitionVector(){
		if(numActors == 0) return null;
		Fraction[] reps = new Fraction[numActors];
		for(int i=0; i<numActors; i++) reps[i] = new Fraction(0, 1);
		setReps(reps, 0, new Fraction(1, 1));
		long[] denoms = new long[numActors];
		for(int i=0; i<numActors; i++){
			denoms[i] = reps[i].denominator();
		}
		long lcm = Fraction.lcmm(denoms);
		
		int[] ret = new int[numActors];
		for(int i=0; i<numActors; i++){
			ret[i] = (int)reps[i].times(lcm).numerator();
			//System.out.println(i + " = " + ret[i]);
		}
		for(int l=0; l<numLinks; l++){
			int prod = 0;
			int cons = 0;
			for(int a=0; a<numActors; a++){
				if(matrix[a][l] > 0){
					prod = matrix[a][l] * ret[a];
					//System.out.println(a + "-" + l + " produces " + prod);
				}
				if(matrix[a][l] < 0){
					cons = -matrix[a][l] * ret[a];
					//System.out.println(a + "-" + l + " consumes " + prod);
				}
			}
			if(prod == 0 || prod != cons){
				System.err.println("Invalid topology matrix: there exists no admissable schedule");
				return null;
			}
		}
		return ret;
	}
	
	public String toString(){
		String ret = "Gamma = " + numLinks + " x " + numActors;
		if(repetitions != null){
			ret += "\n= ";
			for(int a=0; a<numActors; a++){
				ret += repetitions[a] + "\t";
			}
			ret += "=";
		}
		for(int l=0; l<numLinks; l++){
			ret += "\n| ";
			for(int a=0; a<numActors; a++){
				ret += matrix[a][l] + "\t";
			}
			ret += "|";
		}
		return ret;
	}
	
}
