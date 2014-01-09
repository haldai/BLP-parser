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

public class Formula implements Cloneable {

	/**
	 * Logic formular in prolog form, e.g., "grandfather(X, Y):-father(X,Z),father(Z,Y)."
	 * 
	 * use String to interact with JPL.
	 */
	
	double weight = 1.0;
	int headLen;
	int bodyLen;
	private ArrayList<myTerm> head = new ArrayList<myTerm>();//就是标签
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
		if (f.contains("::")) {
			this.weight = Double.valueOf(f.split("::")[0]);//对于有概率的f 字符串 是“概率::公式”的形式
			f = f.split("::")[1];
		}
			
		if (f.endsWith("."))
			f = f.substring(0, f.length() - 1);
		String[] comp = f.split("\\:-");
		// read head;
		String[] head_string = comp[0].split("\\;");
		head = new ArrayList<myTerm>();
		for (String s : head_string) {
			// detect wether the term is negative
			boolean sym = true;
			while (s.startsWith(" "))
				s = s.substring(1);
			if ((s.startsWith("not(")) || (s.startsWith("\\+("))) {
				s = s.substring(4, s.length() - 1);
				sym = false;//not 和+( 对应的是 sym 错误啊
			} else {
				if (s.startsWith("("))
					s = s.substring(1, s.length() - 1);
			}
			myTerm tmp_t = new myTerm(s);//做成Term 其实整体字符串没怎么变只是把前缀去掉了
			if (!sym)
				tmp_t.setNegative();//对于Term的正负
			head.add(tmp_t);//放入
		}
		headLen = head.size();
		// add body
		String[] body_string = comp[1].split("\\;");
		body = new ArrayList<myTerm>();
		for (String s : body_string) {//同上
			// detect wether the term is negative
			boolean sym = true;
			while (s.startsWith(" "))
				s = s.substring(1);
			if ((s.startsWith("not(")) || (s.startsWith("\\+("))) {
				s = s.substring(3, s.length() - 1);
				sym = false;
			} else {
				if (s.startsWith("("))
					s = s.substring(1, s.length() - 1);
			}
			myTerm tmp_t = new myTerm(s);
			if (!sym)
				tmp_t.setNegative();
			body.add(tmp_t);
		}
		bodyLen = body.size();
	}
	

	public void pushBody(ArrayList<myTerm> t) {//对body push_back新的Term
		for (myTerm term : t) {
			body.add(term);
		}
		bodyLen = body.size();
	}
	
	public void pushBody(myTerm t) {
		myTerm nt = t.clone();
		body.add(nt);
		bodyLen = body.size();
	}
	
	public void pushBodyToFirst(myTerm t) {
		myTerm nt = t.clone();
		body.add(0, nt);
		bodyLen = body.size();
	}
	
	public void pushBody(LinkedList<myTerm> terms) {
		for (myTerm t : terms) {
			myTerm nt = t.clone();
			body.add(nt);
		}
		bodyLen = body.size();
	}
	
	public void pushBodyToFirst(LinkedList<myTerm> terms) {
		for (int i = terms.size() - 1; i >= 0; i--) {
			myTerm nt = terms.get(i).clone();
			body.add(0, nt);//从头push 倒序push
		}
		bodyLen = body.size();
	}
	
	public void pushHead(ArrayList<myTerm> h) {
		for (myTerm term : h) {
			myTerm nt = term.clone();
			head.add(nt);
		}
		headLen = head.size();
	}
	
	public void pushHead(LinkedList<myTerm> terms) {
		for (myTerm t : terms) {
			myTerm nt = t.clone();
			head.add(nt);
		}
		headLen = head.size();
	}
	
	public void pushHead(myTerm h) {
		myTerm nt = h.clone();
		head.add(nt);
		headLen = head.size();
	}
	
	public myTerm popBody() {
		myTerm re = body.get(bodyLen - 1);
		body.remove(bodyLen - 1);
		bodyLen = body.size();
		return re;
	}
	
	public myTerm popBodyFirst() {
		myTerm re = body.get(0);
		body.remove(0);
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
	
	public myTerm popHeadFirst() {
		myTerm re = head.get(0);
		head.remove(0);
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
			s = s + ") :- ";
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
		String s = String.format("%s::", weight);
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
	
	public Formula clone() {
		try {
			Formula re = (Formula) super.clone();
			re.head = new ArrayList<myTerm>();
			re.body = new ArrayList<myTerm>();
			for (myTerm t : this.head)
				re.head.add(t.clone());
			for (myTerm t : this.body)
				re.body.add(t.clone());
			return re;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
