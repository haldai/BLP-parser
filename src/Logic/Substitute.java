/**
 * 
 */
package Logic;

import java.util.ArrayList;

/**
 * @author Wang-Zhou
 *
 */
public class Substitute {

	/**
	 *  Logical substitution for a set of logical myTerms
	 */
	ArrayList<myTerm> to_be_sub;
	ArrayList<myTerm> after_sub;
	ArrayList<myWord> word_list;
	ArrayList<myWord> var_list;
	
	public Substitute(ArrayList<myTerm> term_list) {
		to_be_sub = term_list;
		word_list = new ArrayList<myWord>();
		var_list = new ArrayList<myWord>();
		after_sub = subsTermList(to_be_sub);
	}
	/**
	 * TODO need to define a new object to return, include substituted, word_list and var_list
	 * @param term_list
	 * @return
	 */
	public ArrayList<myTerm> subsTermList(ArrayList<myTerm> term_list) {//不太明白 艹？？？
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		// get word list and variable list
		int cnt = 1;
		for (myTerm t : term_list) {
			ArrayList<myWord> tmp_args = new ArrayList<myWord>(t.getArgs().length); 
			for (myWord w : t.getArgs()) {
				if (!word_list.contains(w)) {//不包含当前输入中当前word的情况  加入当前w到word_list 加入X_cnt到val_list cnt序号是加入的次数（个数）
					word_list.add(w);
					myWord tmp_var = new myWord(String.format("X_%d", cnt));
					var_list.add(tmp_var);//X_cnt 加入val_list  val_list是个新list可以看做里面全是 这步加进去的
					tmp_args.add(var_list.get(cnt - 1));//应该就是指当前加入的那个X_cnt  add到tmp_args（那为何写两个完全一样的东西）
					cnt++;
				} else {
					int p = word_list.indexOf(w);//包含这个word的话 直接把序号加入var_list 从而加入tmp_args即可
					tmp_args.add(var_list.get(p));
				}
			}
			myTerm subed_term = new myTerm(t.getPred(), tmp_args.toArray(new myWord[tmp_args.size()]));//和word_list没关了？
			if (!t.isPositive())
				subed_term.setNegative();
			re.add(subed_term);
		}
		return re;
	}
	
	public myTerm subsTerm(myTerm t, String[] s1, String[] s2) {
		if (s1.length != s2.length) {
			System.out.println("Subsititution error, length not match");
			return null;
		}
		String term = t.toString();//转为strng
		for (int i = 0; i < s1.length; i++) {
			term = term.replaceAll(s1[i], s2[i]);//string就可以直接字符串替换了
		}
		return new myTerm(term);//string 转为 myTerm？
	}
	
	public myTerm[] subsTerms(myTerm[] t, String[] s1, String[] s2) {
		myTerm [] re = new myTerm[t.length];
		for (int i = 0; i < t.length; i++) {
			re[i] = subsTerm(t[i], s1, s2);
		}
		return re;
	}
	
	public ArrayList<myTerm> getOriginTerms() {
		return to_be_sub;
	}
	
	public ArrayList<myTerm> getSubTerms() {
		return after_sub;
	}
	
	public ArrayList<myWord> getWordList() {
		return word_list;
	}
	
	public ArrayList<myWord> getVarList() {
		return var_list;
	}
}
