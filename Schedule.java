public class Schedule {
	
	public static class Machine{
		public final Sequence[] sequences;
		
	}
	
	public static class Sequence{
		public final int[] invocationOrder;//lists the invocations of actors
		
		public Sequence(int[] ord){
			invocationOrder = ord;
		}
	}
	
}