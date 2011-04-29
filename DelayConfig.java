import java.util.*;

public class DelayConfig {
	
	public final Topology myTop;
	public final int[] delays;
	
	public static DelayConfig loadDelays(Topology top, Scanner del){
		try{
			int[] ds = new int[top.numLinks];
			for(int i=0; i<ds.length; i++){
				ds[i] = del.nextInt();
			}
			return new DelayConfig(top, ds);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public DelayConfig(Topology t, int[] dels) throws Exception{
		if(t.numLinks != dels.length) throw new Exception("Invalid daly configuration, not enough links: top=" + t.numLinks + " delay=" + dels.length);
		myTop = t;
		delays = dels;
		
	}
	
}