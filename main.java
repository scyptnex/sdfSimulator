import java.io.*;
import java.util.*;

public class main{
	public static void main(String[] args) throws Exception{
		System.out.println(Topology.loadTopology(new Scanner(new File("tests/0.in"))));
		System.out.println(Topology.loadTopology(new Scanner(new File("tests/1.in"))));
	}
}
