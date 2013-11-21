/**
 * 
 */
package ILP;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;

/**
 * @author daiwz
 *
 */
public class SatisfySamples {

	/**
	 * restore the instances that satisfied by a rule L, departed into Negative and Positive samples
	 */
	
	ArrayList<myTerm> negative = new ArrayList<myTerm>();
	ArrayList<myTerm> positive = new ArrayList<myTerm>();
	private boolean hasSolution = false;

	public SatisfySamples() {}

	public void setSatisifySamples(ArrayList<myTerm> label, LinkedList<myTerm> evaled) {
		if (!evaled.isEmpty())
			hasSolution = true;
		for (myTerm t : evaled) {
//			System.out.println(t.toString());
			if (t.isPositive()) {
				if (label.contains(t)) {
					int idx = label.indexOf(t);
					if (t.isPositive() ==  label.get(idx).isPositive())
						positive.add(t);
					else
						negative.add(t);
				}
				else {
					negative.add(t);
				}
			} else {
				if (!label.contains(t)) {
					positive.add(t);
				}
				else {
					int idx = label.indexOf(t);
					if (t.isPositive() ==  label.get(idx).isPositive())
						positive.add(t);
					else
						negative.add(t);
				}
			}
		}
	}
	// TODO debug
	public void setSatisifySamplesProb(ArrayList<myTerm> label, LinkedList<myTerm> evaled, double prob) {
		if (!evaled.isEmpty())
			hasSolution = true;
		for (myTerm t : evaled) {
//			System.out.println(t.toString());
//			t.setWeight(prob);
			if (t.isPositive()) {
				if (prob >= 0.5) {
					if (label.contains(t)) {
						int idx = label.indexOf(t);
						if (label.get(idx).isPositive())
							positive.add(t);
						else
							negative.add(t);
					}
					else {
						negative.add(t);
					}
				} else {
					if (!label.contains(t)) {
						positive.add(t);
					}
					else {
						int idx = label.indexOf(t);
						if (label.get(idx).isPositive())
							positive.add(t);
						else
							negative.add(t);
					}
				}
			} else {
				if (prob >= 0.5) {
					if (!label.contains(t)) {
						positive.add(t);
					}
					else {
						int idx = label.indexOf(t);
						if (!label.get(idx).isPositive())
							positive.add(t);
						else
							negative.add(t);
					}
				} else {
					if (label.contains(t)) {
						int idx = label.indexOf(t);
						if (!label.get(idx).isPositive())
							negative.add(t);
						else
							positive.add(t);
					}
					else {
						negative.add(t);
					}
				}
			}
		}
	}
	
	public ArrayList<myTerm> getPositive() {
		return positive;
	}
	
	public ArrayList<myTerm> getNegative() {
		return negative;
	}
	
	public void pushPositive(myTerm t) {
		positive.add(t);
	}
	
	public void pushPositive(ArrayList<myTerm> t) {
		positive.addAll(t);
	}
	
	public void pushNegative(myTerm t) {
		negative.add(t);
	}
	
	public void pushNegative(ArrayList<myTerm> t) {
		negative.addAll(t);
	}
	
	public boolean hasSolution() {
		return hasSolution;
	}
}
