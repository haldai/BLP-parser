package Tree;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;

public class PathData {
	/*
	 * this is a data structure for one path
	 */
	ArrayList<myWord> varList = new ArrayList<myWord>(); // variable(subed word) list
	ArrayList<myWord> wordList = new ArrayList<myWord>(); // word(constant) list
	ArrayList<ArrayList<myTerm>> wordFeatList = new ArrayList<ArrayList<myTerm>>();
	ArrayList<myTerm> termList = new ArrayList<myTerm>(); // path
	ArrayList<myTerm> subedTermList = new ArrayList<myTerm>(); // subed path
	
	myTerm out = new myTerm();
	boolean cls = false;
	
	int sent = 0; // this path belongs to which sentence
	
	public PathData(ArrayList<myWord> vars, ArrayList<myWord> words, Sentence sent, myTerm term) {
		// TODO Auto-generated constructor stub
		for (myWord v : vars) {
			varList.add(v.clone());
		}
		for (myWord w : words) {
			wordList.add(w.clone());
			wordFeatList.add(new ArrayList<myTerm>());
		}
		// set path output in this tree
		out = term.clone();
		for (int i = 0; i < out.getArgs().length; i++) {
			myWord w = out.getArg(i);
			int j = varList.indexOf(w);
			out.setArg(i, wordList.get(j));
		}
		// add feature list
		for (myTerm f : sent.getFeatures()) {
			if (wordList.contains(f.getArg(0))) {
				int idx = wordList.indexOf(f.getArg(0));
				wordFeatList.get(idx).add(f);
			}
		}
	}
	
	public void setSentNum(int n) {
		sent = n;
	}
	
	public void setClass(boolean b) {
		cls = b;
	}
	
	public myTerm getLabel() {
		return out;
	}

	public ArrayList<myWord> getWordList() {
		return this.wordList;
	}
	
	public ArrayList<myWord> getVarList() {
		return this.varList;
	}
	
	public ArrayList<ArrayList<myTerm>> getWordFeatures() {
		return this.wordFeatList;
	}

	public ArrayList<myTerm> getWordFeature(int i) {
		return this.wordFeatList.get(i);
	}

	public myWord getVar(int i) {
		return this.varList.get(i);
	}
	
	public String toString() {
		String re = "";
		for (int i = 0; i < this.wordList.size(); i++) {
			re = re + this.varList.get(i).toPrologString() + "=" + this.wordList.get(i).toString() + ", ";
		}
		re = re.substring(0, re.length() - 2) + ".";
		return re;
	}
	
	public boolean cls() {
		return cls;
	}
}
