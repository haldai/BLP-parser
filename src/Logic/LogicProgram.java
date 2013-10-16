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
	
	Formula[] rules;
	int length;
	
	public LogicProgram() {
		length = 0;
	}
	
	public LogicProgram(Formula[] p) {
		// TODO Auto-generated constructor stub
		rules = p;
		length = rules.length;
	}
	
	public int length() {
		return length;
	}
	
	public void addRule(Formula f) {
		Formula[] p = rules;
		rules = new Formula[length + 1];
		length = length + 1;
		for (int i = 0; i < length - 1; i++) {
			rules[i] = p[i];
		}
		rules[length - 1] = f;
		p = null;
	}
	
	public void removeLast() {
		Formula[] p = rules.clone();
		rules = new Formula[length - 1];
		length = length - 1;
		for (int i = 0; i < length; i++) {
			rules[i] = p[i];
		}
		p = null;
	}
	
	public Formula[] getRules() {
		return rules;
	}
	
	public Formula getRule(int i) {
		return rules[i];
	}
}
