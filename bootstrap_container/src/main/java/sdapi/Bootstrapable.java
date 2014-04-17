package sdapi;

public interface Bootstrapable {

	abstract public void setRootDependency(Object o);
	abstract public void start();
}
