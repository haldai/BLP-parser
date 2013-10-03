/**
 * 
 */
package Logic;

/**
 * @author daiwz
 *
 */
public class myWord {

	/**
	 * myWord is a word in input data, e.g. "刘德华_1_nr"，"刘德华"is name,
	 * "1" is its position in sentence, "nr" is its postag.
	 */
	String name;
	int num;
	String pos;
	String str;
	
	public myWord(String s) {
		// Generative function of myWord(word_#_pos)
		String[] args = s.split( "_");
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

}
