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
		if (!evaled.isEmpty())//evaled 不为空则为有解 evaled是关键！！！
			hasSolution = true;
		for (myTerm t : evaled) {//对每个输入的evaled
//			System.out.println(t.toString());
			if (t.isPositive()) {//evaled是正的
				if (label.contains(t)) {//找到evaled中在标签中有的，没有的话直接是负
					int idx = label.indexOf(t);//找到有的位置
					if (t.isPositive() ==  label.get(idx).isPositive())//对应的位置也要是一样的isPositive
						positive.add(t);
					else
						negative.add(t);
				}
				else {
					negative.add(t);
				}
			} else {
				if (!label.contains(t)) {//term为负时 如果term不在标签中 也算正 为啥？
					positive.add(t);
				}
				else {
					int idx = label.indexOf(t);
					if (t.isPositive() ==  label.get(idx).isPositive())//term为负 有标签 也是根据 标签的正负性是否和term的正负性一致
						positive.add(t);
					else
						negative.add(t);
				}
			}
		}
	}
	// TODO debug
	public void setSatisifySamplesProb(ArrayList<myTerm> label, LinkedList<myTerm> evaled) {//有概率的setSatisifySamples
		if (!evaled.isEmpty())
			hasSolution = true;
		for (myTerm t : evaled) {
//			System.out.println(t.toString());
			double prob = t.getWeight();
			if (t.isPositive()) {
				if (prob >= 0) {//概率>=0并且term为正 当做上面的term为正处理
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
				} else {//概率<0并且term为正 当做上面的term为负处理
					if (!label.contains(t)) {
						positive.add(t);
					}
					else {
						int idx = label.indexOf(t);
						if (label.get(idx).isPositive())
							negative.add(t);
						else
							positive.add(t);
					}
				}
			} else {
				if (prob >= 0) {//概率>=0并且term为负 当做上面的term为负处理
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
					if (label.contains(t)) {//概率<0并且term为负 当做上面的term为正处理
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
		positive.addAll(t);//全部置为正？
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
