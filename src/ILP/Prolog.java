/**
 * 
 */
package ILP;

import jpl.JPL;
import jpl.PrologException;
import jpl.Query;
import Logic.Predicate;

/**
 * @author Wang-Zhou
 *
 */
public class Prolog {

	/**
	 * 
	 */
	public Prolog() {
		// TODO Auto-generated constructor stub
		JPL.init();
	}
	
	public void assertz(String t) {
		String clause;
		clause = String.format("assertz(%s).", t);
//		System.out.println(clause);
		Query q = new Query(clause);

		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		}
	}
	
	public void retract(String t) {
		String clause;
		clause = String.format("retract(%s).", t);
		Query q = new Query(clause);
		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		}
	}
	
	public void retractAll(String t) {
		String clause;
		clause = String.format("retractall(%s).", t);
		Query q = new Query(clause);
		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		}
	}
	
	public void dynamic(Predicate p) {
		String clause;
		clause = String.format("dynamic(%s/%d)", p.getName(), p.getArity());
		Query q = new Query(clause);
		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		} 
	}

}
