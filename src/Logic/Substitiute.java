/**
 * 
 */
package Logic;

/**
 * @author Wang-Zhou
 *
 */
public class Substitiute {

	/**
	 *  Logical substitution
	 */
	public Substitiute() {
		// TODO Auto-generated constructor stub
	}
	
	public myTerm subsTerm(myTerm t, String[] s1, String[] s2) {
		if (s1.length != s2.length) {
			System.out.println("Subsititution error, length not match");
			return null;
		}
		String term = t.toString();
		for (int i = 0; i < s1.length; i++) {
			term = term.replaceAll(s1[i], s2[i]);
		}
		return new myTerm(term);
	}
	
	public myTerm[] subsTerms(myTerm[] t, String[] s1, String[] s2) {
		myTerm [] re = new myTerm[t.length];
		for (int i = 0; i < t.length; i++) {
			re[i] = subsTerm(t[i], s1, s2);
		}
		return re;
	}

}
