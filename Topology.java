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
	
	private void setReps(double[] rv, int act, double rep){
		//naive non-pre-sorted operation, O(n^3)
		//will have to re-do this when there's time
		//it is also suggested we use fractions not doubles
		rv[act] = rep;
		for(int l=0; l<numLinks; l++){
			if(matrix[act][l] < 0){
				System.out.println(act + " has a link on edge " + l);
				for(int a=0; a<numActors; a++){
					if(matrix[a][l] > 0){
						//System.out.println("Repeating link " + a + "(" + rv[a] + ") with rep " + (-rep * matrix[act][l] / matrix[a][l]) + ": " + matrix[a][l] + " " + matrix[act][l]);
						//System.exit(0);
						if(rv[a] == 0.0) setReps(rv, a, -rep * matrix[act][l] / matrix[a][l]);
						break;
					}
				}
			}
		}
		for(int l=0; l<numLinks; l++){
			if(matrix[act][l] > 0){
				for(int a=0; a<numActors; a++){
					if(matrix[a][l] < 0){
						//System.out.println("Repeating link " + a + "(" + rv[a] + ") with rep " + (-rep * matrix[a][l] / matrix[act][l]) + ": " + matrix[a][l] + " " + matrix[act][l]);
						if(rv[a] == 0.0) setReps(rv, a, -rep * matrix[a][l] / matrix[act][l]);//inverse of above
						break;
					}
				}
			}
		}
	}
	
	private int[] calcRepetitionVector(){
		double[] ret = new double[numActors];
		if(ret.length == 0) return null;
		setReps(ret, 0, 1.0);
		for(int i=0; i<ret.length; i++){
			System.out.println("rv: " + ret[i]);
		}
		//TODO calculate lowest common fraction
		//possibly consider using fraction class instead of doubles
		return null;
	}
	
	public String toString(){
		String ret = "Gamma = " + numLinks + " x " + numActors;
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
