package simulator;

import java.util.*;

public interface Action {
	
	public static final int RETURN_OK = 0;
	public static final int RETURN_FAIL = -1;
	public static final int RETURN_DETECT = -2;
	
	public int invoke(String parent, ArrayList<Object[]> inputs, ArrayList<Object[]> outputs);
	
}
