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
	public String name;
	public int arity;
	
	public Predicate() {
		name = "";
		arity = 0;
	}
	
	public Predicate(String s, int n) {
		name = null;
		arity = 0;
	}
}
