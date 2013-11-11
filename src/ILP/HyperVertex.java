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
	
	public HyperVertex(myWord w) {
		name = w.toString();
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public void setName(myWord w) {
		name = w.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public myWord toMyWord() {
		return new myWord(this.name);
	}
	
	public boolean equals(myWord w) {
		myWord t_wrd = new myWord(this.name);
		return t_wrd.equals(w);
	}
	
	public boolean equals(String s) {
		myWord t_wrd = new myWord(this.name);
		myWord o_wrd = new myWord(s);
		return t_wrd.equals(o_wrd);
	}
	
	public boolean equals(Object o) {
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
