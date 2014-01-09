package Tree;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;

public class PathData {
	/*
	 * 存储一条路径的信息this is a data structure for one path
	 */
	ArrayList<myWord> varList = new ArrayList<myWord>(); // variable(subed word) list变量的list
	ArrayList<myWord> wordList = new ArrayList<myWord>(); // word(constant) list
	ArrayList<ArrayList<myTerm>> wordFeatList = new ArrayList<ArrayList<myTerm>>();//路径上的特征的list？？ 未替换变量
	ArrayList<myTerm> termList = new ArrayList<myTerm>(); // path路径上所有的term
	ArrayList<myTerm> subedTermList = new ArrayList<myTerm>(); // subed path被替换过的term（用真实值替换变量）
	
	myTerm out = new myTerm();
	boolean cls = false;
	
	int sent = 0; // this path belongs to which sentence所属的句子的编号
	
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
		for (int i = 0; i < out.getArgs().length; i++) {//用word（实际值）替换掉var（变量）
			myWord w = out.getArg(i);
			int j = varList.indexOf(w);
			out.setArg(i, wordList.get(j));
		}
		// add feature list
		for (myTerm f : sent.getFeatures()) {//把当前wordFeatList遍历一下
			if (wordList.contains(f.getArg(0))) {//可能是标签 word的list中包含标签的
				int idx = wordList.indexOf(f.getArg(0));//在word的list中位置
				wordFeatList.get(idx).add(f);//把找到的特征（特征的标签词在word的list有的），加入到和该词对应的位置
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
	/**
	 * 就是返回当前wordFeatList
	 * @return
	 */
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
