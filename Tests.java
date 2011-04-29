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
		try{
			Topology top = Topology.loadTopology(new Scanner(new File("tests/2.in")));
			System.out.println(MachineSpecification.makeRandomSpec(top, 4, 0.2));
		}
		catch(Exception e){
			
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