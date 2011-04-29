package simulator;

import java.util.*;

public class Link {
	
	private Queue<Object> q;
	
	public Link(){
		q = new LinkedList<Object>();
	}
	
	public void add(Object ob){
		q.offer(ob);
	}
	
	public Object get(){
		return q.poll();
	}
	
	public Object peek(){//for convenience
		return q.peek();
	}
	
	public int fillState(){
		return q.size();
	}
	
}
