/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

import utils.*;
import ILP.*;
import Logic.*;

/**
 * @author Wang-Zhou
 *
 */
public class RuleTree {

	/**
	 * translate sentence and formula into decision tree and vice versa.
	 * 
	 * in logic domain, adding one term may not affect the classification
	 * distribution, so if any term cannot affect the distribution, just add
	 * the default term (e.g., contains certain variable or just in turn).
	 * 
	 * [optimization] if the variable was not introduced in before terms, its feature cannot
	 * be added
	 * 
	 * [optimization] must contain variable in head, so first add these two terms
	 */
	
	TreeNode root; // tree root - the first splitting node;
	myTerm head; // logical term - head:-body., must have variable
	LinkedList<String> rules = new LinkedList<String>();
	Prolog prolog;
	private int maxHeight;
	
	public RuleTree(Prolog p) {
		root = null;
		head = null;
		prolog = p;
	}
		
	public myTerm getHead() {
		return head;
	}
	
	public void setHead(myTerm h) {
		head = h;
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode r) {
		root = r;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public void setMaxHeight(int h) {
		maxHeight = h;
	}
	
	public LinkedList<String> getPrologRules() {
		return rules;
	}
	/**
	 * build a tree from one path
	 * @param doc: training instances in document
	 * @param path: path of available terms for split
	 */
	public void buildTree(Document doc, myTerm head, LinkedList<myTerm> path) {
		ArrayList<Sentence> data = new ArrayList<Sentence>(Arrays.asList(doc.getSentences())); // data
		ArrayList<ArrayList<myTerm>> label = doc.getLabels(); // labels
		ArrayList<myTerm> cand = new ArrayList<myTerm>(); // candidate terms
		// substitution and get more features (temporarily only use words themselves)
		ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
		all_terms.add(head);
		all_terms.addAll(path);
		Substitute subs = new Substitute(all_terms);
		ArrayList<myTerm> all_sub_terms = subs.getSubTerms();
		ArrayList<myWord> word_list = subs.getWordList();
		ArrayList<myWord> var_list = subs.getVarList();
		// set head term
		this.setHead(all_sub_terms.get(0));
		all_sub_terms.remove(0);
		// add path as candidate terms, then build more feature as candidate terms
		cand.addAll(all_sub_terms);
		ArrayList<myTerm> feature = buildFeature(var_list, word_list);
		cand.addAll(feature);
		System.out.println("candidate terms:");
		for (myTerm t : cand) {
			System.out.println(t.toPrologString());
		}
		root = create(label, data, cand, null, true);
	}

	
	/**
	 * main procedure for creating a tree node
	 * @data is the data for training a tree
	 * @candidateTerms are the candidate terms for a tree 
	 */
	
	public TreeNode create(ArrayList<ArrayList<myTerm>> label, ArrayList<Sentence> data,
			 ArrayList<myTerm> candidateTerms, TreeNode father, boolean branch) {
		/*
		 * START
		 */
		TreeNode node = new TreeNode();
		node.setFather(father);
		if (father != null)
			node.setHierarchy(father.getHierarchy() + 1); // set hierarchy of 1
		
		// get available terms to add
		ArrayList<myTerm> availTerms = getAvailTerms(node, candidateTerms); 
		
		// TODO should be put into a procedure
		if (father == null) {
			/*
			 * BUILD ROOT NODE
			 * if no father, this node is root, add terms that includes head variables
			 */
			// get candidate for root node
			myWord[] headArgs = head.getArgs();
			ArrayList<ArrayList<myTerm>> rootCand = new ArrayList<ArrayList<myTerm>>(headArgs.length);
			for (int i = 0; i < headArgs.length; i++) {
				rootCand.add(new ArrayList<myTerm>());
			}
			for (myTerm t : availTerms) {
				boolean is_feat = false;
				for (myWord w : t.getArgs())
					if (!w.isVar())
						is_feat = true;
				if (is_feat)
					continue;
				for (myWord w : t.getArgs()) {
					for (int i = 0; i < headArgs.length; i++) {
						myWord arg = headArgs[i];
						if (arg.equals(w)) {
							rootCand.get(i).add(t);
							break;
						}
					}
				}
			}
			// enumerate the best combination(covers most positive and least negative) of head
			// (Foil gain: |p|*[log2(|p|/(|p|+|n|)) – log2(|P|/(|P|+|N|))])
			// use DFS to enumerate
			ArrayList<myTerm> max_root_terms = new ArrayList<myTerm>();
			SentSat max_root_cov = new SentSat();
			double max_root_acc = -1.0;
			for (int j = 0; j < rootCand.get(0).size(); j++) {
				Stack<Tuple<Integer, Integer>> S = new Stack<Tuple<Integer, Integer>>();
				ArrayList<Tuple<Integer, Integer>> visited = new ArrayList<Tuple<Integer, Integer>>();
				ArrayList<Tuple<Integer, Integer>> route = new ArrayList<Tuple<Integer, Integer>>();
				Tuple<Integer, Integer> tup = new Tuple<Integer, Integer>(0, j);
				S.push(tup);
				while (!S.isEmpty()) {
					Tuple<Integer, Integer> u = S.pop();
					if (!visited.contains(u)) {
						visited.add(u);
						route.add(u);
						// For each neighbor of u (only connected to next layer)
						if (u.x == rootCand.size() - 1) {
							// reach the bottom, forms a temp rootTerm
							ArrayList<myTerm> tmp_root = new ArrayList<myTerm>();
							for (Tuple<Integer, Integer> t : route) {
								myTerm tmp_t = rootCand.get(t.x).get(t.y);
								if (!tmp_root.contains(tmp_t)) {
									tmp_root.add(tmp_t);
								}
							}
							// build temporary formula
							Formula root_f = new Formula();
							root_f.pushBody(tmp_root);
							root_f.pushHead(head);
							// all covered data
							SentSat sat = getSatSamps(root_f, label, data);
							sat.setTotal();
							// compute accuracy
							double tmp_root_acc = computeAccuracy(sat);
							if (tmp_root_acc > max_root_acc) {
								max_root_acc = tmp_root_acc;
								max_root_terms = tmp_root;
								max_root_cov = sat;
								System.out.println("=======");
								System.out.println(sat.getCov() + " / " + tmp_root_acc + ": " + root_f.toString());	
								System.out.println("=======");
							}
							visited.remove(u); // pop visited
							if (!S.isEmpty() && (S.lastElement().x < route.get(route.size() - 1).x))
								route.remove(visited.get(visited.size() - 1));
							route.remove(u);
						} else {
							for (int i = 0; i < rootCand.get(u.x + 1).size(); i++) {
								S.push(new Tuple<Integer, Integer>(u.x + 1, i));
							}
						}
					}
				}
			}
			// finally get the maximum accuracy root; add negative sample paths into candidate
			for (int i = 0; i < max_root_cov.getAllNegNum(); i ++) {
				myTerm t = max_root_cov.getAllNeg(i);
				ArrayList<myTerm> neg_cand = candFromNegSamps(t, max_root_cov.getWhichSent(t));
				for (myTerm tt : neg_cand) {
					if (!candidateTerms.contains(tt))
						candidateTerms.add(tt);
				}
			}
			System.out.println("all candidate terms after add negative examples");
			for (myTerm t : candidateTerms) {
				System.out.println(t.toPrologString());
			}
			/*
			 *  FINISHING ROOT NODE BUILDING
			 */
			node.setTermNodes(max_root_terms); // set splitting critira
			node.setHierarchy(1); // root is first layer
			node.setBranchPositive();
			// remove all used candidate terms
			for (myTerm tmp_term : node.getTermNodes()) {
				candidateTerms.remove(tmp_term);
			}
			// create childrens
			node.setFalseChild(create(max_root_cov.getAllLables(), max_root_cov.getAllSents(), candidateTerms, node, true));
			node.setTrueChild(create(max_root_cov.getAllLables(), max_root_cov.getAllSents(), candidateTerms, node, false));
			
			return node;
		}
		else {
			/*
			 * NOT ROOT NODE
			 */
			// TODO should be put into a procedure
			// TODO if father has enough layer or accuracy, return
			if (node.getHierarchy() > utils.MAX_HIERARCHY_NUM) {
				node.getFather().setIsLeaf(true);
				return null;
			} else {
				// TODO else split current node
				// TODO use ILP coverage
				double maxAcc = 0.0, maxCov = 0.0; // covered positive & covered negative
				
				for (myTerm t : availTerms) {
					
				}
				return node;
			}
		}
	}
	
	private ArrayList<myTerm> candFromNegSamps(myTerm negSamp, Sentence sent) {
		HyperGraph graph = new HyperGraph();
 	   	myTerm[] terms = sent.getTerms();
 	   	myWord[] words = sent.getWords();
 	   	for (myWord word : words) {
 	   		graph.addHyperVertex(word);
 	   	}
 	   	
 	   	for (myTerm term : terms) {
 	   		graph.addHyperEdge(term);
 	   	}
 	   	HyperVertex start = new HyperVertex(negSamp.getArg(0));
 	   	HyperVertex end = new HyperVertex(negSamp.getArg(1));
 	   	HyperPathFind pf = new HyperPathFind(graph, start, end);
 	   	LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
 	   	pf.Search(visitedEdges);
 	 	// place substitution
 	   	ArrayList<myTerm> cand = new ArrayList<myTerm>(); // candidate terms
		// substitution and get more features (temporarily only use words themselves)
 	   	for (LinkedList<myTerm> path : pf.getPaths()) {
 	   		ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
 	   		all_terms.add(negSamp);
 	   		all_terms.addAll(path);
 	   		Substitute subs = new Substitute(all_terms);
 	   		ArrayList<myTerm> all_sub_terms = subs.getSubTerms();
 	   		ArrayList<myWord> word_list = subs.getWordList();
 	   		ArrayList<myWord> var_list = subs.getVarList();
 	   		ArrayList<myTerm> feature = buildFeature(var_list, word_list);
 			// set head term
 			this.setHead(all_sub_terms.get(0));
 			all_sub_terms.remove(0);
 			// add path as candidate terms, then build more feature as candidate terms
 			cand.addAll(feature);
 	   	}
//		for (myTerm t : cand) {
//			System.out.println(t.toPrologString());
//		}
 	   	return cand;
	}
	/**
	 * given a formula and a set of instances with label, compute the accuracy
	 * @param f: formula
	 * @param label: list of labels
	 * @param data: list of instances
	 * @return: satisfy samples (including negative samples)
	 */
	private SentSat getSatSamps(Formula f, ArrayList<ArrayList<myTerm>> label, ArrayList<Sentence> data) {
		Eval eval = new Eval(prolog);
		eval.setRule(f);
		SentSat sat = eval.evalAllSat(label, data);
		// IMPORTANT! must retract all rules
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sat;
	}
	/**
	 * given a formula and a doc, compute the accuracy
	 * @param f: formula
	 * @param doc: document
	 * @return: satisfy samples (including negative samples)
	 */
	private SentSat getDocSatSamps(Formula f, Document doc) {
		Eval eval = new Eval(prolog);
		eval.setRule(f);
		SentSat sat = eval.evalDocSat(doc);
		// IMPORTANT! must retract all rules
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sat;
	}
	/**
	 * given satisfy samples then compute accuracy
	 * @return: accuracy
	 */
	private double computeAccuracy(SentSat sat) {
		int p = sat.getAllPosNum();
		int n = sat.getAllNegNum();
		return (double) p/(p+n);
	}

	/**
	 * compute available terms
	 * @node the node to build
	 * @candidateTerms a candidate set for choosing
	 */
	private ArrayList<myTerm> getAvailTerms(TreeNode node, ArrayList<myTerm> candidateTerms) {
		// TODO debug
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<String> appVars = new ArrayList<String>();
		ArrayList<myTerm> historyTerms = node.getAncestorNodes();
		for (myTerm t : historyTerms) {
			for (myWord w : t.getArgs()) {
				if ((w.isVar()) && !appVars.contains(w.toPrologString())) {
					appVars.add(w.toPrologString());					
				}
			}
		}
		for (myTerm t : candidateTerms) {
			boolean banned = false;
			for (myWord w : t.getArgs()) {
				for (String s : appVars) {
					if (s.equals(w.toPrologString()))
						banned = true;
				}
			}
			if (!banned)
				re.add(t);
		}
		return re;
	}
	/**
	 * return a formula from a node to its root
	 * @param node
	 * @return
	 */
	private Formula toFormula(TreeNode node) {
		Formula re = new Formula();
		re.pushHead(head);
		re.pushbody(node.toTerms());
		while (node.getFather() != null) {
			re.pushbody(node.getFather().toTerms());
		}
		return re;
	}
	
	/**
	 * get Prolog rule string from current tree
	 * @return string as prolog rule
	 */
	public void toPrologRules() {
		rules = null;
		LinkedList<myTerm> re = new LinkedList<myTerm>();
		visit(root, re);
	}
	/**
	 * visit current node and trace the prolog rule until meet leaf node
	 * @param current: current node to be visited
	 * @param visited: visited terms
	 */
	private void visit(TreeNode current, LinkedList<myTerm> visited) {
		if (current.isLeaf()) {
			for (myTerm t : current.toTerms()) {
				t.setPositive();
				visited.add(t);
			}
			rules.add(returnPrologRule(visited));
			for (myTerm t : current.toTerms()) {
				visited.removeLast();
			}

		} else {
			// visit True child, push positive current node terms
			for (myTerm t : current.toTerms()) {
				t.setPositive();
				visited.add(t);
			}
			visit(current.getTrueChild(), visited);
			for (myTerm t : current.toTerms()) {
				visited.removeLast();
			}

			// visit False child, push negative current node terms
			for (myTerm t : current.toTerms()) {
				t.setNegative();
				visited.add(t);
			}
			visit(current.getFalseChild(), visited);
			for (myTerm t : current.toTerms()) {
				visited.removeLast();
			}
		}
	}
	
	/**
	 * return a prolog rule from visited terms stack
	 * @param visited: stack for visited terms
	 * @return
	 */
	private String returnPrologRule(LinkedList<myTerm> visited) {
		String re_rule = "";
		for (myTerm t : visited) {
			re_rule = re_rule + t.toPrologString();
		}
		return re_rule;
	}
	
	/**
	 * Add feature of each word in path
	 * @param words: words in path
	 * @param vars: variables that represent words
	 * @return: a list of terms as feature
	 */
	private ArrayList<myTerm> buildFeature(ArrayList<myWord> vars, ArrayList<myWord> words) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		if (words.size() == vars.size()) {
			for (int i = 0; i < words.size(); i++) {
				myTerm tmp_term = new CommonPredicates().prologEqual(vars.get(i), words.get(i).getZeroConst());
//				myTerm tmp_neg_term = new CommonPredicates().prologEqual(words.get(i), vars.get(i));
//				tmp_neg_term.setNegative();
				re.add(tmp_term);
//				re.add(tmp_neg_term);
			}
		} else {
			System.out.println("Number of words and number of variables does not meet");
		}
		return re;
	}
}
