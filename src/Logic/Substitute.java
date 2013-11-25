/**
 * 
 */
package Logic;

import java.util.ArrayList;

/**
 * @author Wang-Zhou
 *
 */
public class Substitute {

	/**
	 *  Logical substitution for a set of logical myTerms
	 */
	ArrayList<myTerm> to_be_sub;
	ArrayList<myTerm> after_sub;
	ArrayList<myWord> word_list;
	ArrayList<myWord> var_list;
	
	public Substitute(ArrayList<myTerm> term_list) {
		to_be_sub = term_list;
		word_list = new ArrayList<myWord>();
		var_list = new ArrayList<myWord>();
		after_sub = subsTermList(to_be_sub);
	}
	/**
	 * TODO need to define a new object to return, include substituted, word_list and var_list
	 * @param term_list
	 * @return
	 */
	public ArrayList<myTerm> subsTermList(ArrayList<myTerm> term_list) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		// get word list and variable list
		int cnt = 1;
		for (myTerm t : term_list) {
			ArrayList<myWord> tmp_args = new ArrayList<myWord>(t.getArgs().length); 
			for (myWord w : t.getArgs()) {
				if (!word_list.contains(w)) {
					word_list.add(w);
					myWord tmp_var = new myWord(String.format("X_%d", cnt));
					var_list.add(tmp_var);
					tmp_args.add(var_list.get(cnt - 1));
					cnt++;
				} else {
					int p = word_list.indexOf(w);
					tmp_args.add(var_list.get(p));
				}
			}
			myTerm subed_term = new myTerm(t.getPred(), tmp_args.toArray(new myWord[tmp_args.size()]));
			if (!t.isPositive())
				subed_term.setNegative();
			re.add(subed_term);
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
	
	public ArrayList<myTerm> getOriginTerms() {
		return to_be_sub;
	}
	
	public ArrayList<myTerm> getSubTerms() {
		return after_sub;
	}
	
	public ArrayList<myWord> getWordList() {
		return word_list;
	}
	
	public ArrayList<myWord> getVarList() {
		return var_list;
	}
}
