/**
 * 
 */
package Logic;

import java.util.ArrayList;

/**
 * @author daiwz
 *
 */
public class CommonPredicates {
	/**
	 * 
	 */
	public myTerm prologEqual(myWord arg1, myWord arg2) {
		Predicate eq = new Predicate("==", 2);
		ArrayList<myWord> args = new ArrayList<myWord>(2);
		args.add(arg1);
		args.add(arg2);
		return new myTerm(eq, args);
	}
}
