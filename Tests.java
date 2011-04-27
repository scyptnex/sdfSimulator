import java.io.*;
import java.util.*;
import java.text.*;

public class Tests {
	
	public static void testAll(){
		testInform("Loading Topologies");
		testTopologyLoad();
		testInform("Specifying Machines");
		testMachineSpecification();
	}
	
	public static void testInform(String text){
		for(int i=0; i<text.length() + 6; i++) System.out.print("*");
		System.out.print("\n*");
		for(int i=0; i<text.length() + 4; i++) System.out.print(" ");
		System.out.println("*");
		System.out.println("*  " + text + "  *");
		System.out.print("*");
		for(int i=0; i<text.length() + 4; i++) System.out.print(" ");
		System.out.println("*");
		for(int i=0; i<text.length() + 6; i++) System.out.print("*");
		System.out.println();
	}
	
	public static void testMachineSpecification(){
		double[][] macspec = MachineSpecification.randPerturbInvoke(MachineSpecification.uniformInvoke(1.0, 5, 7), 0.9, 1.1, 6.0, 6.0);
		for(int a=0; a<5; a++){
			for(int p=0; p<7; p++){
				System.out.print(new DecimalFormat("0.00").format(macspec[a][p]) + "  ");
			}
			System.out.println();
		}
	}
	
	public static void testTopologyLoad(){
		File testDir = new File("tests");
		int counter = 0;
		for(File f : testDir.listFiles()){
			System.err.flush();
			System.out.flush();
			System.out.println("====================\n" + f.getName() + "\n====================");
			try{
				System.out.println(Topology.loadTopology(new Scanner(f)));
			}
			catch(Exception e){
				System.out.println("Failed to load " + counter);
				System.err.println("Failure: " + counter);
				e.printStackTrace();
				counter++;
			}
		}
	}
	
}