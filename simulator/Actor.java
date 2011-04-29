package simulator;

public abstract class Actor {
	
	public void fire(){
		//here we may do something with duration of actors or some such
		this.work();
	}
	
	//Each actor is responsible for pushing and popping its own datas
	protected abstract void work();
	
}