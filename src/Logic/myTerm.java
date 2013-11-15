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
	double weight = 0.0;
	boolean isPositive = true;
	
	public myTerm(String n, ArrayList<myWord> words) {
		pred = new Predicate(n, words.size());
		args = words.toArray(new myWord[words.size()]);
	}
	
	public myTerm(Predicate p, myWord[] words) {
		pred = p;
		args = words;
	}
	
	public myTerm(Predicate p, ArrayList<myWord> words) {
		pred = p;
		args = words.toArray(new myWord[words.size()]);
	}
	/**
	 * another realization of directly reading string into myWord and Predicate
	 * @param s: string to be parsed
	 */
	public myTerm(String s) {
		// find arguments
		Pattern p = Pattern.compile("\\(.*?\\)");
		Matcher m = p.matcher(s);
		boolean found = m.find();
		String[] tmp_args = null;
		List<myWord> buff_words = new ArrayList<myWord>();
		if (found) {
			String tmp_s = m.group();
			tmp_s = tmp_s.substring(1, tmp_s.lastIndexOf(')'));
			tmp_args = tmp_s.split("\\,");
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
	}
	
	public Predicate getPred() {
		return pred;
	}
	
	public String toString() {
		String pos = "";
		if (!this.isPositive())
			pos = "not: "; 
		String s = String.format("%s%s(", pos, pred.getName());
		for (myWord w : args) {
			s = s + w.toString() + ",";
		}
		if (s.endsWith(","))
			s = s.substring(0, s.length() - 1) + ")";
		return s;
	}
	
	public String toPrologString() {
		String pos = "";
		if (!this.isPositive()) {
			if (pred.getName().equals("=="))
				pos = " \\+";
			else
				pos = " \\+";
		}
		String s = String.format("%s(%s(", pos, pred.getName());
		for (myWord w : args) {
			s = s + w.toPrologString() + ",";
		}
		if (s.endsWith(","))
			s = s.substring(0, s.length() - 1) + "))";
		return s;
	}
	
	public myWord[] getArgs() {
		return args;
	}
	
	public myWord getArg(int i) {
		return args[i];
	}
	
	/**
	 * term substitution
	 * @param vars: to be substituted
	 * @param atms: to substitute
	 * @return substituted term
	 */
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
	
	public boolean equals(Object o) {
		if (!(o instanceof myTerm))
			return false;
		else {
			myTerm t = (myTerm) o;
			if (!this.pred.equals(t.pred))
				return false;
			else {
				for (int i = 0; i < this.pred.arity; i++) {
					if (!this.args[i].equals(t.getArg(i)))
						return false;
				}
			}
			return true;
		}
	}
	
	public void setWeight(double w) {
		weight = w;
	}
	
	public double getWeight() {
		return weight;
	}
	/**
	 * set this term as a positive term
	 */
	public void setPositive() {
		isPositive = true;
	}
	/**
	 * set this term to negative
	 */
	public void setNegative() {
		isPositive = false;
	}
	
	public boolean isPositive() {
		return isPositive;
	}
	
	public void flip() {
		if (isPositive)
			isPositive = false;
		else
			isPositive = true;
	}
}
