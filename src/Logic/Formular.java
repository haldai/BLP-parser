/**
 * 
 */
package Logic;

/**
 * @author Wang-Zhou Dai
 *
 */

import jpl.Variable;

public class Formular {

	/**
	 * Logic formular in prolog form, e.g., "grandfather(X, Y):-father(X,Z),father(Z,Y)."
	 */
	
	private double weight;
	private int headLen;
	private int tailLen;
	private myTerm[] head;
	private myTerm[] tail;
	private String str;
	
	public Formular() {
		// TODO deal with variables !!!
		weight = 0;
		headLen = 0;
		tailLen = 0;
		head = null;
		tail = null;
		str = "";
	}
	
	public Formular(double w, myTerm[] h, myTerm[] t) {
		weight = w;
		headLen = h.length;
		tailLen = t.length;
		head = h;
		tail = t;
		// TODO turn to str
	}
	
	public void pushTail() {
		
	}
	
	public void pushHead() {
		
	}
	
	public void popTail() {
		
	}
	
	public void popHead() {
		
	}

}
