/**
 * 
 */
package Logic;

/**
 * @author Wang-Zhou Dai
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Formula {

	/**
	 * Logic formular in prolog form, e.g., "grandfather(X, Y):-father(X,Z),father(Z,Y)."
	 * 
	 * use String to interact with JPL.
	 */
	
	double weight = 1.0;
	int headLen;
	int bodyLen;
	private ArrayList<myTerm> head;
	private ArrayList<myTerm> body;
	
	public Formula() {
		headLen = 0;
		bodyLen = 0;
		head = null;
		body = null;
	}
	
	public Formula(ArrayList<myTerm> h, ArrayList<myTerm> t) {
		headLen = h.size();
		bodyLen = t.size();
		head = h;
		body = t;
	}
	// build formula by String	
	public Formula(String f) {
		if (f.endsWith("."))
			f = f.substring(0, f.length());
		String[] comp = f.split("\\:-");
		// read head;
		String[] head_string = comp[0].split("\\;");
		head = new ArrayList<myTerm>();
		for (String s : head_string) {
			head.add(new myTerm(s));
		}
		headLen = head.size();
		// add body
		String[] body_string = comp[1].split("\\;");
		body = new ArrayList<myTerm>();
		for (String s : body_string) {
			body.add(new myTerm(s));
		}
		bodyLen = body.size();
	}

	public void pushbody(ArrayList<myTerm> t) {
		for (myTerm term : t) {
			body.add(term);
		}
		bodyLen = body.size();
	}
	
	public void pushbody(myTerm t) {
		body.add(t);
		bodyLen = body.size();
	}
	
	public void pushbody(LinkedList<myTerm> terms) {
		for (myTerm t : terms) {
			body.add(t);
		}
		bodyLen = body.size();
	}
	
	public void pushHead(ArrayList<myTerm> h) {
		for (myTerm term : h) {
			head.add(term);
		}
		headLen = head.size();
	}
	
	public void pushHead(LinkedList<myTerm> terms) {
		for (myTerm t : terms) {
			head.add(t);
		}
		headLen = head.size();
	}
	
	public void pushHead(myTerm h) {
		head.add(h);
		headLen = head.size();
	}
	
	public myTerm popbody() {
		myTerm re = body.get(bodyLen - 1);
		body.remove(bodyLen - 1);
		bodyLen = body.size();
		return re;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double w) {
		weight = w;
	}
	
	public myTerm popHead() {
		myTerm re = head.get(headLen - 1);
		head.remove(headLen - 1);
		return re;
	}
	
	public myTerm getbody(int i) {
		return body.get(i);
	}
	
	public myTerm getHead(int i) {
		return head.get(i);
	}
	
	public ArrayList<myTerm> getbody() {
		return body;
	}
	
	public ArrayList<myTerm> getHead() {
		return head;
	}
	// return a String in prolog style
	public String toPrologStr() {
		String s = "";
		for (int i = 0; i < head.size(); i++) {
			s = s + head.get(i).toString() + ',';
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 2);
			s = s + "):- ";
		}
		for (int i = 0; i < body.size(); i++) {
			s = s + body.get(i).toString() + ',';
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 1);
			s = s + '.';
		}
		return s;
	}
	
	public String toString() {
		return this.toPrologStr();
	}


	
//	// substitution
//	public Formular substitution(myWord[] vars, myWord[] atms) {
//		// TODO myTerm substitution
//		if (vars.length != atms.length) {
//			System.out.println("Substitution variables and atoms are in different length!");
//			throw new IllegalArgumentException();
//		}
//		for (int i = 0; i < body.length; i++) {
//			
//		}
//	}
}
