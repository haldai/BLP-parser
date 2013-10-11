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
	
	public boolean equals(HyperVertex v) {
		if (this.name.equals(v.name))
			return true;
		else
			return false;
	}
	
	public boolean equals(String v) {
		if (this.name.equals(v))
			return true;
		else
			return false;
	}
	
	public boolean equals(myWord w) {
		if (this.name.equals(w.toString()))
			return true;
		else
			return false;
	}
}
