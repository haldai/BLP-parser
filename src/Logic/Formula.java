/**
 * 
 */
package Logic;

/**
 * @author Wang-Zhou Dai
 *
 */

import java.util.ArrayList;
import java.util.LinkedList;

public class Formula {

	/**
	 * Logic formular in prolog form, e.g., "grandfather(X, Y):-father(X,Z),father(Z,Y)."
	 * 
	 * use String to interact with JPL.
	 */
	
	double weight = 1.0;
	int headLen;
	int bodyLen;
	private ArrayList<myTerm> head = new ArrayList<myTerm>();
	private ArrayList<myTerm> body = new ArrayList<myTerm>();
	
	public Formula() {
		headLen = 0;
		bodyLen = 0;
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

	public void pushBody(ArrayList<myTerm> t) {
		for (myTerm term : t) {
			body.add(term);
		}
		bodyLen = body.size();
	}
	
	public void pushBody(myTerm t) {
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
	
	public myTerm popBody() {
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
	
	public myTerm getBody(int i) {
		return body.get(i);
	}
	
	public myTerm getHead(int i) {
		return head.get(i);
	}
	
	public ArrayList<myTerm> getBody() {
		return body;
	}
	
	public ArrayList<myTerm> getHead() {
		return head;
	}
	// return a String in prolog style
	public String toPrologString() {
		String s = "";
		for (int i = 0; i < head.size(); i++) {
			s = s + head.get(i).toPrologString() + ',';
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 2);
			s = s + "):- ";
		}
		for (int i = 0; i < body.size(); i++) {
			s = s + body.get(i).toPrologString() + ',';
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 1);
			s = s + '.';
		}
		return s;
	}
	
	public String toString() {
		return this.toPrologString();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Formula))
			return false;
		else {
			Formula f = (Formula) o;
			for (myTerm t1 : f.getHead())
				for (myTerm t2 : this.getHead()) {
					if (!t1.equals(t2))
						return false;
				}
			for (myTerm t1 : f.getBody())
				for (myTerm t2 : this.getBody()) {
					if (!t1.equals(t2))
						return false;
				}
		}
		return true;
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
