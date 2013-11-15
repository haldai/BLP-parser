/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.lang.Math;

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
		Data data = new Data(doc);
		root = create(data, cand, null, true);
	}

	
	/**
	 * main procedure for creating a tree node
	 * @data is the data for training a tree
	 * @candidateTerms are the candidate terms for a tree 
	 */
	
	public TreeNode create(Data d, ArrayList<myTerm> candidateTerms, TreeNode father, boolean branch) {
		/*
		 * START
		 */
		ArrayList<ArrayList<myTerm>> label = d.getLabels();
		ArrayList<Sentence> data = d.getSents();
		TreeNode node = new TreeNode();
		node.setFather(father);
		
		if (father != null) {
			node.setHierarchy(father.getHierarchy() + 1); // set hierarchy of 1
			father.setIsLeaf(false);
		}
		
		// get available terms to add
		ArrayList<myTerm> availTerms = getAvailTerms(node, candidateTerms); 
		
		if (branch)
			node.setBranchPositive();
		else
			node.setBranchNegative();
		
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
							
							/*
							 * ADD UNEQUAL VARS INTO THE NODE
							 */
							ArrayList<myTerm> uneq = buildUnequalFeature(getAllVars(tmp_root));
							for (myTerm t : uneq) {
								if (!tmp_root.contains(t))
										tmp_root.add(t);
							}
							root_f.pushBody(tmp_root);
							root_f.pushHead(head);
							// all covered data
//							if ((tmp_root.size() == 1) && (tmp_root.get(0).getPred().toString().equals("adv/2")))
//								System.out.println(tmp_root.get(0).toString());
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
			// finally use the maximum accuracy root
			// add negative sample paths into candidate
			candidateTerms = addTermFromNegSamps(max_root_cov, candidateTerms);
//			for (int i = 0; i < max_root_cov.getAllNegNum(); i ++) {
//				myTerm t = max_root_cov.getAllNeg(i);
//				ArrayList<myTerm> neo_cand = candFromSamps(t, max_root_cov.getCovSentFromTerm(t));
//				for (myTerm tt : neo_cand) {
//					if (!candidateTerms.contains(tt))
//						candidateTerms.add(tt);
//				}
//			}
//			System.out.println("all candidate terms after add negative examples");
//			for (myTerm t : candidateTerms) {
//				System.out.println(t.toPrologString());
//			}
			/*
			 *  FINISHING ROOT NODE BUILDING
			 */
			node.addTermNodes(max_root_terms); // set splitting critira
			node.setHierarchy(1); // root is first layer
			node.setBranchPositive();
			node.setSentSat(true, max_root_cov);
			// remove all used candidate terms
			for (myTerm tmp_term : node.getTermNodes()) {
				candidateTerms.remove(tmp_term);
			}
			// create childrens
//			node.setFalseChild(create(max_root_cov.getUncoveredData(), candidateTerms, node, false));
			// FIRST TERM IN PROLOG RULE MUST BE TRUE!!!
			node.setTrueChild(create(max_root_cov.getCoveredData(), candidateTerms, node, true));
			node.setFalseChild(null);
//			System.out.println("=========================\nCOVERED INSTANCES\n==========================");
//			System.out.println(max_root_cov.getCoveredData());
//			System.out.println("=======================\nUNCOVERED INSTANCES\n==========================");
//			System.out.println(max_root_cov.getUncoveredData());
//			System.exit(0);
			return node;
		}
		else {
			/*
			 * NOT ROOT NODE
			 */
			// TODO should be put into a procedure
			// if father has enough layer or accuracy, return
			if ((node.getHierarchy() > utils.MAX_HIERARCHY_NUM)) {
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
				System.out.println(father.getSentSat(branch).getAccuracy() + "::" + form.toString());
				return node;
			} else if(father.getSentSat(branch).getAccuracy() > utils.MAX_ACC_CRI) {
				// TODO father's accuracy is enough for a positivesample
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
				System.out.println(father.getSentSat(branch).getAccuracy() + "::" + form.toString());
				return node;
			} else if(father.getSentSat(branch).getAccuracy() < utils.MAX_INACC_CRI) {
				// TODO father's accuracy is enough for a negative sample
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
				System.out.println(father.getSentSat(branch).getAccuracy() + "::" + form.toString());
				return node;
			} else {
				// TODO else split current node
				// TODO use ILP coverage
				double maxGain = -100.0; // covered positive & covered negative
				myTerm max_gain_term = new myTerm();
				ArrayList<myTerm> max_form_body = new ArrayList<myTerm>();
				ArrayList<myTerm> no_improve_terms = new ArrayList<myTerm>();
				SentSat maxPosSat = new SentSat();
				SentSat maxNegSat = new SentSat();
				Formula cur_form = toFormula(father, branch);
				System.out.println("=================cur_form===================");
				System.out.println(cur_form.toPrologString());
				System.out.println("=================cur_form===================");
				for (myTerm t : availTerms) {
					// add t to body
					t.setPositive();
					cur_form.pushBody(t);
//					System.out.println(cur_form.toString());
					// compute criterion and pop
					SentSat PosSat = getSatSamps(cur_form, label, data);
					PosSat.setTotal();
//					double tmp_acc = computeAccuracy(sat);
					double pos_foilgain = foilGain(PosSat, node.getFather().getSentSat(branch));
//					System.out.println(sat.getCov() + " / " + tmp_acc + ": " + cur_form.toString());	
					System.out.println(pos_foilgain + ": " + cur_form.toString());	
					cur_form.popBody();
					// Try falsed term
					t.setNegative();
					cur_form.pushBody(t);
//					System.out.println(cur_form.toString());	
					// TODO compute criterion and pop
					SentSat NegSat = getSatSamps(cur_form, label, data);
					NegSat.setTotal();
//					tmp_acc = computeAccuracy(sat);
					double neg_foilgain = foilGain(NegSat, node.getFather().getSentSat(branch));
//					System.out.println(sat.getCov() + " / " + tmp_acc + ": " + cur_form.toString());	
					System.out.println(neg_foilgain + ": " + cur_form.toString());	

					/*
					 * retain the best
					 */
					if (maxGain <= Math.abs(pos_foilgain - neg_foilgain)) {
						maxGain = Math.abs(pos_foilgain - neg_foilgain);
						maxPosSat = PosSat;
						maxNegSat = NegSat;
						t.setPositive();
						max_gain_term = t;
						max_form_body = cur_form.getBody(); 
					}
					if (Math.abs(pos_foilgain - neg_foilgain) <= 0.0)
						no_improve_terms.add(t);
					cur_form.popBody();
				}
//				System.out.println(maxGain + ": " + max_gain_term.toPrologString());
				/*
				 * ADD NEW TERM AS NODE
				 * 1. create node;
				 * 2. remove candidate node;
				 * 3. create node's children.
				 */
				node.addTermNodes(max_gain_term);
				// add unequality constraints
				ArrayList<myTerm> uneq = buildUnequalFeature(getAllVars(max_form_body));
				for (myTerm t : uneq) {
					if (!max_form_body.contains(t))
							node.addTermNodes(t);
				}
				// store satisfication info
				node.setSentSat(true, maxPosSat);
				node.setSentSat(false, maxNegSat);
				// deal with candidate forms
				// remove t
				
				// add negative candidates
//				candidateTerms.removeAll(no_improve_terms);
				@SuppressWarnings("unchecked")
				ArrayList<myTerm> pos_candidateTerms = (ArrayList<myTerm>) candidateTerms.clone();
				@SuppressWarnings("unchecked")
				ArrayList<myTerm> neg_candidateTerms = (ArrayList<myTerm>) candidateTerms.clone();
				pos_candidateTerms = addTermFromNegSamps(maxPosSat, pos_candidateTerms);
				neg_candidateTerms = addTermFromNegSamps(maxNegSat, neg_candidateTerms);
				for (myTerm t : node.getTermNodes()) {
					pos_candidateTerms.remove(t);
					neg_candidateTerms.remove(t);
				}
				pos_candidateTerms.removeAll(no_improve_terms);
				neg_candidateTerms.removeAll(no_improve_terms);
				// create children
				node.setFalseChild(create(maxNegSat.covered, pos_candidateTerms, node, false));
				node.setTrueChild(create(maxPosSat.covered, neg_candidateTerms, node, true));
				return node;
			}
		}
	}
	/**
	 * add features extracted from negative samples
	 * @param sat: satisfaction info of node, contains negative samples
	 * @param cand: candidate terms
	 */
	private ArrayList<myTerm> addTermFromNegSamps(SentSat sat, ArrayList<myTerm> cand) {
		for (int i = 0; i < sat.getAllNegNum(); i ++) {
			myTerm t = sat.getAllNeg(i);
			ArrayList<myTerm> neo_cand = candFromSamps(t, sat.getCovSentFromTerm(t));
			for (myTerm tt : neo_cand) {
				if (!cand.contains(tt))
					cand.add(tt);
			}
		}
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
		double t = (double) p + n;
		if (t == 0)
			t = 0.000000000000001;
		return (double) p/t;
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
			boolean found = false;
			if (!appVars.isEmpty()) {
				for (myWord w : t.getArgs()) {
					if (w.isVar()) {
						for (String s : appVars) {
							if (s.equals(w.toPrologString())) {
								found = true;
								break;
							}
						}
					}
					if (!found)
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
	@SuppressWarnings("unchecked")
	private Formula toFormula(TreeNode node, boolean son_branch) {
		Formula re = new Formula();
		re.pushHead(head);
		LinkedList<myTerm> fa = (LinkedList<myTerm>) node.toTerms().clone();
		if (!son_branch)
			for (myTerm t : fa) {
				t.flip();
			}
		re.pushBodyToFirst(fa);
		while (node.getFather() != null) {
			TreeNode tmp_father_node = node.getFather();
			fa = (LinkedList<myTerm>) tmp_father_node.toTerms().clone();
			if (!node.isPositiveBranch()) {
				for (myTerm t : fa)
					t.flip();
			}
			re.pushBodyToFirst(fa);
			node = tmp_father_node;
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
	 * find all variables in a list of terms
	 * @param terms
	 * @return
	 */
	private ArrayList<myWord> getAllVars(ArrayList<myTerm> terms) {
		ArrayList<myWord> re = new ArrayList<myWord>();
		for (myTerm t : terms) {
			for (myWord arg : t.getArgs()) {
				if (!re.contains(arg) && arg.isVar())
					re.add(arg);
			}
		}
		return re;
	}
	/**
	 * add unequal constraints
	 * @param vars
	 * @return
	 */
	private ArrayList<myTerm> buildUnequalFeature(ArrayList<myWord> vars) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		for (int i = 0; i < vars.size(); i++)
			for (int j = i + 1; j < vars.size(); j++) {
				if (!vars.get(i).equals(vars.get(j))) {
					myTerm tmp_term = new CommonPredicates().prologEqual(vars.get(i), vars.get(j));
					tmp_term.setNegative();
					re.add(tmp_term);
				}
			}
		return re;
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
				if (!re.contains(tmp_term))
					re.add(tmp_term);
				tmp_term = new CommonPredicates().posTag(vars.get(i), words.get(i).toPostagWord());
//				re.add(tmp_neg_term);
				if (!re.contains(tmp_term))
					re.add(tmp_term);
			}
		} else {
			System.out.println("Number of words and number of variables does not meet");
		}
		return re;
	}
	
	private ArrayList<myTerm> candFromSamps(myTerm negSamp, Sentence sent) {
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
	 * Gain(R0, R1) := t * ( log2(p1/(p1+n1)) - log2(p0/(p0+n0)) ).
	 * R0 denotes a rule before adding a new literal.
	 * R1 is an extesion of R0.
	 * p0 denotes the number of positive tupels, covered by R0,
	 * p1 the number of positive tupels, covered by R1.
	 * n0 and n1 are the number of negative tupels, covered by the according rule.
	 * t is the number of positive tupels, covered by R0 as well as by R1.
	 * @param r1
	 * @param r0
	 * @return
	 */
	private double foilGain(SentSat r1, SentSat r0) {
		@SuppressWarnings("unchecked")
		ArrayList<myTerm> r1_all_pos = (ArrayList<myTerm>) r1.getAllPos().clone();
		@SuppressWarnings("unchecked")
		ArrayList<myTerm> r0_all_pos = (ArrayList<myTerm>) r0.getAllPos().clone();
		r0_all_pos.retainAll(r1_all_pos);
		int t = r0_all_pos.size();
		double acc1 = computeAccuracy(r1);
		if (acc1 == 0.0)
			acc1 = 0.000000000000001;
		double acc0 = computeAccuracy(r0);
		if (acc0 == 0.0)
			acc0 = 0.000000000000001;
		double re = (double) t*(Math.log(acc1) - Math.log(acc0));
		return re;
	}
	
	private double splitInfo(SentSat sat) {
		double p = sat.getCov();
		if (p == 0)
				p = 0.000000000000001;
		return (double) -(p*Math.log(p) + (1-p)*Math.log(1-p));
	}
}
