/**
 * 
 */
package Logic;


/**
 * @author daiwz
 *
 */
public class myTerm {

	/**
	 * myTerm is a term type, which have a predicate and some arguments.
	 * e.g. "att(刘德华_1_nr, 老婆_2_n)"，"att" is name, the else in brackets 
	 * are words/arguments.
	 */
	Predicate pred;
	myWord[] args;
	String str;
	
	public myTerm(String n, myWord[] words) {
		pred = new Predicate(n, words.length);
		args = words;
		String s = String.format("%s(", pred.name);
		for (int i = 0; i < words.length; i++) {
			s = s + String.format("%s,", words[i].name);
		}
		if (s.charAt(s.length() - 1) == ',') {
				str = s.substring(0, s.length() - 1);
		}
	}
	
	public myTerm() {
		pred = null;
		args = null;
		str = null;
	}
}
