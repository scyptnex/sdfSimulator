import java.text.DecimalFormat;

public class MachineSpecification {
	
	public final int numProcessors;
	public final Topology myTop;
	public final double[][] invokeTime;//actor-processor
	public final double[][] communication;//processor-processor
	
	//generates a random spec
	//rdmness is the deviation from 1 of all invoke times
	public static MachineSpecification makeRandomSpec(Topology top, int numProc, double rdmness) throws Exception{
		return new MachineSpecification(randPerturbInvoke(uniformInvoke(1.0, top.numActors, numProc), 1.0-rdmness, 1.0+rdmness, 0, 0), top);
	}
	
	//constructs and returns a uniform arroy for invocation time testing
	public static double[][] uniformInvoke(double t, int numAct, int numProc){
		double[][] ret = new double[numAct][numProc];
		for(int a=0; a<numAct; a++){
			for(int p=0; p<numProc; p++){
				ret[a][p] = t;
			}
		}
		return ret;
	}
	
	//randomly perturbs an invocation time array
	//the time will be multiplied by some random ammount between minFac and maxFac
	//the time will then be added to some random ammount between minAdd and maxAdd
	//to perturb all ammounts by nothing the invocation is randPerturbInvoke(it, 1, 1, 0, 0);
	//returns it even though it modifies it on the fly
	public static double[][] randPerturbInvoke(double[][] it, double minFac, double maxFac, double minAdd, double maxAdd){
		for(int a=0; a<it.length; a++){
			for(int p=0; p<it[a].length; p++){
				double fr = minFac + Math.random()*(maxFac-minFac);
				double ar = minAdd + Math.random()*(maxAdd-minAdd);
				it[a][p] = (it[a][p]*fr)+ar;
			}
		}
		return it;
	}
	
	public MachineSpecification(double[][] it, Topology top) throws Exception{
		//ensure there is at least 1 actor
		if(it.length != top.numActors || it.length == 0){
			throw new Exception("Either the topology has no actors or the invocation times are empty");
		}
		//ensure there is at least 1 processor
		numProcessors = it[0].length;
		if(numProcessors == 0){
			throw new Exception("You have supplied 0 processor");
		}
		//ensure the matrix is rectangular
		for(int i=1; i<top.numActors; i++){
			if(it[i].length != numProcessors){
				throw new Exception("You have supplied 0 processors");
			}
		}
		invokeTime = it;
		communication = null;
		myTop = top;
	}
	
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("Num Processors: " + numProcessors + " for " + myTop.numActors + " actors\n");
		for(int a=0; a<invokeTime.length; a++){
			for(int p=0; p<invokeTime[a].length; p++){
				ret.append(new DecimalFormat("0.00").format(invokeTime[a][p]) + " ");
			}
			ret.append("\n");
		}
		return ret.toString();
	}
	
}