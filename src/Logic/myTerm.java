/**
 * 
 */
package Logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author daiwz
 *
 */
public class myTerm implements Cloneable{

	/**
	 * myTerm is a term type, which have a predicate and some arguments.
	 * e.g. "att(刘德华_1_nr, 老婆_2_n)"，"att" is name, the else in brackets 
	 * are words/arguments.
	 */
	Predicate pred;
	myWord[] args;
	double weight = 0.0;
	boolean isPositive = true;//啥含义？
	
	public myTerm(String n, ArrayList<myWord> words) {
		pred = new Predicate(n, words.size());//words/arguments的arguments就是代表size的 傻逼！
		args = words.toArray(new myWord[words.size()]);//words 转为数组的形式
	}
	
	public myTerm(Predicate p, myWord[] words) {
		pred = p;
		args = words;
	}
	
	public myTerm(Predicate p, ArrayList<myWord> words) {
		pred = p;
		args = words.toArray(new myWord[words.size()]);
	}
	/**
	 * another realization of directly reading string into myWord and Predicate
	 * 从直接读进来的字符串直接转换
	 * @param s: string to be parsed
	 */
	public myTerm(String s) {
		// find arguments
		if (s.startsWith("\\+(")) {
			s = s.substring(3, s.length() - 1);
			isPositive = false;
		} else if (s.startsWith("not(")) {
			s = s.substring(4, s.length() - 1);
			isPositive = false;//啥含义
		}//忽略前缀？not和+(
		Pattern p = Pattern.compile("\\(.*?\\)");
		Matcher m = p.matcher(s);//以\\(.*?\\) 的某种方式对 s进行匹配 ？？感觉s和p写反了？
		boolean found = m.find();//？？是否匹配成功吗
		String[] tmp_args = null;
		List<myWord> buff_words = new ArrayList<myWord>();
		if (found) {
			String tmp_s = m.group();//之前匹配之后的子串
			tmp_s = tmp_s.substring(1, tmp_s.lastIndexOf(')'));//1到)中间的字符串  从1开始可能表示去掉开始的"("
			tmp_args = tmp_s.split("\\,");//这字符串用,分开 分成每个word
			for (int i = 0; i < tmp_args.length; i++) {
				buff_words.add(new myWord(tmp_args[i]));//形成word加入word数组：buff
			}
			args = new myWord[buff_words.size()];
			for (int i = 0; i < buff_words.size(); i++) {
				args[i] = buff_words.get(i);//buff复制到args（正式了）
			}
		} else {//没成功
			System.out.println("arguments of " + s + " not found!");
			System.exit(0);
		}
		pred = new Predicate(s.split("\\(")[0], tmp_args.length);
	}
	
	public myTerm() {
		pred = null;
		args = null;
	}

	public Predicate getPred() {//谓词
		return pred;
	}
	
	public String toString() {//形成谓词+word的形式 可能是类似prolog语言的形式
		String pos = "";
		if (!this.isPositive())//注意 用到了
			pos = "not: "; //只加个这个
		String s = String.format("%s%s(", pos, pred.getName());
		for (myWord w : args) {
			if (w.getNum() == 0)//对应啥情况 不应该啊0的话 不是词 是未解决问题
				s = s + w.toPrologString() + ",";
			else
				s = s + w.toString() + ",";
		}
		if (s.endsWith(","))
			s = s.substring(0, s.length() - 1) + ")";
		return s;
	}
	
	public String toPrologString() {
		String pos = "";
		if (!this.isPositive()) {
			if (pred.getName().equals("=="))//谓词是==的时候 pos为+
				pos = " \\+";
			else
				pos = " \\+";
		}
		String s = String.format("%s(%s(", pos, pred.getName());
		for (myWord w : args) {//直接干成prolog string
			s = s + w.toPrologString() + ",";
		}
		if (s.endsWith(","))
			s = s.substring(0, s.length() - 1) + "))";
		return s;
	}
	
	public myWord[] getArgs() {//得到word
		return args;
	}
	
	public myWord getArg(int i) {
		return args[i];
	}
	
	/**
	 * term substitution
	 * @param vars: to be substituted
	 * @param atms: to substitute
	 * @return substituted term
	 */
	public myTerm substitution(myWord[] vars, myWord[] atms) {//用B（atms）替换掉A（vars），list中对应位置做替换
		myWord[] a = this.args;
		List<myWord> varList = Arrays.asList(vars);
		for (int i = 0; i < a.length; i++) {
			if (varList.contains(a[i])) {//遍历a中每个元素，看看当前的a[i]是否在A列中有，有的话 对应位置做替换
				int pos = varList.indexOf(a[i]);
				a[i] =  atms[pos];
			}
		}
		return new myTerm(this.pred, a);
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof myTerm))
			return false;
		else {
			myTerm t = (myTerm) o;
			if (!this.pred.equals(t.pred))
				return false;
			else {
				for (int i = 0; i < this.pred.arity; i++) {
					if (!this.args[i].equals(t.getArg(i)))
						return false;
				}
			}
			return true;
		}
	}
	
	public void setWeight(double w) {//哦 weight的操作
		weight = w;
	}
	
	public double getWeight() {
		return weight;
	}
	/**
	 * set this term as a positive term
	 */
	public void setPositive() {
		isPositive = true;
	}
	/**
	 * set this term to negative
	 */
	public void setNegative() {
		isPositive = false;
	}
	
	public boolean isPositive() {
		return isPositive;
	}
	
	public void setArgs(myWord[] a) {//把输入word串 赋给当前串
		if (a.length == args.length)
			args = a;
	}
	
	public void setArg(int i, myWord w) {//把输入word 赋给当前串的第i个
		// TODO Auto-generated method stub
		args[i] = w;
		
	}
	
	public void setPred(Predicate p) {
		if (p.getArity() == pred.getArity())
			pred = p;
	}
	/**
	 * 反转一下正负（Positive）
	 */
	public void flip() {
		if (isPositive)
			isPositive = false;
		else
			isPositive = true;
	}
	@Override
	public myTerm clone() {
		try {
			myTerm re =  (myTerm) super.clone();
			re.args = new myWord[this.args.length];
			for (int i = 0; i < this.args.length; i++)
				re.args[i] = this.args[i].clone();
			return re;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}


	
}
