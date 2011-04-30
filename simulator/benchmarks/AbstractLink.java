package simulator.benchmarks;

import java.util.*;

import simulator.*;

public class AbstractLink extends Topology.Link{

	private Queue<Object> q;

	public AbstractLink(int idex, AbstractActor prod, int pam, AbstractActor con, int cam){
		super(idex, prod, pam, con, cam);
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
