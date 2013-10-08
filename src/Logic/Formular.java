/**
 * 
 */
package Logic;

/**
 * @author Wang-Zhou Dai
 *
 */

import java.util.Arrays;
import java.util.List;

public class Formular {

	/**
	 * Logic formular in prolog form, e.g., "grandfather(X, Y):-father(X,Z),father(Z,Y)."
	 * 
	 * use String to interact with JPL.
	 */
	
	double weight;
	int headLen;
	int tailLen;
	private myTerm[] head;
	private myTerm[] tail;
	
	public Formular() {
		weight = 0.0;
		headLen = 0;
		tailLen = 0;
		head = null;
		tail = null;
	}
	
	public Formular(double w, myTerm[] h, myTerm[] t) {
		weight = w;
		headLen = h.length;
		tailLen = t.length;
		head = h;
		tail = t;
	}
	
	public void pushTail(myTerm t) {
		List<myTerm> l = Arrays.asList(tail);
		l.add(t);
		tail = null;
		tail = new myTerm[l.size()];
		for (int i = 0; i < l.size(); i++) {
			tail[i] = l.get(i);
		}
		tailLen = l.size();
	}
	
	public void pushHead(myTerm h) {
		List<myTerm> l = Arrays.asList(head);
		l.add(h);
		head = null;
		head = new myTerm[l.size()];
		for (int i = 0; i < l.size(); i++) {
			head[i] = l.get(i);
		}
		headLen = l.size();
	}
	
	public myTerm popTail() {
		List<myTerm> l = Arrays.asList(tail);
		myTerm t = l.get(l.size() - 1);
		l.remove(l.size() - 1);
		tail = null;
		tail = new myTerm[l.size()];
		for (int i = 0; i < l.size(); i++) {
			tail[i] = l.get(i);
		}
		tailLen = l.size();
		return t;
	}
	
	public myTerm popHead() {
		List<myTerm> l = Arrays.asList(head);
		myTerm h = l.get(l.size() - 1);
		l.remove(l.size() - 1);
		head = null;
		head = new myTerm[l.size()];
		for (int i = 0; i < l.size(); i++) {
			head[i] = l.get(i);
		}
		headLen = l.size();
		return h;
	}
	
	public myTerm getTail(int i) {
		return tail[i];
	}
	
	public myTerm getHead(int i) {
		return head[i];
	}
	
	public myTerm[] getTail() {
		return tail;
	}
	
	public myTerm[] getHead() {
		return head;
	}
	// return a String in prolog style
	public String toPrologStr() {
		String s = "";
		for (int i = 0; i < head.length; i++) {
			s = s + head[i].toString() + ',';
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 2);
			s = s + ":-";
		}
		for (int i = 0; i < tail.length; i++) {
			s = s + tail[i].toString() + ',';
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 2);
			s = s + '.';
		}
		return s;
	}
//	// substitution
//	public Formular substitution(myWord[] vars, myWord[] atms) {
//		// TODO myTerm substitution
//		if (vars.length != atms.length) {
//			System.out.println("Substitution variables and atoms are in different length!");
//			throw new IllegalArgumentException();
//		}
//		for (int i = 0; i < tail.length; i++) {
//			
//		}
//	}
}
