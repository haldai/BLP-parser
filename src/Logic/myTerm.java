/**
 * 
 */
package Logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	public myTerm(Predicate p, myWord[] words) {
		pred = p;
		args = words;
		String s = String.format("%s(", pred.name);
		for (int i = 0; i < words.length; i++) {
			s = s + String.format("%s,", words[i].name);
		}
		if (s.charAt(s.length() - 1) == ',') {
				str = s.substring(0, s.length() - 1);
		}
	}
	// another realization of directly reading string into myWord and Predicate
	public myTerm(String s) {
		str = s;
		// find arguments
		Pattern p = Pattern.compile("\\(.*?\\)");
		Matcher m = p.matcher(s);
		boolean found = m.find();
		String[] tmp_args = null;
		List<myWord> buff_words = new ArrayList<myWord>();
		if (found) {
			tmp_args = m.group().split("\\,");
			for (int i = 0; i < tmp_args.length; i++) {
				buff_words.add(new myWord(tmp_args[i]));
			}
			args = new myWord[buff_words.size()];
			for (int i = 0; i < buff_words.size(); i++) {
				args[i] = buff_words.get(i);
			}
		} else {
			System.out.println("arguments of " + s + " not found!");
			System.exit(0);
		}
		pred = new Predicate(s.split("\\(")[0], tmp_args.length);
	}
	
	public myTerm() {
		pred = null;
		args = null;
		str = null;
	}
	
	public Predicate getPred() {
		return pred;
	}
	
	public String getStr() {
		return str;
	}
	
	public myWord[] getArgs() {
		return args;
	}
	
	// term substitution
	public myTerm substitution(myWord[] vars, myWord[] atms) {
		myWord[] a = this.args;
		List<myWord> varList = Arrays.asList(vars);
		for (int i = 0; i < a.length; i++) {
			if (varList.contains(a[i])) {
				int pos = varList.indexOf(a[i]);
				a[i] =  atms[pos];
			}
		}
		return new myTerm(this.pred, a);
	}
}
