/**
 * 
 */
package Logic;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Wang-Zhou
 *
 */
public class LogicProgram {

	/**
	 * LogicProgram is a set of (probabilistic) logic rules
	 */
	
	ArrayList<Formula> rules = new ArrayList<Formula>();
	ArrayList<Predicate> headPred = new ArrayList<Predicate>();//是标签的吗
	ArrayList<Predicate> bodyPred = new ArrayList<Predicate>();
	int length;
	
	public LogicProgram() {
		length = 0;
	}
	
	public LogicProgram(ArrayList<Formula> p) {
		rules = p;
		length = rules.size();
		// find out all query predicates
		for (Formula r : rules) {
			addPredicate(r);
		}
	}
	
	public int length() {
		return length;
	}
	
	public void addRule(Formula f) {
		rules.add(f);
		addPredicate(f);
	}
	
	public void removeLast() {
		rules.remove(rules.size() - 1);
	}
	
	public ArrayList<Formula> getRules() {
		return rules;
	}
	
	public Formula getRule(int i) {
		return rules.get(i);
	}
	
	public ArrayList<Predicate> getHeadPred() {
		return headPred;
	}
	
	public ArrayList<Predicate> getBodyPred() {
		return bodyPred;
	}

	private void addPredicate(Formula r) {
		ArrayList<Predicate> buff_preds = new ArrayList<Predicate>();
		for (myTerm t : r.getHead()) {
			if (!buff_preds.contains(t.getPred())) {
//				System.out.println(t.getPred().getName() + '/' + t.getPred().getArity());
				buff_preds.add(t.getPred());
			}
		}
		for (Predicate ps : buff_preds) {
			if (!headPred.contains(ps))
				headPred.add(ps);
		}
		buff_preds = null;
		buff_preds = new ArrayList<Predicate>();
		for (myTerm t : r.getBody()) {
			if (!buff_preds.contains(t.getPred())) {
//				System.out.println(t.getPred().getName() + '/' + t.getPred().getArity());
				buff_preds.add(t.getPred());
			}
		}
		for (Predicate ps : buff_preds) {
			if (!bodyPred.contains(ps))
				bodyPred.add(ps);
		}
		buff_preds = null;
	}

	public void addRules(LinkedList<Formula> rs) {
		for (Formula r : rs)
			this.addRule(r);
	}

	public void addRules(ArrayList<Formula> rs) {
		for (Formula r : rs)
			this.addRule(r);
	}
}
