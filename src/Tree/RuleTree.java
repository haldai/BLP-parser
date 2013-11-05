/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import ILP.Prolog;
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
		root = create(label, data, cand);
	}

	
	/**
	 * main procedure for creating a tree node
	 * @data is the data for training a tree
	 * @candidateTerms are the candidate terms for a tree 
	 */
	
	public TreeNode create(ArrayList<ArrayList<myTerm>> label, ArrayList<Sentence> data, ArrayList<myTerm> candidateTerms) {
		TreeNode node = new TreeNode();
		// TODO return
		// TODO else split current node
		ArrayList<myTerm> availTerms = getAvailTerms(node, candidateTerms); // get available terms to add
		// TODO use ILP coverage
		double maxP = 0.0, minN = 0.0; // covered positive & covered negative
		
		for (myTerm t : availTerms) {
			
		}
		
		return node;
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
	private ArrayList<myTerm> buildFeature(ArrayList<myWord> words, ArrayList<myWord> vars) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		if (words.size() == vars.size()) {
			for (int i = 0; i < words.size(); i++) {
				myTerm tmp_term = new CommonPredicates().prologEqual(words.get(i), vars.get(i));
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