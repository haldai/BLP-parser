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
	ArrayList<myTerm> featList = new ArrayList<myTerm>();//特征  这个注意下 貌似是句子专有的
	
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
		String[] dep = deps.split(";");//句子的每个特征？ 用;隔开
		termLen = dep.length;
		// use dynamic array to initialize the termlist and wordlist  这就是望洲说的dynamic 不明觉厉
		List<myWord> wd = new ArrayList<myWord>();
		List<myTerm> tm = new ArrayList<myTerm>();
		// processing each term
		List<Integer> l = new ArrayList<Integer>();
		Map<Integer, myWord> map = new HashMap<Integer, myWord>();
		l.add(-1);
		for (int i = 0; i < dep.length; i++) {//对于每个特征 一阶谓词
//			System.out.print(i);
//			System.out.println(dep[i]);
			List<myWord> arg_words = new ArrayList<myWord>();
			Matcher m = p.matcher(dep[i]);//这种mathc方法 注意啊 我不会
			boolean found = m.find(); // if matched (content)
			if (found) {
				String args = m.group();
				args = args.substring(1, args.length() - 1);//为蛤第一个不要了 哦 他是谓词吧
//				System.out.println(dep[i]);
				String[] words = args.split(",");
				for (int j = 0; j < words.length; j++) {//对于word
					myWord tmp = new myWord(words[j]);
					arg_words.add(tmp);
					if (!l.contains(tmp.num)) {//就是单纯的add吧 这个判断貌似是去重的 得到uniq的word和map
						wd.add(tmp);
						l.add(tmp.num);
						map.put(tmp.num, tmp);
					}
				}
				tm.add(new myTerm(dep[i]));//当前的dep[i]放入tm中 是啥意思
				arg_words = null;//这tm是干屁的 又给删了
			} else {//木有
				System.out.println("Parsing dependency error in Sentence:" + deps);
				System.exit(1);
			}
		}
//		System.out.println(l.toString());
		Collections.sort(l);
		myTerm[] buff_terms = new myTerm[tm.size()];
		myWord[] buff_words = new myWord[wd.size()];
		for (int i = 0; i < tm.size(); i++ ) {//tm是干这个用的
			buff_terms[i] = tm.get(i);
		}
		
		for (int i = 0 ; i < wd.size(); i++) {//本来有l 和wd的工作 现在只有map l了
//			System.out.println(wd.get(i));
//			System.out.println(l.get(i+1) - 1);
//			buff_words[l.get(i + 1) - 1] = wd.get(i);
			buff_words[i] = map.get(l.get(i + 1));//l记录 index index是hash的key
		}
		
		
		
		tm = null;
		wd = null;
		termList = buff_terms;
		wordList = buff_words;//uniq 的wordlist 不知道嘎哈的
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
		for (int i = 0; i < wordLen; i++) {//不需要提前处理 对wordlen赋值吗
			String n = wordList[i].name;
			if (n.startsWith("u"))//如果词以u开头 删去 并和之前的字符串相加
				n = n.substring(1);
			s = s + n;
		}
		return s;
	}
	
	public myTerm[] getTerms() {
		return termList;
	}
	
	public myWord[] getWords() {//注意 是uniq的
		return wordList;
	}
	/**
	 * get a list of predicates in array list of terms
	 * @param terms: input terms
	 * @return: array list of predicates
	 */
	public ArrayList<Predicate> getAllPreds(ArrayList<myTerm> terms) {//得到uniq的谓词
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
	private void buildFeature() {//把uniq的word用CommonPredicates().posTag之后得到的uniq的feature
		// POSTAG feature
		for (myWord w : this.getWords()) {
			myTerm tmp_term = new CommonPredicates().posTag(w, w.toPostagWord());//w.toPostagWord()是当前词+postag+“_POS”
			if (!featList.contains(tmp_term))//上一行意思是组成一个有postag的feature 嘎哈用啊
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

	public String toTermString() {//就给每个Term后面加个;在放一起
		// TODO Auto-generated method stub
		String s = "";
		for (myTerm t : this.termList) {
			s = s + t.toString() + ";";
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}
	
}
