package simulator;

import java.util.*;

public interface Action {
	
	public void invoke(Actor parent, ArrayList<Object>[] inputs, ArrayList<Object>[] outputs);
	
}
