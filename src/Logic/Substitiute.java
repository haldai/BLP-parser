/**
 * 
 */
package Logic;

import java.util.ArrayList;

/**
 * @author Wang-Zhou
 *
 */
public class Substitiute {

	/**
	 *  Logical substitution for a set of logical myTerms
	 */
	public Substitiute() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * TODO need to define a new object to return, include substituted, word_list and var_list
	 * @param term_list
	 * @return
	 */
	public ArrayList<myTerm> subsTermList(ArrayList<myTerm> term_list) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<myWord> word_list = new ArrayList<myWord>();
		ArrayList<myWord> var_list = new ArrayList<myWord>();
		int cnt = 1;
		for (myTerm t : term_list) {
			for (myWord w : t.getArgs()) {
				if (word_list.contains(w)) {
					word_list.add(w);
					var_list.add(new myWord(String.format("X_%d_var", cnt)));
					cnt++;
				}
			}
		}
		
		return re;
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
