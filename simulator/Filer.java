package simulator;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Filer {
	
	public static final double INVOKE_SCALE = 0.3;
	
	public static void main(String[] args){
		File fi = new File("test.exp");
		System.out.println("saving test file to \"" + fi.getName() + "\"");
		Topology2 top =Generator.generateSimulated(false, false, 5, 3);
		NPM mac = new NPM(4);
		save(fi, top, mac);
		NPM readmac = readMachine(fi);
		Topology2 readtop = readTopology(fi);
		lnbreak(System.out);
		System.out.println(mac);
		lnbreak(System.out);
		System.out.println(readmac);
		lnbreak(System.out);
		System.out.println(top);
		lnbreak(System.out);
		System.out.println(readtop);
		
	}
	
	public static void lnbreak(PrintStream ps){
		ps.println("#-------------------");
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
			int[][] affine = new int[num][Mapper.NUM_AFFINITIES];
			for(int p=0; p<num; p++){
				sca.next();
				for(int a=0; a<Mapper.NUM_AFFINITIES; a++){
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
			int[][] affine = new int[proc][Mapper.NUM_AFFINITIES];
			for(int p=0; p<proc; p++){
				sca.next();
				for(int a=0; a<Mapper.NUM_AFFINITIES; a++){
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
	
	public static boolean save(File loc, Topology2 top, NPM mac){
		try{
			PrintStream ps = new PrintStream(new FileOutputStream(loc));
			printProblem(ps, top, mac);
			ps.println();
			ps.println("# Dups: " + top.duplicates);
			ps.println();
			ps.println("# Chans: " + top.chans.size());
			ps.println();
			printAffinities(ps, "Actor Affinities", top.affinities);
			printAffinities(ps, "Processor Affinities", mac.pAffinities);
			
			ps.println("# Topology");
			for(int c=0; c<top.chans.size(); c++){
				ps.print("#");
				for(int a=0; a<top.actors.size(); a++){
					ps.print(" " + top.gamma[a][c]);
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
	
	public static void printProblem(PrintStream out, Topology2 top, NPM mac) throws IOException{
		out.println("data;");
		out.println();
		out.println("param n := " + top.actors.size() + ";");
		out.println();
		out.println("param p := " + mac.numProcessors + ";");
		out.println();
		out.println("param AG :");
		for(int i=0; i<top.actors.size(); i++){
			out.print("\t" + (i+1));
		}
		out.println(" :=");
		for(int i=0; i<top.actors.size(); i++){
			out.print((i+1));
			for(int j=0; j<top.actors.size(); j++){
				//this sequence works even when there is 1 dup
				out.print("\t" + (top.isdup(i, j) ? "1" : "0"));
			}
			out.println();
		}
		out.println(";");
		
		out.println();
		printArray(out, "PI", Mapper.affine(mac.pAffinities, top.affinities, INVOKE_SCALE));
		out.println();
		printArray(out, "CA", top.communication);
		out.println();
		printArray(out, "CP", mac.bandwidths);
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
				out.print("\t" + Mapper.roundFourDecimals(arr[r][c]));
			}
			out.println();
		}
		out.println(";");
	}
	
}
