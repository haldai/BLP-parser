/**
 * 
 */
package ILP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import jpl.*;
import Logic.*;
import Tree.SentSat;
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
	
	ArrayList<Formula> rules = new ArrayList<Formula>();
	ArrayList<Predicate> Q_Preds = new ArrayList<Predicate>(); // query predicates
	int ruleLen = 0;
	Prolog prolog;
	
	/**
	 * initialize an evaluation
	 * @param plg: swi-prolog instance
	 * @param program: logic rules
	 * @param doc: document to be evaluated
	 */
	public Eval(Prolog plg, LogicProgram program, Predicate[] preds) {
		// Start prolog engine initialization
		rules = program.getRules();
		Q_Preds = program.getHeadPred();
		ruleLen = rules.size();
		prolog = plg; 
		assertDynamic(plg, preds);
		// Prolog.assertz(ALL_RULES)
		for (Formula r : rules) {
			String clause = r.toPrologString();
			if (clause.endsWith("."))
				clause = "(" + clause.substring(0, clause.length() - 1) + ")";
			prolog.assertz(clause);
		}
	}
	
	public Eval(Prolog plg, Predicate[] preds) {
		// Start prolog engine initialization
		ruleLen = rules.size();
		prolog = plg; 
		assertDynamic(plg, preds);
	}
	
	/**
	 * initiate a evaluation only with prolog
	 * @param p: prolog engine
	 */
	public Eval(Prolog p) {
		prolog = p;
	}
	/**
	 * IMPORTANT! must assert dynamicity of all predicates
	 * @param plg: prolog engine
	 * @param preds: predicate array
	 */
	private void assertDynamic(Prolog plg, Predicate[] preds) {
		// Prolog.dynamic(ALL_PREDICATES)
		for (Predicate p : preds) {
			plg.dynamic(p);
		}
	}
	/**
	 * setting rules for this evaluation
	 * @param formulae: the input formlae
	 */
	public void setRules(LogicProgram program) {
		for (Predicate p : program.getHeadPred()) {
			if (!Q_Preds.contains(p))
				Q_Preds.add(p);
		}
		for (Formula r : program.getRules()) {
			if (!rules.contains(r))
				rules.add(r);
		}
		// Prolog.dynamic(ALL_PREDICATES)
		for (Predicate p : Q_Preds) {
			prolog.dynamic(p);
		}
		// Prolog.assertz(ALL_RULES)
		for (Formula r : program.getRules()) {
			String clause = r.toPrologString();
			if (clause.endsWith("."))
				clause = "(" + clause.substring(0, clause.length() - 1) + ")";
			prolog.assertz(clause);
		}
	}
	/**
	 * 
	 * @param program
	 */
	public void setRule(Formula f) {
		LogicProgram program = new LogicProgram();
		program.addRule(f);
		for (Predicate p : program.getHeadPred()) {
			if (!Q_Preds.contains(p))
				Q_Preds.add(p);
		}
		for (Formula r : program.getRules()) {
			if (!rules.contains(r))
				rules.add(r);
		}
		// Prolog.dynamic(ALL_PREDICATES)
		for (Predicate p : Q_Preds) {
			prolog.dynamic(p);
		}
		// Prolog.assertz(ALL_RULES)
		for (Formula r : program.getRules()) {
			String clause = r.toPrologString();
			if (clause.endsWith("."))
				clause = "(" + clause.substring(0, clause.length() - 1) + ")";
			prolog.assertz(clause);
		}
	}
	/**
	 * evaluate all sentenecs in document
	 * @return: all answers
	 */
	public ArrayList<LinkedList<myTerm>> evalAll(Document doc) {
		ArrayList<LinkedList<myTerm>> re = new ArrayList<LinkedList<myTerm>>(doc.getSentences().length);
		// Start evaluation sentences
		for (int i = 0; i < doc.getSentences().length; i++) {
			LinkedList<myTerm> ans_list = evalSent(doc.getSent(i));
//			re.set(i, ans_list);
			re.add(ans_list);
		}
		return re;
	}
	/**
	 * evaluate all sentences in a list
	 * @param sents: list of sentences
	 * @return: all answers
	 */
	public ArrayList<LinkedList<myTerm>> evalAll(ArrayList<Sentence> sents) {
		ArrayList<LinkedList<myTerm>> re = new ArrayList<LinkedList<myTerm>>(sents.size());
		// Start evaluation sentences
		for (int i = 0; i < sents.size(); i++) {
			ArrayList<myTerm> terms = new ArrayList<myTerm>(Arrays.asList(sents.get(i).getTerms()));
			terms.addAll(sents.get(i).getFeatures());
			LinkedList<myTerm> ans_list = eval(terms);
			re.add(ans_list);
		}
		return re;
	}
	/**
	 * evaluate all term list
	 * @param sents: the term list
	 * @return: all answers
	 */
	public ArrayList<LinkedList<myTerm>> evalAllTermList(ArrayList<ArrayList<myTerm>> sents, ArrayList<ArrayList<myTerm>> feats) {
		ArrayList<LinkedList<myTerm>> re = new ArrayList<LinkedList<myTerm>>(sents.size());
		// Start evaluation sentences
		for (int i = 0; i < sents.size(); i++) {
			ArrayList<myTerm> terms = sents.get(i);
			terms.addAll(feats.get(i));
			LinkedList<myTerm> ans_list = eval(terms);
			re.add(ans_list);
		}
		return re;
	}
	/**
	 * evaluate a list of instances, record all covered positive and negative answers
	 * @param labels: input labels of sentences
	 * @param sents: input sentences
	 * @return: set of negative and positive answers
	 */
	public SentSat evalAllSat(ArrayList<ArrayList<myTerm>> labels, ArrayList<Sentence> sents) {
		SentSat re = new SentSat();
		if (labels.size() != sents.size())
			return null;
		// Start evaluation sentences
		for (int i = 0; i < sents.size(); i++) {
			SatisfySamples sa = evalSentSat(labels.get(i), sents.get(i));
			re.addSentSat(labels.get(i), sents.get(i), sa);
		}
		return re;
	}
	/**
	 * evaluate the whole document
	 * @param doc: input document
	 * @return: satisfy samples
	 */
	public SentSat evalDocSat(Document doc) {
		SentSat re = new SentSat();
		// Start evaluation sentences
		for (int i = 0; i < doc.length(); i++) {
			SatisfySamples sa = evalSentSat(doc.getLabel(i), doc.getSent(i));
			re.addSentSat(doc.getLabel(i), doc.getSent(i), sa);
		}
		return re;
	}
	
	/**
	 * evaluate a sentence
	 * @param i: the i-th sentence
	 * @return: deduced answer
	 */
	public LinkedList<myTerm> evalSent(Sentence sent) {
		// evaluate the i-th sentence
		LinkedList<myTerm> re = new LinkedList<myTerm>();
		ArrayList<myTerm> terms = new ArrayList<myTerm>(Arrays.asList(sent.getTerms()));
		terms.addAll(sent.getFeatures());
		re = eval(terms); 
		return re;
	}
	/**
	 * get satisfied samples of a sentence in labeled data
	 * @param sent: input sentence
	 * @param label: input label of the sentence
	 * @return: satisfied samples
	 */
	public SatisfySamples evalSentSat(ArrayList<myTerm> label, Sentence sent) {
		SatisfySamples re = new SatisfySamples();
		ArrayList<myTerm> terms = new ArrayList<myTerm>(Arrays.asList(sent.getTerms()));
		terms.addAll(sent.getFeatures());
		LinkedList<myTerm> ans = eval(terms);
		re.setSatisifySamples(label, ans);
		return re;
	}
	
	public SatisfySamples evalSentSat(ArrayList<myTerm> label, Document doc, int sentIdx) {
		SatisfySamples re = new SatisfySamples();
		ArrayList<myTerm> terms = new ArrayList<myTerm>(Arrays.asList(doc.getSent(sentIdx).getTerms()));
		terms.addAll(doc.getSent(sentIdx).getFeatures());
		LinkedList<myTerm> ans = eval(terms);
		re.setSatisifySamples(label, ans);
		return re;
	}
	
	/**
	 * evaluate current rules in facts
	 * @param facts: facts of background knowledge
	 * @return: linked list of true groundings deduced from facts and current rules
	 */
	public LinkedList<myTerm> eval(ArrayList<myTerm> facts) {
		LinkedList<myTerm> re = new LinkedList<myTerm>();
		// evaluate all terms;
		for (myTerm term : facts) {
			prolog.assertz(term.toPrologString());
//			System.out.println(term.toPrologString());
		}
		// get answers
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
			LinkedList<String> answers = new LinkedList<String>();
//			System.out.println("Query: " + q);
			while (q.hasMoreSolutions()) {
				String answer = new String(query);
				@SuppressWarnings("rawtypes")
				java.util.Hashtable ans = q.nextSolution();
				// Build solution terms
				for (int i = 0; i < pred.getArity(); i++) {
					// replace query variables
					answer = answer.replaceAll(vars[i], ans.get(vars[i]).toString());
				}
//				System.out.println(answer);
				if (!answers.contains(answer)) {
					re.add(new myTerm(answer));
					answers.add(answer);
				}
			}
//			System.out.println(answer);
//			re.add(new myTerm(answer));
		}
		for (myTerm term : facts) {
			prolog.retract(term.toPrologString());
		}
		return re;
	}
	
	public void unEval() throws Throwable {
		retractRules();
		this.finalize();
	}
	
	private void retractRules() {
		for (Formula r : rules) {
			String clause = r.toPrologString();
			if (clause.endsWith("."))
				clause = "(" + clause.substring(0, clause.length() - 1) + ")";
			prolog.retract(clause);
		}
	}
	
/**
 * Evaluate given document by each formula alone, and add weight for each outcome
 * @param prog: logic programs that should be evaled one by one;
 * @param labels: labels 
 * @param sent: sentences
 * @return: array list of sentence stisfication answers for each rule
 */
	public ArrayList<SentSat> evalOneByOne(LogicProgram prog, ArrayList<ArrayList<myTerm>> labels,
				ArrayList<Sentence> sents) {
		// if all rules are set, clear rule and prolog engine
		if (rules.size() > 0) {
			retractRules();
		} else {
			System.out.println("Please set up rules first!");
			return null;
		}
		
		ArrayList<SentSat> ans_by_rules = new ArrayList<SentSat>();
		for (Formula f : prog.getRules()) {
			this.setRule(f);
			
			SentSat ans = new SentSat();
			for (int i = 0; i < sents.size(); i++) {
				SatisfySamples tmp_sat = evalSentSat(labels.get(i), sents.get(i));
				
				/*
				 * TODO SET PROBABILITY for ans
				 */
				ans.addSentSat(labels.get(i), sents.get(i), tmp_sat);
			}

			ans_by_rules.add(ans);
			this.retractRules();
		}
		
		/*
		 * TODO PUT ALL TOGETHER
		 */
		return ans_by_rules;
	}
}
