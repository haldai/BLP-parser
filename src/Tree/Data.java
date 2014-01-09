/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ILP.*;
import Logic.*;

/**
 * @author daiwz
 *
 */
public class Data {

	/**
	 * 总体数据
	 *
	 */ 
	ArrayList<ArrayList<myTerm>> labels = new ArrayList<ArrayList<myTerm>>(); // for each sentence
	ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	ArrayList<ArrayList<myTerm>> positive = new ArrayList<ArrayList<myTerm>>(); // for each sentence存储标签为正的term 何为正？
	ArrayList<ArrayList<myTerm>> negative = new ArrayList<ArrayList<myTerm>>(); // for each sentence
	Map<myTerm, Sentence> term_sent = new HashMap<myTerm, Sentence>();//term到句子的映射
	
	public Data() {}
	/**
	 * 把Document doc中的数据放在当前Data中
	 * @param doc
	 */
	public Data(Document doc) {//Document doc类型是啥？ 
		for (int i = 0; i < doc.length(); i++) {
			this.addData(doc.getLabel(i), doc.getSent(i));
		}
	}
	/**
	 * 依次把句子加入本data中
	 * @param label
	 * @param sent
	 */
	public void addData(ArrayList<myTerm> label, Sentence sent) {
		labels.add(label);
		sentences.add(sent);
		positive.add(new ArrayList<myTerm>());
		negative.add(new ArrayList<myTerm>());
	}
	
	public void addData(int i, ArrayList<myTerm> label, Sentence sent) {//加在第i位置
		labels.add(i, label);
		sentences.add(i, sent);
		positive.add(i, new ArrayList<myTerm>());
		negative.add(i, new ArrayList<myTerm>());
	}
	
	public void addData(ArrayList<myTerm> label, Sentence sent, SatisfySamples sat) {
		labels.add(label);
		sentences.add(sent);
		positive.add(new ArrayList<myTerm>());
		negative.add(new ArrayList<myTerm>());
		addSat(sent, sat);
	}
	/**
	 * 依次把句子加入本data中，在根据输入SatisfySamples sat把positive和negative分一下存下
	 * @param label
	 * @param sent
	 */
	public void addData(int i, ArrayList<myTerm> label, Sentence sent, SatisfySamples sat) {
		labels.add(i, label);
		sentences.add(i, sent);
		positive.add(i, new ArrayList<myTerm>());
		negative.add(i, new ArrayList<myTerm>());
		addSat(sent, sat);
	}
	
	public void addSat(Sentence sent, SatisfySamples sat) {
		for (myTerm t : sat.getPositive())
			addPositive(sent, t);
		for (myTerm t : sat.getNegative())
			addNegative(sent, t);
	}
	
	public void addPositive(int i, myTerm t) {
//		if (positive.get(i) == null)
//			positive.add(i, new ArrayList<myTerm>());
		positive.get(i).add(t);
		term_sent.put(t, sentences.get(i));
	}
	
	public void addNegative(int i, myTerm t) {
//		if (negative.get(i) == null)
//			negative.add(i, new ArrayList<myTerm>());
		negative.get(i).add(t);
		term_sent.put(t, sentences.get(i));
	}
	
	public void addPositive(Sentence s, myTerm t) {
		int i = sentences.indexOf(s);
		addPositive(i, t);
	}
	
	public void addNegative(Sentence s, myTerm t) {
		int i = sentences.indexOf(s);
		addNegative(i, t);
	}
	
	public ArrayList<ArrayList<myTerm>> getLabels() {
		return labels;
	}
	
	public ArrayList<myTerm> getLabel(int i) {
		return labels.get(i);
	}
	
	public ArrayList<Sentence> getSents() {
		return sentences;
	}
	
	public Sentence getSent(int i) {
		return sentences.get(i);
	}
	
	public ArrayList<ArrayList<myTerm>> getPositive() {
		return positive;
	}
	
	public ArrayList<myTerm> getPositive(int i) {
		return positive.get(i);
	}
	
	public ArrayList<ArrayList<myTerm>> getNegative() {
		return negative;
	}
	
	public ArrayList<myTerm> getNegative(int i) {
		return negative.get(i);
	}
	
	public int getNegativeSentSize() {
		int re = 0;
		for (ArrayList<myTerm> l : negative)
			if (!l.isEmpty())
				re++;
		return re;
	}
	
	public int getPositiveSentSize() {
		int re = 0;
		for (ArrayList<myTerm> l : positive)
			if (!l.isEmpty())
				re++;
		return re;
	}
	
	public int numAllNegative() {
		int re = 0;
		for (ArrayList<myTerm> ne : negative) {
			re = re + ne.size();
		}
		return re;
	}
	
	public int numAllPositive() {
		int re = 0;
		for (ArrayList<myTerm> po : positive) {
			re = re + po.size();
		}
		return re;
	}
	
	public int size() {
		return sentences.size();
	}
	/**
	 * 得到输入Term对应的句子
	 * @param t
	 * @return
	 */
	public Sentence getSentFromTerm(myTerm t) {
		return term_sent.get(t);
	}
	
	public String toString() {
		String re = "";
		for (Sentence sent : sentences) {
			re = re + String.format("%s\n", sent.toString());
		}
		return re;
	}

	public void addData(myTerm t, Sentence sent) {//针对myTerm t类型
		if (sentences.contains(sent)) {
			int s_idx = sentences.indexOf(sent);
			if (!labels.get(s_idx).contains(t)) {
				labels.get(s_idx).add(t);
				term_sent.put(t, sentences.get(s_idx));
			}
		} else {
			sentences.add(sent);
			labels.add(new ArrayList<myTerm>());
			labels.get(labels.size() - 1).add(t);
			positive.add(new ArrayList<myTerm>());
			negative.add(new ArrayList<myTerm>());
			term_sent.put(t, sent);
		}
	}
}
