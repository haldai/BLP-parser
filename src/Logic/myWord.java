/**
 * 
 */
package Logic;

import jpl.Atom;
import utils.*;
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
	 * 
	 * If meet word that start with upper case or number, simply add a "c" 
	 * in front of it
	 */
	String name;
	int num;
	String pos;
	
	public myWord(String s) {
		// Generative function of myWord(word_#_pos)
		String[] args = s.split("_");
		name = args[0];
		name = name.replaceAll("？", "question");
		name = name.replaceAll("。", "period");
		name = name.replaceAll("，", "comma");
		name = name.replaceAll("《", "bookLeft");
		name = name.replaceAll("》", "bookRight");
		name = name.replaceAll("（", "parLeft");
		name = name.replaceAll("）", "parRight");
		name = name.replaceAll("”", "quoteRight");
		name = name.replaceAll("“", "quoteLeft");
		name = name.replaceAll("’", "quoteRight");
		name = name.replaceAll("‘", "quoteLeft");
		name = name.replaceAll("、", "backslash");
		name = name.replaceAll("—", "minus");
		name = name.replaceAll("[?]", "question");
		name = name.replaceAll("[.]", "period");
		name = name.replaceAll(",", "comma");
		name = name.replaceAll("<", "lessThan");
		name = name.replaceAll(">", "largerThan");
		name = name.replaceAll("[(]", "parLeft");
		name = name.replaceAll("[)]", "parRight");
		name = name.replaceAll("\"", "quote");
//		name = name.replaceAll("\'", "quote");
//		name = name.replaceAll("\\\\", "backslash");
		name = name.replaceAll("/", "slash");
		name = name.replaceAll("[-]", "minus");
		if (args.length == 3) {
			num = Integer.parseInt(args[1]);
			pos = args[2];
		} else if (args.length == 2) {
			// is a prolog term
			if (args[0].equals("X") && utils.isNumeric(args[1])) {
				num = Integer.parseInt(args[1]);
				pos = "var";
			} else {
				num = 0;
				pos = args[1];
			}
		} else if (args.length == 1) {
			// is a single word
			num = 0;
			pos = "null";
		}
	}
	public myWord() {
		name = null;
		num = 0;
		pos = null;
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
	
	public String toString() {
		String str;
		if (!this.isVar())
			str = String.format("%s_%s_%s", name, num, pos);
		else
			str = String.format("%s_%s", name, pos);
		return str;
	}
	
	public String toPrologString() {
		char c = name.charAt(0);
		String new_name = name;
		if (Character.isDigit(c) || Character.isSpace(c)) {
		new_name = "d" + new_name;
		}
		new_name = new_name.replaceAll(" ", "SPACE");
		if (!this.isVar())
			return String.format("%s_%s", new_name, pos);
		else
			return String.format("%s_%d", new_name, num);
//		String str = String.format("%s_%s_%s", new_name, num, pos);
	}

	public Atom toAtom() {
		return new Atom(this.toString());
	}
	// to judge if the word is a variable
	public boolean isVar() {
		if ((name.equals("X")) && (pos == "var")) 
			return true;
		else return false;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof myWord))
			return false;
		else {
			myWord w = (myWord) o;
			if ((this.name.equals(w.getName())) && (this.num == w.getNum()) && (this.pos.equals(w.getPos())))
				return true;
			else
				return false;
		}
	}
}
