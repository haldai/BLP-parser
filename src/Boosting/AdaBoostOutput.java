/**
 * 
 */
package Boosting;

import java.util.ArrayList;

import Logic.*;
import Tree.*;
/**
 * @author daiwz
 *
 */
public class AdaBoostOutput {

	/**
	 * 
	 */
	ArrayList<ArrayList<Formula>> weakRules = new ArrayList<ArrayList<Formula>>();
	ArrayList<Double> ruleWeights = new ArrayList<Double>();
	
	public AdaBoostOutput() {}
	public ArrayList<ArrayList<Formula>> getWeakRules() {
		return weakRules;
	}
	
	public void addWeakRules(ArrayList<Formula> t, double w) {
		weakRules.add(t);
		ruleWeights.add(w);
	}
	
	public ArrayList<Double> getWeights() {
		return ruleWeights;
	}
	
	public void setWeakRules(ArrayList<ArrayList<Formula>> rules) {
		this.weakRules = rules;
	}
	
	public void setWeights(ArrayList<Double> weights) {
		this.ruleWeights = weights;
	}
}
