/**
 * 
 */
package Logic;

/**
 * @author Wang-Zhou
 *
 */
public class LogicProgram {

	/**
	 * LogicProgram is a set of (probabilistic) logic rules
	 */
	
	Formular[] rules;
	int length;
	
	public LogicProgram() {
		rules = null;
		length = 0;
	}
	
	public LogicProgram(Formular[] p) {
		// TODO Auto-generated constructor stub
		rules = p;
		length = rules.length;
	}
	
	public int length() {
		return length;
	}
	
	public void addRule(Formular f) {
		Formular[] p = rules.clone();
		rules = new Formular[length + 1];
		length = length + 1;
		for (int i = 0; i < length - 1; i++) {
			rules[i] = p[i];
		}
		rules[length - 1] = f;
		p = null;
	}
	
	public void removeLast() {
		Formular[] p = rules.clone();
		rules = new Formular[length - 1];
		length = length - 1;
		for (int i = 0; i < length; i++) {
			rules[i] = p[i];
		}
		p = null;
	}
	
	public Formular[] getRules() {
		return rules;
	}
	
	public Formular getRule(int i) {
		return rules[i];
	}
}
