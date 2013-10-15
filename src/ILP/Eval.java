/**
 * 
 */
package ILP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import jpl.*;
import Logic.*;
/**
 * @author daiwz
 *
 */
public class Eval {

	/**
	 * Eval is a function y = f(x), when given features x, it can 
	 * deduce the results y. 
	 * 
	 * Initialization with formulae.
	 */
	
	Sentence[] sentences;
	Formular[] rules;
	Predicate[] Q_Preds; // query predicates
	int ruleLen;
	int sentLen;
	int predLen;
	
	
	public Eval(LogicProgram program, Document doc) {
		// Start prolog engine initialization
		rules = program.getRules();
		sentences = doc.getSentences();
		ruleLen = rules.length;
		sentLen = sentences.length;
		// find out all query predicates
		Set<Predicate> buff_preds = new HashSet<Predicate>();
		for (Formular r : rules) {
			for (myTerm t : r.getHead()) {
				if (!buff_preds.contains(t.getPred())) {
					buff_preds.add(t.getPred());
				}
			}
		}
		Q_Preds = new Predicate[buff_preds.size()];
		predLen = buff_preds.size();
		int i = 0;
		for (Iterator<Predicate> it = buff_preds.iterator(); it.hasNext();) {
			Q_Preds[i] = it.next();
			i++;
		}
		buff_preds = null;
		JPL.init();
		// Prolog.assertz(ALL_RULES)
		for (Formular r : rules) {
			String clause = r.toPrologStr();
			if (clause.endsWith("."))
				clause = clause.substring(0, clause.length() - 1);
			assertz(clause);
		}
	}
	
	public Eval() {
		rules = null;
		sentences = null;
		Q_Preds = null;
		ruleLen = 0;
		sentLen = 0;
		predLen = 0;
		JPL.init();
	}
	
	public ArrayList<LinkedList<myTerm>> evalAll() {
		ArrayList<LinkedList<myTerm>> re = new ArrayList<LinkedList<myTerm>>(sentLen);
		// Start evaluation sentences
		for (int i = 0; i < sentLen; i++) {
			re.set(i, evalSent(i));
		}
		return re;
	}

	public LinkedList<myTerm> evalSent(int i) {
		// evaluate the i-th sentence
		LinkedList<myTerm> re = new LinkedList<myTerm>();
		re = eval(sentences[i].getTerms()); 
		return re;
	}
	
	public LinkedList<myTerm> eval(myTerm[] facts) {
		LinkedList<myTerm> re = new LinkedList<myTerm>();
		// TODO evaluate all terms;
		for (myTerm term : facts) {
			assertz(term.toString());
		}
		// TODO get answers
		for (Predicate pred : Q_Preds) {
			String query = String.format("%s(", pred.getName());
			// Build query string
			String[] vars = new String[pred.getArity()]; // variable list, for querying
			for (int i = 0; i < pred.getArity(); i++) {
				vars[i] = String.format("X_%d", i + 1);
				query = query + vars[i] + ",";
			}
			if (query.endsWith(","))
				query = query.substring(0, query.length() - 1) + ")";
			// Start querying
			Query q = new Query(query);
			String answer = new String(query);
			while (q.hasMoreSolutions()) {
				java.util.Hashtable ans = q.nextSolution();
				// Build solution terms
				for (int i = 0; i < pred.getArity(); i++) {
					// replace query variables
					answer = answer.replaceAll(vars[i], ans.get(vars[i]).toString());
				}
			}
			System.out.println(answer);
			re.add(new myTerm(answer));
		}
		for (myTerm term : facts) {
			retract(term.toString());
		}
		return re;
	}
	
	private void assertz(String t) {
		String clause;
		clause = String.format("assertz(%s).", t);
		Query q = new Query(clause);
		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		}
	}
	
	private void retract(String t) {
		String clause;
		clause = String.format("retract(%s).", t);
		Query q = new Query(clause);
		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		}
	}
	
	private void retractAll(String t) {
		String clause;
		clause = String.format("retractall(%s).", t);
		Query q = new Query(clause);
		try {
			q.hasSolution();
		} catch (PrologException e) {
			System.out.println("Prolog Assertion Failed!!!");
			System.out.println("ERROR LOG: " + clause);
			System.out.println(e.getMessage());
		}
	}
}
