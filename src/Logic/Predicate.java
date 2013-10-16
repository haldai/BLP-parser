/**
 * 
 */
package Logic;

/**
 * @author daiwz
 *
 */
public class Predicate {

	/**
	 * Logic Predicate, with its arity, e.g., "att/2" means att(_,_).
	 */
	String name;
	int arity;
	
	public Predicate() {
		name = "";
		arity = 0;
	}
	
	public Predicate(String s, int n) {
		name = s.toLowerCase();
		arity = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public int getArity() {
		return arity;
	}
	
	public void setArity(int a) {
		arity = a;
	}
	
	public boolean equals(Predicate p) {
		if ((this.name.equals(p.name)) && (this.arity == p.arity))
			return true;
		else 
			return false;
	}
}
