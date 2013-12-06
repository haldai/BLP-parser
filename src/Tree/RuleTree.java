/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
	LinkedList<Formula> rules = new LinkedList<Formula>();
	Prolog prolog;
	ArrayList<Predicate> pred_list = new ArrayList<Predicate>();
	
	
	private int maxHeight;
	
	public RuleTree(Prolog p, ArrayList<Predicate> preds) {
		root = null;
		head = null;
		prolog = p;
		pred_list = preds;
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
	
	public LinkedList<Formula> getPrologRules() {
		return rules;
	}
	/**
	 * build a tree from one path
	 * @param doc: training instances in document
	 * @param path: path of available terms for split
	 */
	public void buildTree(Data data, myTerm head, LinkedList<myTerm> path, Sentence sent) {
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
		if (head.isPositive())
			this.head.setPositive();
		else
			this.head.setNegative();
		// debug
		all_sub_terms.remove(0);
		// add path as candidate terms, then build more feature as candidate terms
		cand.addAll(all_sub_terms);
		ArrayList<myTerm> feature = buildFeature(var_list, word_list, sent.getFeatures());
		cand.addAll(feature);
//		System.out.println("candidate terms:");
//		for (myTerm t : cand) {
//			System.out.println(t.toPrologString());
//		}
		root = create(data, cand, new ArrayList<myTerm>(), null, true);
	}

	
	/**
	 * main procedure for creating a tree node
	 * @data is the data for training a tree
	 * @candidateTerms are the candidate terms for a tree 
	 */
	
	
	
	public TreeNode create(Data d, ArrayList<myTerm> candidateTerms, ArrayList<myTerm> usedTerms, TreeNode father, boolean branch) {
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
		
		if (father == null) {
			// ROOT NODE
			return createRoot(label, data, candidateTerms, availTerms);
		} else {
			/*
			 * NOT ROOT NODE
			 */
			// if father has enough layer or accuracy, return
			if ((node.getHierarchy() > utils.MAX_HIERARCHY_NUM)) {
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
					form.setWeight(father.getSentSat(branch).getAccuracy());
				
				rules.add(form);
//				System.out.println(data.size() + "/" + form.toString());
				return node;
			} else if(father.getSentSat(branch).getAccuracy() >= utils.MAX_ACC_CRI) {
				// father's accuracy is enough for a positivesample
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
					form.setWeight(father.getSentSat(branch).getAccuracy());
				rules.add(form);
//				System.out.println(data.size() + "/"  + form.toString());
				return node;
			} else if(father.getSentSat(branch).getAccuracy() <= utils.MAX_INACC_CRI) {
				// father's accuracy is enough for a negative sample
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
					form.setWeight(father.getSentSat(branch).getAccuracy());
				rules.add(form);
//				System.out.println(data.size() + "/" + form.toString());
				return node;
			} else if (candidateTerms.isEmpty()) {
				// no candidates
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
					form.setWeight(father.getSentSat(branch).getAccuracy());
				rules.add(form);
//				System.out.println(data.size() + "/" + form.toString());
				return node;
			} else	{
				// else split current node
				double maxGain = -100.0; // covered positive & covered negative
				myTerm max_gain_term = new myTerm();
				ArrayList<myTerm> max_form_body = new ArrayList<myTerm>();
				ArrayList<myTerm> no_improve_terms = new ArrayList<myTerm>();
				SentSat maxPosSat = new SentSat();
				SentSat maxNegSat = new SentSat();
				Formula cur_form = toFormula(father, branch);
				ArrayList<myTerm> appeared = cur_form.getBody();
				
				availTerms = removeDup(availTerms, appeared);
//				System.out.println("=================cur_form===================");
//				System.out.println(cur_form.toString());
//				System.out.println("=================cur_form===================");
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
//					System.out.println(pos_foilgain + ": " + cur_form.toString());	
					cur_form.popBody();
					// Try falsed term
					t.setNegative();
					cur_form.pushBody(t);
//					System.out.println(cur_form.toString());	
					// compute criterion and pop
					SentSat NegSat = getSatSamps(cur_form, label, data);
					NegSat.setTotal();
//					tmp_acc = computeAccuracy(sat);
					double neg_foilgain = foilGain(NegSat, node.getFather().getSentSat(branch));
//					System.out.println(sat.getCov() + " / " + tmp_acc + ": " + cur_form.toString());	
//					System.out.println(neg_foilgain + ": " + cur_form.toString());	

					/*
					 * retain the best
					 */
					if (maxGain <= Math.abs(pos_foilgain - neg_foilgain)) {
						maxGain = Math.abs(pos_foilgain - neg_foilgain);
						maxPosSat = PosSat;
						maxNegSat = NegSat;
						max_gain_term = t.clone();
						max_gain_term.setPositive();
						max_form_body = new ArrayList<myTerm>();
						for (myTerm tmp_term : cur_form.getBody()) {
							max_form_body.add(tmp_term.clone());
						}
					}
					if (Math.abs(pos_foilgain - neg_foilgain) <= 0.0)
						no_improve_terms.add(t);
					cur_form.popBody();
				}
				if (maxGain <= 0.0) {
					node.setIsLeaf(true);
					Formula form = toFormula(father, branch);
//					if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//						myTerm h = form.getHead().get(0).clone();
//						h.setPositive();
//						form.getHead().clear();
//						form.pushHead(h);
//						form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//					} else
						form.setWeight(father.getSentSat(branch).getAccuracy());
					rules.add(form);
//					System.out.println(data.size() + "/"  + form.toString());
					return node;
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
				for (myTerm tmp_term : node.getTermNodes()) {
					usedTerms.add(tmp_term.clone());
				}
				
				ArrayList<myTerm> pos_candidateTerms = new ArrayList<myTerm>();
				for (myTerm tmp_term : candidateTerms) {
					pos_candidateTerms.add(tmp_term.clone());
				}
				ArrayList<myTerm> neg_candidateTerms = new ArrayList<myTerm>();
				for (myTerm tmp_term : candidateTerms) {
					neg_candidateTerms.add(tmp_term.clone());
				}
				pos_candidateTerms = addTermFromNegSamps(maxPosSat, pos_candidateTerms);
				neg_candidateTerms = addTermFromNegSamps(maxNegSat, neg_candidateTerms);
				
				// remove added nodes and useless nodes
				ArrayList<myTerm> tt = new ArrayList<myTerm>();
				for (myTerm tmp_term : pos_candidateTerms) {
					if (!(usedTerms.contains(tmp_term)) && !(no_improve_terms.contains(tmp_term)))
						tt.add(tmp_term.clone());
				}
				pos_candidateTerms = tt;
				tt = null;
				
				tt = new ArrayList<myTerm>();
				for (myTerm tmp_term : neg_candidateTerms) {
					if (!(usedTerms.contains(tmp_term)) && !(no_improve_terms.contains(tmp_term)))
						tt.add(tmp_term.clone());
				}
				neg_candidateTerms = tt;
				tt = null;
				
				// create children
				node.setFalseChild(create(maxNegSat.covered, pos_candidateTerms, usedTerms, node, false));
				node.setTrueChild(create(maxPosSat.covered, neg_candidateTerms, usedTerms, node, true));
				return node;
			}
		}
	}

	private ArrayList<myTerm> removeDup(ArrayList<myTerm> cands,
			ArrayList<myTerm> app) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<Predicate> app_P = new ArrayList<Predicate>();
		ArrayList<myWord> app_V = new ArrayList<myWord>();
		
		ArrayList<Predicate> dup_feat = new ArrayList<Predicate>();
		dup_feat.add(new Predicate("postag/2"));
		dup_feat.add(new Predicate("class/2"));
		dup_feat.add(new Predicate("spoc/2"));
		for (myTerm a : app) {
			if (a.isPositive() && dup_feat.contains(a.getPred())) {
				app_P.add(a.getPred());
				app_V.add(a.getArg(1));
			}
		}
		
		for (myTerm t : cands) {
			if (dup_feat.contains(t.getPred()) && (app_P.size() > 0)) {
				for (int i = 0; i < app_P.size(); i++) {
					if (!(t.getPred().equals(app_P.get(i)) && t.getArg(0).equals(app_V.get(i))))
						re.add(t.clone());
				}
			} else {
				re.add(t.clone());
			}
		}
		return re;
	}

	private TreeNode createRoot(ArrayList<ArrayList<myTerm>> label, ArrayList<Sentence> data, ArrayList<myTerm> candidateTerms, ArrayList<myTerm> availTerms) {
		/*
		 * BUILD ROOT NODE
		 * if no father, this node is root, add terms that includes head variables
		 */
		TreeNode node = new TreeNode();
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
//						if ((tmp_root.size() == 1) && (tmp_root.get(0).getPred().toString().equals("adv/2")))
//							System.out.println(tmp_root.get(0).toString());
						SentSat sat = getSatSamps(root_f, label, data);
						sat.setTotal();
						// compute accuracy
						double tmp_root_acc = computeAccuracy(sat);
						if (tmp_root_acc > max_root_acc) {
							max_root_acc = tmp_root_acc;
							max_root_terms = tmp_root;
							max_root_cov = sat;
//							System.out.println("=======");
//							System.out.println(sat.getCov() + " / " + tmp_root_acc + ": " + root_f.toString());	
//							System.out.println("=======");
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
		/*
		 *  FINISHING ROOT NODE BUILDING
		 */
		node.addTermNodes(max_root_terms); // set splitting critira
		node.setHierarchy(1); // root is first layer
		node.setBranchPositive();
		node.setSentSat(true, max_root_cov);
		ArrayList<myTerm> used = new ArrayList<myTerm>();
		// remove all used candidate terms
		ArrayList<myTerm> tt = new ArrayList<myTerm>();
		for (myTerm tmp_term : candidateTerms) {
			if (node.getTermNodes().contains(tmp_term)) {
				used.add(tmp_term);
			} else {
				tt.add(tmp_term.clone());
			}
		}
		
		candidateTerms = tt;
		// create childrens
//		node.setFalseChild(create(max_root_cov.getUncoveredData(), candidateTerms, node, false));
		// FIRST TERM IN PROLOG RULE MUST BE TRUE!!!

		node.setTrueChild(create(max_root_cov.getCoveredData(), candidateTerms, used, node, true));
		node.setFalseChild(null);
//		System.out.println("=========================\nCOVERED INSTANCES\n==========================");
//		System.out.println(max_root_cov.getCoveredData());
//		System.out.println("=======================\nUNCOVERED INSTANCES\n==========================");
//		System.out.println(max_root_cov.getUncoveredData());
//		System.exit(0);
		return node;
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
		Eval eval = new Eval(prolog, pred_list);
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
		Eval eval = new Eval(prolog, pred_list);
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
	private Formula toFormula(TreeNode node, boolean son_branch) {
		Formula re = new Formula();
		re.pushHead(head);
		// deep clone
		LinkedList<myTerm> fa = new LinkedList<myTerm>();
		for (myTerm t : node.toTerms()) {
			myTerm n_t = t.clone();
			if (!son_branch)
				n_t.flip();
			fa.add(n_t);
		}
		re.pushBodyToFirst(fa);
		while (node.getFather() != null) {
			TreeNode tmp_father_node = node.getFather();
			fa = new LinkedList<myTerm>();
			for (myTerm tmp_t : tmp_father_node.toTerms()) {
				fa.add(tmp_t.clone());
			}
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
	private ArrayList<myTerm> buildFeature(ArrayList<myWord> vars, ArrayList<myWord> words, ArrayList<myTerm> feat_list) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		if (words.size() == vars.size()) {
			myTerm tmp_term = new myTerm();
			for (int i = 0; i < words.size(); i++) {
				for (myTerm feat_term : feat_list) {
					if (feat_term.getArg(0).equals(words.get(i))) {
						tmp_term = feat_term.clone();
						tmp_term.setArg(0, vars.get(i));
						if (!re.contains(tmp_term))
							re.add(tmp_term);
					}
				}
//				tmp_term = new CommonPredicates().prologEqual(vars.get(i), words.get(i).getZeroConst());
//				myTerm tmp_neg_term = new CommonPredicates().prologEqual(words.get(i), vars.get(i));
//				tmp_neg_term.setNegative();
				
//				tmp_term = new CommonPredicates().posTag(vars.get(i), words.get(i).toPostagWord());
//				re.add(tmp_neg_term);
//				if (!re.contains(tmp_term))
//					re.add(tmp_term);
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
 	   		ArrayList<myTerm> feature = buildFeature(var_list, word_list, sent.getFeatures());
 			// set head term
// 			this.setHead(all_sub_terms.get(0));
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
		ArrayList<myTerm> r1_all_pos = new ArrayList<myTerm>();
		for (myTerm tmp_term : r1.getAllPos())
			r1_all_pos.add(tmp_term.clone());
		ArrayList<myTerm> r0_all_pos = new ArrayList<myTerm>();
		for (myTerm tmp_term : r0.getAllPos())
			r0_all_pos.add(tmp_term.clone());
		r0_all_pos.retainAll(r1_all_pos);
		int t = r0_all_pos.size();
		double acc1 = r1.getAccuracy();computeAccuracy(r1);
		if (acc1 == 0.0)
			acc1 = 0.000000000000001;
		double acc0 = r0.getAccuracy();computeAccuracy(r0);
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

	public SentSat evaluateThis(Data data) {
		// evaluate given data by current rules
		SentSat re = new SentSat();
		LogicProgram lp = new LogicProgram();
		lp.addRules(rules);
		// predicates
		
		Eval eval = new Eval(prolog, pred_list);
		eval.setRules(lp);
		
		// evaluate each rule in current tree one by one
//		ArrayList<SentSat> all_sat = eval.evalOneByOneSat(lp, data.getLabels(), data.getSents());
		
		// get the list of answer for each sentence by each rule
		ArrayList<ArrayList<LinkedList<myTerm>>> result_one_by_one = eval.evalOneByOne(lp, data.getSents());
		
		// merge all the results by averaging the probability
		ArrayList<ArrayList<myTerm>> merged_result = mergeProbResults(result_one_by_one);

		// Calculate accuracy from merged_result
		for (int k = 0; k < merged_result.size(); k++) {
			SatisfySamples tmp_sat = new SatisfySamples();
			tmp_sat.setSatisifySamplesProb(data.getLabel(k), new LinkedList<myTerm>(merged_result.get(k)));
			re.addSentSat(data.getLabel(k), data.getSent(k), tmp_sat);
		}
		re.setTotal();
		
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return re;
	}
	
	public SentSat yapEvaluateThis(Data data) throws IOException {
		// evaluate given data by current rules
		SentSat re = new SentSat();
		String tmp_dir = System.getProperty("user.dir") + "/tmp/";
		String tmp_rule = tmp_dir + "tmp_rules.pl";
		String tmp_sent = tmp_dir + "tmp_sent.pl";
		String tmp_query = tmp_dir + "tmp/tmp_query.pl";
		String tmp_ground_prog = tmp_dir + "tmp_ground_prog.pl";
		String tmp_evidence = tmp_dir + "tmp_evidence.pl";
		String tmp_ground_query = tmp_dir + "tmp_ground_query.pl";
		
		String yap_cmd = String.format("yap -L /home/daiwz/Projects/problog2/assist/ground_hack.pl -- %s %s %s %s %s %s",
				tmp_rule, tmp_query, tmp_sent, tmp_ground_prog, tmp_evidence, tmp_ground_query);
		
		// write problog file
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tmp_rule));
		BufferedWriter frule = new BufferedWriter(osw);
		for (Formula f : this.rules) {
			frule.write(f.toPrologString() + '\n');
		}
		frule.close();
		
		// for each sentence, call yap to compute
		for (Sentence sent : data.getSents()) {
			osw = new OutputStreamWriter(new FileOutputStream(tmp_sent));
			BufferedWriter fsent = new BufferedWriter(osw);
			for (myTerm t : sent.getTerms())
				fsent.write(t.toString() + ".\n");
			for (myTerm t : sent.getFeatures())
				fsent.write(t.toString() + ".\n");
			fsent.close();
			String[] cmds = { "/bin/sh", "-c", new String(yap_cmd.getBytes(), "utf-8") };
			try {
				Process ps = Runtime.getRuntime().exec(cmds);
				System.out.print(loadStream(ps.getInputStream()));
				System.err.print(loadStream(ps.getErrorStream()));
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
				
		// evaluate each rule in current tree one by one
//		ArrayList<SentSat> all_sat = eval.evalOneByOneSat(lp, data.getLabels(), data.getSents());
		
		// get the list of answer for each sentence by each rule
//		ArrayList<ArrayList<LinkedList<myTerm>>> result_one_by_one = eval.evalOneByOne(lp, data.getSents());
		
		// merge all the results by averaging the probability
//		ArrayList<ArrayList<myTerm>> merged_result = mergeProbResults(result_one_by_one);

		// Calculate accuracy from merged_result
//		for (int k = 0; k < merged_result.size(); k++) {
//			SatisfySamples tmp_sat = new SatisfySamples();
//			tmp_sat.setSatisifySamplesProb(data.getLabel(k), new LinkedList<myTerm>(merged_result.get(k)));
//			re.addSentSat(data.getLabel(k), data.getSent(k), tmp_sat);
//		}
//		re.setTotal();
		
		return re;
	}
	/**
	 * Merge results of different probabilistic rules
	 * @param results
	 * @return: merged results, with probability
	 */
	private ArrayList<ArrayList<myTerm>> mergeProbResults(
			ArrayList<ArrayList<LinkedList<myTerm>>> results) {
		int rule_num = results.size();
		int sent_num = results.get(0).size();
		
		ArrayList<ArrayList<myTerm>> re = new ArrayList<ArrayList<myTerm>>(sent_num);
		
		for (int j = 0; j < sent_num; j++) {
			ArrayList<myTerm> tmp_ans_list = new ArrayList<myTerm>();
			ArrayList<Integer> tmp_ans_list_cnt = new ArrayList<Integer>();
			
			for (int i = 0; i < rule_num; i++) {
				LinkedList<myTerm> tmp_result_list = results.get(i).get(j);
				for (myTerm t : tmp_result_list) {
					if (tmp_ans_list.contains(t)) {
						int idx = tmp_ans_list.indexOf(t);
						
						myTerm the_t = tmp_ans_list.get(idx); // the term already in ans_list
						
						if (t.isPositive() == the_t.isPositive())
							the_t.setWeight((double) (the_t.getWeight() + t.getWeight()));
						else {
							the_t.setWeight((double) (the_t.getWeight() + (1 - t.getWeight())));
						}
						
						tmp_ans_list_cnt.set(idx, tmp_ans_list_cnt.get(idx) + 1); // record the counts
					} else {
						tmp_ans_list.add(t);
						tmp_ans_list_cnt.add(1);
					}
				}
			}
			
			for (int k = 0; k < tmp_ans_list.size(); k++) {
				tmp_ans_list.get(k).setWeight((double) tmp_ans_list.get(k).getWeight()/tmp_ans_list_cnt.get(k));
			}
			
			re.add(tmp_ans_list);
		}
		return re;
	}

	static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		in = new BufferedInputStream(in);
		StringBuffer buffer = new StringBuffer();
		while( (ptr = in.read()) != -1 ) {
			buffer.append((char)ptr);
		}
		return buffer.toString();
	}
}
