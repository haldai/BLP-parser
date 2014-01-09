/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ILP.SatisfySamples;
import Logic.*;

/**
 * @author daiwz
 *
 */
public class SentSat {
	
	ArrayList<Sentence> sents = new ArrayList<Sentence>();
	ArrayList<SatisfySamples> sats = new ArrayList<SatisfySamples>();
	ArrayList<ArrayList<myTerm>> labels = new ArrayList<ArrayList<myTerm>>();//为啥两层list lable和term有啥关系？  第一层句子 第二层 每个lable 为了多步推理
	ArrayList<myTerm> allPos = new ArrayList<myTerm>();
	ArrayList<myTerm> allNeg = new ArrayList<myTerm>();
	
	/*
	 * SPLITTING FOR ROBUSTNESS
	 */
	
	Data covered = new Data();//coverd被二叉树的叉分开的某一支上的数据
	Data uncovered = new Data();//被二叉树的叉分开的另一支上的数据
	
	double coverage = 0.0;
	double accuracy = 0.0;
	/**
	 * 
	 */
	public SentSat() {}
	
	public SentSat(int len) {
		ArrayList<Sentence> sents = new ArrayList<Sentence>(len);
		ArrayList<SatisfySamples> sats = new ArrayList<SatisfySamples>(len);
		ArrayList<ArrayList<myTerm>> labels = new ArrayList<ArrayList<myTerm>>(len);
		for (int i = 0; i < len; i++) {
			labels.add(new ArrayList<myTerm>());
		}
		ArrayList<myTerm> allPos = new ArrayList<myTerm>();
		ArrayList<myTerm> allNeg = new ArrayList<myTerm>();
	}
	/**
	 * 把输入存在此，并根据 SatisfySamples sat是否hasSolution 分为covered和uncovered？？为啥根据这个
	 * @param label
	 * @param sent
	 * @param sat
	 */
	public void addSentSat(ArrayList<myTerm> label, Sentence sent, SatisfySamples sat) {
		labels.add(label);
		sents.add(sent);
		sats.add(sat);
		if (sat.hasSolution()) {
			// covered
			covered.addData(label, sent, sat);
		} else
			uncovered.addData(label, sent, sat);
	}
	
	/**
	 * 得到输入Term对应的covered中的句子
	 * @param t
	 * @return
	 */
	public Sentence getCovSentFromTerm(myTerm t) {
		return covered.getSentFromTerm(t);//得到输入Term对应的句子
	}
	
	/**
	 * 得到输入Term对应的uncovered中的句子
	 * @param t
	 * @return
	 */
	public Sentence getUncovSentFromTerm(myTerm t) {
		return uncovered.getSentFromTerm(t);
	}
	/**
	 * 把coverd/uncoverd中的正负性加入到allNeg/allPos中
	 */
	private void setAllNeg() {
		allNeg = new ArrayList<myTerm>();
		for (ArrayList<myTerm> tl : covered.negative) {
			for (myTerm t : tl) {
				allNeg.add(t);
			}
		}
	}
	
	/**
	 * 把coverd/uncoverd中的正负性加入到allNeg/allPos中
	 */
	private void setAllPos() {
		allPos = new ArrayList<myTerm>();
		for (ArrayList<myTerm> tl : covered.positive) {
			for (myTerm t : tl) {
				allPos.add(t);
			}
		}
	}
	
	public ArrayList<myTerm> getAllPos() {
		setAllPos();
		return allPos;
	}
	
	public ArrayList<myTerm> getAllNeg() {
		setAllNeg();
		return allNeg;
	}
	
	public myTerm getAllNeg(int i) {
		return allNeg.get(i);
	}
	
	public myTerm getAllPos(int i) {
		return allPos.get(i);
	}
	
	public int getAllPosNum() {
		return allPos.size();
	}
	
	public int getAllNegNum() {
		return allNeg.size();
	}
	
//	public SatisfySamples toSatisfySamples() {
//		SatisfySamples re = new SatisfySamples();
//		re.pushPositive(allPos);
//		re.pushNegative(allNeg);
//		return re;
//	}
	
	public ArrayList<Sentence> getAllSents() {
		return sents;
	}
	
	public Sentence getAllSent(int i) {
		return sents.get(i);
	}
	
	public ArrayList<SatisfySamples> getAllSats() {
		return sats;
	}
	
	public SatisfySamples getAllSats(int i) {
		return sats.get(i);
	}
	
	/**
	 * 把coverd/uncoverd中的正负性加入到allNeg/allPos中，并算准确率
	 */
	public void setTotal() {
		setAllNeg();
		setAllPos();
		coverage = (double) covered.size()/(uncovered.size() + covered.size());
		double p = this.getAllPosNum();
		double t = this.getAllNegNum() + this.getAllPosNum();
		if (t == 0.0)
			t = p = 0.000000000000001;
		accuracy = p/t;
	}
	
	public double getCov() {
		return coverage;
	}
	
	public ArrayList<ArrayList<myTerm>> getAllLables() {
		return labels;
	}
	
	public ArrayList<myTerm> getAllLabel(int i) {
		return labels.get(i);
	}
	
	public Data getCoveredData() {
		return covered;
	}
	
	public Data getUncoveredData() {
		return uncovered;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	/**
	 * lables中每个元素的Term的size
	 * @return
	 */
	public int getLabelNum() {
		int re = 0;
		for (ArrayList<myTerm> l : labels) {
			re = re + l.size();
		}
		return re;
	}

}
