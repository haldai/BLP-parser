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
	int headLen;
	int tailLen;
	myTerm[] head;
	myTerm[] tail;
	public Formular() {
		// TODO Auto-generated constructor stub
	}

}
