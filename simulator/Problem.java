package simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Problem {

	public static final int NUM_AFFINITIES = 3;
	public static final double INVOKE_SCALE = 0.3;

	public final Topology2 top;
	public final NPM mac;
	
	public final int n;
	public final int p;
	
	public final double[][] invoke;
	public final double[][] commu;
	public final double[][] band;
	
	public static void main(String[] args){
		Topology2 top = Generator.generateSimulated(false, false, 4, 3);
		NPM mac = new NPM(5);
		Problem alph = new Problem(top, mac, INVOKE_SCALE);
		alph.reinvoke(1.0, 1.0);
		Problem bet = new Problem(alph, 2);
		Problem gam = new Problem(alph, 3);
		alph.save(new File("alpha.exp"));
		bet.save(new File("beta.exp"));
		gam.save(new File("gamma.exp"));
	}
	
	public Problem(Problem prob, int dups){
		mac = prob.mac;
		p = mac.numProcessors;
		band = mac.bandwidths;
		top = new Topology2(prob.top, dups);
		commu = top.communication;
		n = top.actors.size();
		invoke = new double[n][p];
		for(int a=0; a<n; a++){
			for(int proc=0; proc<p; proc++){
				invoke[a][proc] = prob.invoke[a/dups][proc];
			}
		}
	}
	
	public Problem(Topology2 t, NPM m, double invokeScale){
		top = t;
		mac = m;
		invoke = affine(mac.pAffinities, top.affinities, invokeScale);
		n = top.actors.size();
		p = mac.numProcessors;
		commu = top.communication;
		band = mac.bandwidths;
	}
	
	public Problem(File loc){
		top = readTopology(loc);
		mac = readMachine(loc);
		
		double[][] inv = null;
		try{
			Scanner sca = new Scanner(loc);
			inv = new double[top.actors.size()][mac.numProcessors];
			pass(sca, "param PI");
			sca.nextLine();
			for(int i=0; i<top.actors.size(); i++){
				sca.next();
				for(int j=0; j<mac.numProcessors; j++){
					inv[i][j] = sca.nextDouble();
				}
			}
			sca.close();
		}
		catch(IOException e){
			inv = null;
		}
		invoke = inv;
		n = top.actors.size();
		p = mac.numProcessors;
		commu = top.communication;
		band = mac.bandwidths;
	}
	
	public void reinvoke(double min, double range){
		for(int i=0; i<top.actors.size(); i++){
			for(int p=0; p<mac.numProcessors; p++){
				invoke[i][p] = this.roundFourDecimals(min + Math.random()*range);
			}
		}
	}
	
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append(top.toString());
		ret.append("\n--------------------\n");
		ret.append(mac.toString());
		ret.append("\n--------------------\nInvocation:");
		for(int a=0; a<top.actors.size(); a++){
			ret.append("\n");
			for(int p=0; p<mac.numProcessors; p++){
				ret.append(invoke[a][p] + " ");
			}
		}
		return ret.toString();
	}
	
	public boolean save(File loc){
		return save(loc, this);
	}
	
	public static Topology2 readTopology(File loc){
		try{
			Scanner sca = new Scanner(loc);
			
			sca.findWithinHorizon("param n := ", Integer.MAX_VALUE);
			int num = Integer.parseInt(sca.next().replaceAll("[^0-9]", ""));
			
			pass(sca, "param CA");
			sca.nextLine();
			double[][] com = new double[num][num];
			for(int i=0; i<num; i++){
				sca.next();
				for(int j=0; j<num; j++){
					com[i][j] = sca.nextDouble();
				}
			}
			
			String dupline = pass(sca, "# Dups");
			int dups = Integer.parseInt(dupline.replaceAll("[^0-9]", ""));
			
			String chanline = pass(sca, "# Chans");
			int chans = Integer.parseInt(chanline.replaceAll("[^0-9]", ""));
			
			pass(sca, "# Actor Affinities");
			int[][] affine = new int[num][NUM_AFFINITIES];
			for(int p=0; p<num; p++){
				sca.next();
				for(int a=0; a<NUM_AFFINITIES; a++){
					affine[p][a] = sca.nextInt();
				}
			}
			
			pass(sca, "# Topology");
			int[][] gamma = new int[num][chans];
			for(int c=0; c<chans; c++){
				sca.next();//skip the #
				for(int a=0; a<num; a++){
					gamma[a][c] = sca.nextInt();
				}
			}
			
			sca.close();
			return new Topology2(gamma, com, dups, affine);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static NPM readMachine(File loc){
		try{
			Scanner sca = new Scanner(loc);
			
			sca.findWithinHorizon("param p := ", Integer.MAX_VALUE);
			int proc = Integer.parseInt(sca.next().replaceAll("[^0-9]", ""));
			
			pass(sca, "param CP");
			sca.nextLine();
			double[][] band = new double[proc][proc];
			for(int i=0; i<proc; i++){
				sca.next();
				for(int j=0; j<proc; j++){
					band[i][j] = sca.nextDouble();
				}
			}
			
			pass(sca, "# Processor Affinities");
			int[][] affine = new int[proc][NUM_AFFINITIES];
			for(int p=0; p<proc; p++){
				sca.next();
				for(int a=0; a<NUM_AFFINITIES; a++){
					affine[p][a] = sca.nextInt();
				}
			}
			
			sca.close();
			return new NPM(proc, band, affine);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String pass(Scanner sca, String pattern){
		String ln = sca.nextLine();
		while(!ln.contains(pattern) && sca.hasNextLine()){
			ln = sca.nextLine();
		}
		return ln;
	}
	
	public static boolean save(File loc, Problem prob){
		try{
			PrintStream ps = new PrintStream(new FileOutputStream(loc));
			printProblem(ps, prob);
			ps.println();
			ps.println("# Dups: " + prob.top.duplicates);
			ps.println();
			ps.println("# Chans: " + prob.top.chans.size());
			ps.println();
			printAffinities(ps, "Actor Affinities", prob.top.affinities);
			printAffinities(ps, "Processor Affinities", prob.mac.pAffinities);
			
			ps.println("# Topology");
			for(int c=0; c<prob.top.chans.size(); c++){
				ps.print("#");
				for(int a=0; a<prob.top.actors.size(); a++){
					ps.print(" " + prob.top.gamma[a][c]);
				}
				ps.println();
			}
			
			ps.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void printAffinities(PrintStream out, String group, int[][] affine) throws IOException{
		out.println("# " + group);
		for(int n=0; n<affine.length; n++){
			out.print("#");
			for(int a=0; a<affine[n].length; a++){
				out.print(" " + affine[n][a]);
			}
			out.println();
		}
		out.println();
	}
	
	public static void printProblem(PrintStream out, Problem prob) throws IOException{
		out.println("data;");
		out.println();
		out.println("param n := " + prob.top.actors.size() + ";");
		out.println();
		out.println("param p := " + prob.mac.numProcessors + ";");
		out.println();
		out.println("param AG :");
		for(int i=0; i<prob.top.actors.size(); i++){
			out.print("\t" + (i+1));
		}
		out.println(" :=");
		for(int i=0; i<prob.top.actors.size(); i++){
			out.print((i+1));
			for(int j=0; j<prob.top.actors.size(); j++){
				//this sequence works even when there is 1 dup
				out.print("\t" + (prob.top.isdup(i, j) ? "1" : "0"));
			}
			out.println();
		}
		out.println(";");
		
		out.println();
		printArray(out, "PI", prob.invoke);
		out.println();
		printArray(out, "CA", prob.top.communication);
		out.println();
		printArray(out, "CP", prob.mac.bandwidths);
	}
	
	private static void printArray(PrintStream out, String name, double[][] arr){
		int rws = arr.length;
		int cols = (rws == 0 ? 0 : arr[0].length);
		out.println("param " + name + " :");
		for(int c=0; c<cols; c++){
			out.print("\t" + (c+1));
		}
		out.println(" :=");
		for(int r=0; r<rws; r++){
			out.print(r+1);
			for(int c=0; c<cols; c++){
				out.print("\t" + roundFourDecimals(arr[r][c]));
			}
			out.println();
		}
		out.println(";");
	}
	
	public static int[][] genAffinities(int nbr, int min, int rng){
		int[][] ret = new int[nbr][];
		for(int i=0; i<nbr; i++){
			ret[i] = genAffinity(min, rng);
		}
		return ret;
	}
	public static int[] genAffinity(int min, int rng){
		int[] ret = new int[NUM_AFFINITIES];
		for(int i=0; i<NUM_AFFINITIES; i++){
			ret[i] = min + (int)Math.floor(Math.random()*rng);
		}
		return ret;
	}
	private static double[][] affine(int[][] proca, int[][] acta, double scale){
		double[][] ret = new double[acta.length][proca.length];
		for(int a=0; a<acta.length; a++){
			for(int p=0; p<proca.length; p++){
				double sm = 0;
				for(int i=0; i<NUM_AFFINITIES; i++){
					sm += (double)acta[a][i]/(double)proca[p][i];
				}
				ret[a][p] = roundFourDecimals(sm*scale);
			}
		}
		return ret;
	}
	public static double roundFourDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		return Double.valueOf(twoDForm.format(d));
	}
	
}
