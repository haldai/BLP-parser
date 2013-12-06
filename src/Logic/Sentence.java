/**
 * 
 */
package Logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daiwz
 *
 */
public class Sentence {

	/**
	 * Sentence is a bunch of dependencies, i.e., myTerms, and 
	 * its word list
	 */
	int termLen = 0;
	int wordLen = 0;
	myTerm[] termList;
	myWord[] wordList;
	ArrayList<myTerm> featList = new ArrayList<myTerm>();
	
	public Sentence(int t_len, int w_len) {
		termLen = t_len;
		wordLen = w_len;
		termList = new myTerm[t_len];
		wordList = new myWord[w_len];
	}
	
	public Sentence(String deps) {
		// read sentence from string
		if (deps.endsWith("."))
			deps = deps.substring(0, deps.length() - 1);
		Pattern p = Pattern.compile("\\(.*?\\)");
		String[] dep = deps.split(";");
		termLen = dep.length;
		// use dynamic array to initialize the termlist and wordlist
		List<myWord> wd = new ArrayList<myWord>();
		List<myTerm> tm = new ArrayList<myTerm>();
		// processing each term
		List<Integer> l = new ArrayList<Integer>();
		Map<Integer, myWord> map = new HashMap<Integer, myWord>();
		l.add(-1);
		for (int i = 0; i < dep.length; i++) {
//			System.out.print(i);
//			System.out.println(dep[i]);
			List<myWord> arg_words = new ArrayList<myWord>();
			Matcher m = p.matcher(dep[i]);
			boolean found = m.find(); // if matched (content)
			if (found) {
				String args = m.group();
				args = args.substring(1, args.length() - 1);
//				System.out.println(dep[i]);
				String[] words = args.split(",");
				for (int j = 0; j < words.length; j++) {
					myWord tmp = new myWord(words[j]);
					arg_words.add(tmp);
					if (!l.contains(tmp.num)) {
						wd.add(tmp);
						l.add(tmp.num);
						map.put(tmp.num, tmp);
					}
				}
				tm.add(new myTerm(dep[i]));
				arg_words = null;
			} else {
				System.out.println("Parsing dependency error in Sentence:" + deps);
				System.exit(1);
			}
		}
//		System.out.println(l.toString());
		Collections.sort(l);
		myTerm[] buff_terms = new myTerm[tm.size()];
		myWord[] buff_words = new myWord[wd.size()];
		for (int i = 0; i < tm.size(); i++ ) {
			buff_terms[i] = tm.get(i);
		}
		
		for (int i = 0 ; i < wd.size(); i++) {
//			System.out.println(wd.get(i));
//			System.out.println(l.get(i+1) - 1);
//			buff_words[l.get(i + 1) - 1] = wd.get(i);
			buff_words[i] = map.get(l.get(i + 1));
		}
		
		
		
		tm = null;
		wd = null;
		termList = buff_terms;
		wordList = buff_words;
		wordLen = wordList.length;
		buildFeature();
//		for (int i = 0; i < buff_words.length; i++) {
//			System.out.println(wordList[i].str);
//		}
	}
	
	public int termLen() {
		return termLen;
	}
	
	public int wordLen() {
		return wordLen;
	}
	
	public myTerm getTerm(int num) {
		return termList[num];
	}
	
	public myWord getWord(int num) {
		return wordList[num];
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < wordLen; i++) {
			s = s + wordList[i].name;
		}
		return s;
	}
	
	public myTerm[] getTerms() {
		return termList;
	}
	
	public myWord[] getWords() {
		return wordList;
	}
	/**
	 * get a list of predicates in array list of terms
	 * @param terms: input terms
	 * @return: array list of predicates
	 */
	public ArrayList<Predicate> getAllPreds(ArrayList<myTerm> terms) {
		ArrayList<Predicate> buff_preds = new ArrayList<Predicate>();
		for (myTerm t : terms) {
			if (!buff_preds.contains(t.getPred())) {
//				System.out.println(t.getPred().getName() + '/' + t.getPred().getArity());
				buff_preds.add(t.getPred());
			}
		}
		return buff_preds;
	}
	
	/**
	 * get feature from current sentence
	 * @param sent: input sentence
	 * @return: list of feature term
	 */
	private void buildFeature() {
		// POSTAG feature
		for (myWord w : this.getWords()) {
			myTerm tmp_term = new CommonPredicates().posTag(w, w.toPostagWord());
			if (!featList.contains(tmp_term))
				featList.add(tmp_term);
		}
		// TODO More
	}
	
	public ArrayList<myTerm> getFeatures() {
		return featList;
	}
	
	public myTerm getFeature(int i) {
		return featList.get(i);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Sentence))
			return false;
		else {
			Sentence s = (Sentence) o;
			if (s.termLen != this.termLen || s.wordLen != this.wordLen)
				return false;
			else {
				for (int i = 0; i < s.termLen; i++)
					if (!s.getTerm(i).equals(this.getTerm(i)))
						return false;
				for (int i = 0; i < s.wordLen; i++)
					if (!s.getWord(i).equals(this.getWord(i)))
						return false;
				return true;
			}
		}
	}

	public String toTermString() {
		// TODO Auto-generated method stub
		String s = "";
		for (myTerm t : this.termList) {
			s = s + t.toString() + ";";
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}
	
}
