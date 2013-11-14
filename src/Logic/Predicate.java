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
	
	public Predicate(String s) {
		String[] t = s.split("\\/");
		name = t[0].toLowerCase();
		arity = Integer.parseInt(t[1]);
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
	
	public boolean equals(Object o) {
		if (!(o instanceof Predicate))
			return false;
		else {
			Predicate p = (Predicate) o;
			if ((this.name.equals(p.getName())) && (this.arity == p.getArity()))
				return true;
			else 
				return false;
		}
	}
	
	public String toString() {
		return String.format("%s/%d", name, arity);
	}
	
}
