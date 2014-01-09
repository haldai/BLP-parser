/**
 * 
 */
package Logic;

import jpl.Atom;
import utils.*;
/**
 * @author daiwz
 *
 */
public class myWord implements Cloneable{

	/**
	 * myWord is a word in input data, e.g. "刘德华_1_nr"，"刘德华"is name,
	 * "1" is its position in sentence, "nr" is its POS tag.
	 * 
	 * Otherwise, the class myWorld also defines variable. When the "num" 
	 * position > 0 it is a constant, or atom; when it is less than 0 it 
	 * defines a variable, and the POS tag is "var", e.g. "X_-1_var, X_-2_var",
	 * -1 or -2 means different variables. In convenience, the "name" field 
	 * should always be "X".
	 * 
	 * If meet word that start with upper case or number, simply add a "c" 
	 * in front of it
	 */
	String name;
	int num;
	String pos;
	
	public myWord(String s) {
		// Generative function of myWord(word_#_pos) 
		String[] args = s.split("_");//输入是word_#_pos形式的 我给他分开 应该是位置：num
		name = args[0];
		name = name.replaceAll("？", "char");//应该是替换掉word中的字符 是从哪里来的输入？ 什么featrue吗
		name = name.replaceAll("[*]", "char");
		name = name.replaceAll("[+]", "char");
		name = name.replaceAll("_", "char");
		name = name.replaceAll(":", "char");
		name = name.replaceAll("：", "char");
		name = name.replaceAll("!", "char");
		name = name.replaceAll("！", "char");
		name = name.replaceAll("[?]", "char");
		name = name.replaceAll("、", "char");
		name = name.replaceAll("…", "char");
		name = name.replaceAll("\\[", "char");
		name = name.replaceAll("\\]", "char");
		name = name.replaceAll("@", "char");
		name = name.replaceAll("[$]", "char");
		name = name.replaceAll("~", "char");
		name = name.replaceAll(";", "char");
		name = name.replaceAll("；", "char");
		name = name.replaceAll("。", "char");
		name = name.replaceAll("，", "char");
		name = name.replaceAll("《", "char");
		name = name.replaceAll("》", "char");
		name = name.replaceAll("（", "char");
		name = name.replaceAll("）", "char");
		name = name.replaceAll("”", "char");
		name = name.replaceAll("“", "char");
		name = name.replaceAll("’", "char");
		name = name.replaceAll("‘", "char");
		name = name.replaceAll("、", "char");
		name = name.replaceAll("—", "char");
		name = name.replaceAll("[?]", "char");
		name = name.replaceAll("[.]", "char");
		name = name.replaceAll(",", "char");
		name = name.replaceAll("<", "char");
		name = name.replaceAll(">", "char");
		name = name.replaceAll("[(]", "char");
		name = name.replaceAll("[)]", "char");
		name = name.replaceAll("\"", "char");
		name = name.replaceAll("\'", "char");
		name = name.replaceAll("\\\\", "char");
		name = name.replaceAll("/", "char");
		name = name.replaceAll("[-]", "char");
		name = name.replaceAll("·", "char");
		
		if (args.length == 3) {//word num（位置） postag的形式
			num = Integer.parseInt(args[1]);
			pos = args[2];
		} else if (args.length == 2) {//对应啥情况？
			// is a prolog term
			if (args[0].equals("X") && utils.isNumeric(args[1])) {//word是X 位置num是数组
				num = Integer.parseInt(args[1]);
				pos = "var";
			} else {
				num = 0;
				pos = args[1];
			}
		} else if (args.length == 1) {
			// is a single word
			num = 0;
			pos = "null";
		}
	}
	public myWord() {//没有输入情况下 构造函数
		name = null;
		num = 0;
		pos = null;
	}
	
	public String getName() {//没输入 因为 name num pos是类内的变量
		return name;
	}
	
	public int getNum() {
		return num;
	}
	
	public String getPos() {
		return pos;
	}
	
	public String toString() {//返回字符串 
		String str;
		if (!this.isVar())
			str = String.format("%s_%s_%s", name, num, pos);
		else
			str = String.format("%s_%s", name, pos);
		return str;
	}
	
	@SuppressWarnings("deprecation")
	public String toPrologString() {
		char c = name.charAt(0);
		String new_name = name;
		if (Character.isDigit(c) || Character.isSpace(c)) {//看下第一个字符是字母或空格吗 加d形成变量/常量？
		new_name = "d" + new_name;
		}
		new_name = new_name.replaceAll(" ", "SPACE");
		if (!this.isVar())
			return String.format("%s_%s", new_name, pos);//对变量 只输出pos？？？
		else
			return String.format("%s_%d", new_name, num);
//		String str = String.format("%s_%s_%s", new_name, num, pos);//???
	}

	public Atom toAtom() {//注意应该是把正常的string放在atom中
		return new Atom(this.toString());
	}
	// to judge if the word is a variable
	public boolean isVar() {
		if ((name.equals("X")) && (pos.equals("var"))) //意思是变量吗？
			return true;
		else return false;
	}
	
	public boolean isPos() {//要看输入是啥样的
		if (pos.equals("POS") && num == 0) 
			return true;
		else return false;
	}
	
	public boolean equals(Object o) {//就是分类比较各个量是否都相等 没啥
		if (!(o instanceof myWord))//是否是这类
			return false;
		else {
			myWord w = (myWord) o;
			String t_name = this.name;
			String o_name = w.name;
			// cope with the "d"-addition of some prolog string
			if (this.name.startsWith("d"))//是否都是以d开头
				t_name = t_name.substring(1);
			if (w.name.startsWith("d"))//是否都是以d开头
				o_name = o_name.substring(1);
			// deal with variable and constant comparing
			if ((this.num == 0) || (w.num == 0)) {
				if ((t_name.equals(o_name)) && (this.pos.equals(w.getPos())))
					return true;
				else
					return false;
			} else {
				// constant
				if ((t_name.equals(o_name)) && (this.num == w.getNum()) && (this.pos.equals(w.getPos())))
					return true;
				else
					return false;
			}
		}
	}
	
	public myWord getZeroConst() {//转成prolog再set num 0
		myWord re = new myWord(this.toPrologString());
		re.setNumZero();
		return re;
	}
	
	public void setNumZero() {
		this.num = 0;
	}
	
	public myWord toPostagWord() {
		myWord re = new myWord(this.getPos() + "_" + "POS");
		return re;
	}
	
	public myWord clone() {
		try {
			return (myWord) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public void setPos(String string) {
		this.pos = string;		
	}
	
	public void setName(String code) {
		// TODO Auto-generated method stub
		this.name = code;
	}
}
