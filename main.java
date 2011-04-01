import java.io.*;
import java.util.*;

public class main{
	public static void main(String[] args) throws Exception{
		
		File testDir = new File("tests");
		for(File f : testDir.listFiles()){
			System.err.flush();
			System.out.flush();
			System.out.println("====================\n" + f.getName() + "\n====================");
			try{
				System.out.println(Topology.loadTopology(new Scanner(f)));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
