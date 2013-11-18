/**
 * 
 */
package Boosting;

import java.util.ArrayList;

import Tree.*;
/**
 * @author daiwz
 *
 */
public class AdaBoostOutput {

	/**
	 * 
	 */
	ArrayList<RuleTree> weakRules = new ArrayList<RuleTree>();
	ArrayList<Double> ruleWeights = new ArrayList<Double>();
	
	public AdaBoostOutput() {}
	public ArrayList<RuleTree> getWeakRules() {
		return weakRules;
	}
	
	public void addRule(RuleTree t, double w) {
		weakRules.add(t);
		ruleWeights.add(w);
	}
	
	public ArrayList<Double> getWeights() {
		return ruleWeights;
	}
	
	public void setWeakRules(ArrayList<RuleTree> trees) {
		this.weakRules = trees;
	}
	
	public void setWeights(ArrayList<Double> weights) {
		this.ruleWeights = weights;
	}
}
