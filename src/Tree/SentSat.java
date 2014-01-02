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
	ArrayList<ArrayList<myTerm>> labels = new ArrayList<ArrayList<myTerm>>();
	ArrayList<myTerm> allPos = new ArrayList<myTerm>();
	ArrayList<myTerm> allNeg = new ArrayList<myTerm>();
	
	/*
	 * SPLITTING FOR ROBUSTNESS
	 */
	
	Data covered = new Data();
	Data uncovered = new Data();
	
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
	
	public Sentence getCovSentFromTerm(myTerm t) {
		return covered.getSentFromTerm(t);
	}
	
	public Sentence getUncovSentFromTerm(myTerm t) {
		return uncovered.getSentFromTerm(t);
	}
	
	private void setAllNeg() {
		allNeg = new ArrayList<myTerm>();
		for (ArrayList<myTerm> tl : covered.negative) {
			for (myTerm t : tl) {
				allNeg.add(t);
			}
		}
	}
	
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
	public int getLabelNum() {
		int re = 0;
		for (ArrayList<myTerm> l : labels) {
			re = re + l.size();
		}
		return re;
	}

}
