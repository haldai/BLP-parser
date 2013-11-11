/**
 * 
 */
package ILP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	Map<myTerm, Sentence> term_sent = new HashMap<myTerm, Sentence>();
	
	int allData = 0;
	int covData = 0;
	
	double coverage = 0.0;
	/**
	 * 
	 */
	public SentSat() {
		// TODO Auto-generated constructor stub
	}
	
	public void addSentSat(ArrayList<myTerm> label, Sentence sent, SatisfySamples sat) {
		allData++;
		if (!(sat.getNegative().size() == 0 && sat.getPositive().size() == 0)) {
			covData++;
			sents.add(sent);
			sats.add(sat);
			labels.add(label);
			for (myTerm t : sat.getNegative()){
				term_sent.put(t, sent);
			}
		}
	}
	
	public Sentence getWhichSent(myTerm t) {
		return term_sent.get(t);
	}
	
	private void setAllNeg() {
		allNeg = new ArrayList<myTerm>();
		for (SatisfySamples sat : sats) {
			for (myTerm t : sat.getNegative()) {
				allNeg.add(t);
			}
		}
	}
	
	private void setAllPos() {
		allPos = new ArrayList<myTerm>();
		for (SatisfySamples sat : sats) {
			for (myTerm t : sat.getPositive()) {
				allPos.add(t);
			}
		}
	}
	
	public ArrayList<myTerm> getAllPos() {
		return allPos;
	}
	
	public ArrayList<myTerm> getAllNeg() {
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
	
	public SatisfySamples toSatisfySamples() {
		SatisfySamples re = new SatisfySamples();
		re.pushPositive(allPos);
		re.pushNegative(allNeg);
		return re;
	}
	
	public ArrayList<Sentence> getAllSents() {
		return sents;
	}
	
	public Sentence getAllSent(int i) {
		return sents.get(i);
	}
	
	public ArrayList<SatisfySamples> getAllSats() {
		return sats;
	}
	
	public void setTotal() {
		setAllNeg();
		setAllPos();
		coverage = (double) covData/allData;
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
}
