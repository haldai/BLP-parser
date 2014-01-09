/**
 * 
 */
package ILP;

import Logic.myWord;
/**
 * @author Wang-Zhou
 *
 */
public class HyperVertex {

	/**
	 * HyperVertex is vertices in hyper graph.
	 */
	String name;
	
	public HyperVertex(String s) {
		name = s;
	}
	
	public HyperVertex() {
		name = null;
	}
	
	public HyperVertex(myWord w) {//word考验作为输入
		name = w.toString();
	}
	
	public void setName(String s) {//和构造函数一样
		name = s;
	}
	
	public void setName(myWord w) {
		name = w.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public myWord toMyWord() {//当前点转为MyWord类型
		return new myWord(this.name);
	}
	
	public boolean equals(myWord w) {
		myWord t_wrd = new myWord(this.name);
		return t_wrd.equals(w);
	}
	
	public boolean equals(String s) {//和上面的原理一样 也是转化为MyWord类型比较
		myWord t_wrd = new myWord(this.name);
		myWord o_wrd = new myWord(s);
		return t_wrd.equals(o_wrd);
	}
	
	public boolean equals(Object o) {//HyperVertex类型也能判断 也是转化为MyWord判断 没啥技术含量
		if (o instanceof myWord)
			return equals((myWord) o);
		else if (o instanceof String)
			return equals((String) o);
		else if (o instanceof HyperVertex){
			HyperVertex w = (HyperVertex) o;
			myWord t_wrd = new myWord(this.name);
			myWord o_wrd = new myWord(w.name);
			return t_wrd.equals(o_wrd);
		}
		else
			return false;
	}
	

	
	public String toString() {
		return name;
	}
}
