/**
 * 
 */
package Logic;

import jpl.Atom;
/**
 * @author daiwz
 *
 */
public class myWord {

	/**
	 * myWord is a word in input data, e.g. "刘德华_1_nr"，"刘德华"is name,
	 * "1" is its position in sentence, "nr" is its POS tag.
	 * 
	 * Otherwise, the class myWorld also defines variable. When the "num" 
	 * position > 0 it is a constant, or atom; when it is less than 0 it 
	 * defines a variable, and the POS tag is "var", e.g. "X_-1_var, X_-2_var",
	 * -1 or -2 means different variables. In convenience, the "name" field 
	 * should always be "X". 
	 */
	String name;
	int num;
	String pos;
	String str;
	
	public myWord(String s) {
		// Generative function of myWord(word_#_pos)
		String[] args = s.split("_");
		name = args[0];
		num = Integer.parseInt(args[1]);
		pos = args[2];
		str = s;
	}
	public myWord() {
		name = null;
		num = -1;
		pos = null;
		str = null;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNum() {
		return num;
	}
	
	public String getPos() {
		return pos;
	}
	
	public String getStr() {
		return str;
	}

	public Atom toAtom() {
		return new Atom(str);
	}
	// to judge if the word is a variable
	public boolean isVar() {
		if ((name == "X") && (pos == "var") && (num < 0)) 
			return true;
		else return false;
	}
}
